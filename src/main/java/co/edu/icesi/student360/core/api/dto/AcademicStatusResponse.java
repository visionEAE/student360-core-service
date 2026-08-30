package co.edu.icesi.student360.core.api.dto;

import co.edu.icesi.student360.core.domain.model.AcademicStatus;
import co.edu.icesi.student360.core.domain.model.Enrollment;
import java.math.BigDecimal;
import java.util.List;

public record AcademicStatusResponse(
    String studentId,
    String currentTerm,
    String academicStanding,
    BigDecimal cumulativeGpa,
    int creditsEnrolled,
    List<TermResponse> history) {

  public record TermResponse(
      String term,
      int creditsEnrolled,
      int creditsApproved,
      BigDecimal termGpa,
      BigDecimal cumulativeGpa,
      String academicStanding) {

    static TermResponse from(Enrollment enrollment) {
      return new TermResponse(
          enrollment.getTerm(),
          enrollment.getCreditsEnrolled(),
          enrollment.getCreditsApproved(),
          enrollment.getTermGpa(),
          enrollment.getCumulativeGpa(),
          enrollment.getAcademicStanding().name());
    }
  }

  public static AcademicStatusResponse from(AcademicStatus status) {
    Enrollment current = status.current();
    return new AcademicStatusResponse(
        status.studentId(),
        current.getTerm(),
        current.getAcademicStanding().name(),
        current.getCumulativeGpa(),
        current.getCreditsEnrolled(),
        status.history().stream().map(TermResponse::from).toList());
  }
}
