package co.edu.icesi.student360.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.edu.icesi.student360.common.identity.IdentityHeaders;
import co.edu.icesi.student360.common.logging.Correlation;
import co.edu.icesi.student360.common.security.ServiceTokenProvider;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Phase gate 3: a student fetches their own data; another student's data → 403 with a DENIED audit
 * record; staff read with STAFF_ROLE; calls without a service token never reach the domain.
 */
@SpringBootTest(
    properties = {
      "CORE_DB_PASSWORD=unused-overridden-by-testcontainers",
      "SERVICE_TOKEN_SECRET=0123456789abcdef0123456789abcdef-test-only"
    })
@AutoConfigureMockMvc
@Testcontainers
class StudentRecordsIntegrationTest {

  @Container @ServiceConnection
  static final PostgreSQLContainer<?> POSTGRES =
      new PostgreSQLContainer<>("postgres:16").withInitScript("db/test-init.sql");

  private static final UUID ANA = UUID.fromString("11111111-1111-1111-1111-000000001001");
  private static final UUID CARLOS = UUID.fromString("22222222-2222-2222-2222-000000002001");

  @Autowired private MockMvc mockMvc;
  @Autowired private JdbcTemplate jdbc;
  @Autowired private ServiceTokenProvider tokens;

  @BeforeEach
  void cleanAuditTrail() {
    jdbc.update("DELETE FROM audit.audit_record");
  }

  @Test
  void shouldReturnOwnFinancialStatusAndAuditWithSelfBasis() throws Exception {
    mockMvc
        .perform(
            as(
                ANA,
                "STUDENT",
                "S-1001",
                get("/api/core/students/S-1001/financial-status"),
                "gate3-self"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.studentId").value("S-1001"))
        .andExpect(jsonPath("$.overdue").value(false))
        .andExpect(jsonPath("$.financialHold").value(false));

    Map<String, Object> record = single();
    assertThat(record)
        .containsEntry("action", "READ_FINANCIAL_STATUS")
        .containsEntry("record_type", "DATA_ACCESS")
        .containsEntry("subject_type", "STUDENT")
        .containsEntry("subject_id", "S-1001")
        .containsEntry("authorization_basis", "SELF")
        .containsEntry("outcome", "ALLOWED")
        .containsEntry("actor_id", ANA)
        .containsEntry("request_id", "gate3-self")
        .containsEntry("service_name", "core-service");
  }

  @Test
  void shouldDenyAnotherStudentsDataAndAuditTheDenial() throws Exception {
    mockMvc
        .perform(
            as(
                ANA,
                "STUDENT",
                "S-1001",
                get("/api/core/students/S-1003/financial-status"),
                "gate3-denied"))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.title").value("Access denied"))
        .andExpect(jsonPath("$.requestId").value("gate3-denied"));

    Map<String, Object> record = single();
    assertThat(record)
        .containsEntry("action", "READ_FINANCIAL_STATUS")
        .containsEntry("subject_id", "S-1003")
        .containsEntry("authorization_basis", "NONE")
        .containsEntry("outcome", "DENIED")
        .containsEntry("actor_id", ANA);
  }

  @Test
  void shouldHideExistenceFromUnauthorizedStudents() throws Exception {
    // Authorization runs before existence: 403 for a student, 404 for staff.
    mockMvc
        .perform(as(ANA, "STUDENT", "S-1001", get("/api/core/students/S-9999"), "gate3-hidden"))
        .andExpect(status().isForbidden());
    mockMvc
        .perform(as(CARLOS, "ADVISOR", "A-2001", get("/api/core/students/S-9999"), "gate3-404"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.title").value("Not found"));
  }

  @Test
  void shouldLetStaffReadAnyStudentWithStaffRoleBasis() throws Exception {
    mockMvc
        .perform(
            as(
                CARLOS,
                "ADVISOR",
                "A-2001",
                get("/api/core/students/S-1003/academic-status"),
                "gate3-staff"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.academicStanding").value("AT_RISK"))
        .andExpect(jsonPath("$.currentTerm").value("2026-2"))
        .andExpect(jsonPath("$.history.length()").value(2));

    assertThat(single())
        .containsEntry("authorization_basis", "STAFF_ROLE")
        .containsEntry("outcome", "ALLOWED");
  }

  @Test
  void shouldExposeTheAtRiskFinancialSignalsOfTheSeededStudent() throws Exception {
    mockMvc
        .perform(
            as(
                CARLOS,
                "ADVISOR",
                "A-2001",
                get("/api/core/students/S-1003/financial-status"),
                "gate3-risk"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.overdue").value(true))
        .andExpect(jsonPath("$.daysOverdue").value(62))
        .andExpect(jsonPath("$.financialHold").value(true));
  }

  @Test
  void shouldRejectCallsWithoutServiceTokenBeforeReachingTheDomain() throws Exception {
    mockMvc
        .perform(
            get("/api/core/students/S-1001")
                .header(IdentityHeaders.USER_ID, ANA.toString())
                .header(IdentityHeaders.USER_ROLES, "STUDENT")
                .header(IdentityHeaders.EXTERNAL_REFERENCE, "S-1001"))
        .andExpect(status().isUnauthorized());

    assertThat(jdbc.queryForList("SELECT * FROM audit.audit_record")).isEmpty();
  }

  private MockHttpServletRequestBuilder as(
      UUID userId,
      String role,
      String reference,
      MockHttpServletRequestBuilder request,
      String requestId) {
    return request
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokens.tokenFor("core-service"))
        .header(Correlation.REQUEST_ID_HEADER, requestId)
        .header(IdentityHeaders.USER_ID, userId.toString())
        .header(IdentityHeaders.USER_ROLES, role)
        .header(IdentityHeaders.EXTERNAL_REFERENCE, reference);
  }

  private Map<String, Object> single() {
    List<Map<String, Object>> rows = jdbc.queryForList("SELECT * FROM audit.audit_record");
    assertThat(rows).hasSize(1);
    return rows.get(0);
  }
}
