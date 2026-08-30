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
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.hamcrest.Matchers;
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
 * Phase gate 3 plus contract v2: a student fetches their own data; another student's data → 403
 * with a DENIED audit record; staff read with STAFF_ROLE; the gradebook, GPA history and
 * instalments are exposed; the batch summary is a staff capability.
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
        .andExpect(jsonPath("$.financialHold").value(false))
        .andExpect(jsonPath("$.tuitionAmount").value(7900000.0))
        .andExpect(jsonPath("$.paidAmount").value(7900000.0))
        .andExpect(jsonPath("$.payments.length()").value(4))
        .andExpect(jsonPath("$.payments[*].status", Matchers.everyItem(Matchers.is("PAID"))));

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
  void shouldExposeTheProfileWithCodeSemesterAndProgramLength() throws Exception {
    mockMvc
        .perform(as(CARLOS, "ADVISOR", "A-2001", get("/api/core/students/S-1003"), "v2-profile"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("S-1003"))
        .andExpect(jsonPath("$.code").value("2025145032"))
        .andExpect(jsonPath("$.fullName").value("María Rojas"))
        .andExpect(jsonPath("$.program.code").value("PSI"))
        .andExpect(jsonPath("$.program.totalSemesters").value(10))
        .andExpect(jsonPath("$.currentSemester").value(7))
        .andExpect(jsonPath("$.admissionTerm").value("2023-2"))
        .andExpect(jsonPath("$.status").value("ACTIVE"))
        .andExpect(jsonPath("$.enrollmentStatus").value("ACTIVE"));

    assertThat(single())
        .containsEntry("action", "READ_STUDENT_PROFILE")
        .containsEntry("subject_id", "S-1003")
        .containsEntry("authorization_basis", "STAFF_ROLE");
  }

  @Test
  void shouldExposeGpaHistoryAndCurrentGradebook() throws Exception {
    mockMvc
        .perform(
            as(
                CARLOS,
                "ADVISOR",
                "A-2001",
                get("/api/core/students/S-1003/academic-status"),
                "v2-academic"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.studentId").value("S-1003"))
        .andExpect(jsonPath("$.currentTerm").value("2026-2"))
        .andExpect(jsonPath("$.currentSemester").value(7))
        .andExpect(jsonPath("$.totalSemesters").value(10))
        .andExpect(jsonPath("$.academicStanding").value("AT_RISK"))
        .andExpect(jsonPath("$.enrollmentStatus").value("ACTIVE"))
        .andExpect(jsonPath("$.cumulativeGpa").value(2.95))
        .andExpect(jsonPath("$.creditsEnrolled").value(12))
        .andExpect(jsonPath("$.gpaHistory.length()").value(6))
        .andExpect(jsonPath("$.gpaHistory[0].semester").value(1))
        .andExpect(jsonPath("$.gpaHistory[0].term").value("2023-2"))
        .andExpect(jsonPath("$.gpaHistory[1].termGpa").value(3.6))
        .andExpect(jsonPath("$.gpaHistory[5].semester").value(6))
        .andExpect(jsonPath("$.gpaHistory[5].termGpa").value(2.7))
        .andExpect(jsonPath("$.currentCourses.length()").value(5))
        .andExpect(jsonPath("$.currentCourses[?(@.code == 'EST-201')].currentGrade").value(2.8))
        .andExpect(jsonPath("$.currentCourses[?(@.code == 'EST-201')].credits").value(4))
        .andExpect(jsonPath("$.sourceUpdatedAt").isNotEmpty());

    assertThat(single())
        .containsEntry("authorization_basis", "STAFF_ROLE")
        .containsEntry("outcome", "ALLOWED");
  }

  @Test
  void shouldExposeTheAtRiskFinancialPictureWithInstalments() throws Exception {
    mockMvc
        .perform(
            as(
                CARLOS,
                "ADVISOR",
                "A-2001",
                get("/api/core/students/S-1003/financial-status"),
                "v2-financial"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tuitionAmount").value(8500000.0))
        .andExpect(jsonPath("$.paidAmount").value(7260000.0))
        .andExpect(jsonPath("$.outstandingBalance").value(1240000.0))
        .andExpect(jsonPath("$.overdueBalance").value(1240000.0))
        .andExpect(jsonPath("$.overdue").value(true))
        .andExpect(jsonPath("$.daysOverdue", Matchers.greaterThanOrEqualTo(15)))
        .andExpect(jsonPath("$.dueDate").value("2026-08-15"))
        .andExpect(jsonPath("$.paymentPlan").doesNotExist())
        .andExpect(jsonPath("$.scholarship").value("20% por mérito académico"))
        .andExpect(jsonPath("$.financialHold").value(true))
        .andExpect(jsonPath("$.payments.length()").value(4))
        .andExpect(jsonPath("$.payments[0].date").value("2026-02-20"))
        .andExpect(jsonPath("$.payments[0].description").value("Derechos de matrícula"))
        .andExpect(jsonPath("$.payments[0].status").value("PAID"))
        .andExpect(jsonPath("$.payments[3].amount").value(1240000.0))
        .andExpect(jsonPath("$.payments[3].status").value("OVERDUE"));
  }

  @Test
  void shouldLetStaffReadSummariesInBatchSkippingUnknownIds() throws Exception {
    mockMvc
        .perform(
            as(
                CARLOS,
                "ADVISOR",
                "A-2001",
                get("/api/core/students/summaries").param("ids", "S-1003,S-1001,S-9999"),
                "v2-batch"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value("S-1001"))
        .andExpect(jsonPath("$[0].academicStanding").value("GOOD"))
        .andExpect(jsonPath("$[0].overdue").value(false))
        .andExpect(jsonPath("$[1].id").value("S-1003"))
        .andExpect(jsonPath("$[1].code").value("2025145032"))
        .andExpect(jsonPath("$[1].program.name").value("Psychology"))
        .andExpect(jsonPath("$[1].currentSemester").value(7))
        .andExpect(jsonPath("$[1].academicStanding").value("AT_RISK"))
        .andExpect(jsonPath("$[1].overdue").value(true))
        .andExpect(jsonPath("$[1].outstandingBalance").value(1240000.0));

    assertThat(single())
        .containsEntry("action", "READ_STUDENT_SUMMARIES")
        .containsEntry("subject_type", "STUDENT_BATCH")
        .containsEntry("subject_id", "S-1003,S-1001,S-9999")
        .containsEntry("authorization_basis", "STAFF_ROLE")
        .containsEntry("outcome", "ALLOWED");
  }

  @Test
  void shouldDenyBatchSummariesToStudentsAndRejectOversizedBatches() throws Exception {
    mockMvc
        .perform(
            as(
                ANA,
                "STUDENT",
                "S-1001",
                get("/api/core/students/summaries").param("ids", "S-1001,S-1003"),
                "v2-batch-denied"))
        .andExpect(status().isForbidden());
    assertThat(single())
        .containsEntry("action", "READ_STUDENT_SUMMARIES")
        .containsEntry("outcome", "DENIED")
        .containsEntry("authorization_basis", "NONE");

    String tooMany =
        IntStream.rangeClosed(1, 101).mapToObj(i -> "S-" + i).collect(Collectors.joining(","));
    mockMvc
        .perform(
            as(
                CARLOS,
                "ADVISOR",
                "A-2001",
                get("/api/core/students/summaries").param("ids", tooMany),
                "v2-batch-too-many"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value("Invalid request"));
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
