package co.edu.icesi.student360.core.application.query.model;

import co.edu.icesi.student360.core.domain.model.Enrollment;
import co.edu.icesi.student360.core.domain.model.FinancialStatus;
import co.edu.icesi.student360.core.domain.model.Student;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

/** Contract v2 {@code StudentSummary}: what an advisor overview needs per student. */
public record StudentSummaryModel(
    String id,
    String code,
    String fullName,
    ProgramRef program,
    int currentSemester,
    String academicStanding,
    boolean overdue,
    int daysOverdue,
    BigDecimal outstandingBalance,
    Instant updatedAt) {

  public record ProgramRef(String code, String name) {}

  public static StudentSummaryModel from(
      Student student, Optional<Enrollment> current, Optional<FinancialStatus> financial) {
    return new StudentSummaryModel(
        student.getId(),
        student.getCode(),
        student.fullName(),
        new ProgramRef(student.getProgram().getCode(), student.getProgram().getName()),
        student.getCurrentSemester(),
        current.map(e -> e.getAcademicStanding().name()).orElse(null),
        financial.map(FinancialStatus::isOverdue).orElse(false),
        financial.map(FinancialStatus::getDaysOverdue).orElse(0),
        financial.map(FinancialStatus::getOutstandingBalance).orElse(BigDecimal.ZERO),
        financial.map(FinancialStatus::getUpdatedAt).orElse(null));
  }
}
