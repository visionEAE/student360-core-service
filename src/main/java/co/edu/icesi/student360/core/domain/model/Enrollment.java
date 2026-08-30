package co.edu.icesi.student360.core.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/** One term of a student's academic record. */
@Entity
@Table(name = "enrollment", schema = "core")
public class Enrollment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "student_id", nullable = false)
  private String studentId;

  @Column(nullable = false)
  private String term;

  @Column(name = "credits_enrolled", nullable = false)
  private int creditsEnrolled;

  @Column(name = "credits_approved", nullable = false)
  private int creditsApproved;

  @Column(name = "term_gpa")
  private BigDecimal termGpa;

  @Column(name = "cumulative_gpa", nullable = false)
  private BigDecimal cumulativeGpa;

  @Enumerated(EnumType.STRING)
  @Column(name = "academic_standing", nullable = false)
  private AcademicStanding academicStanding;

  protected Enrollment() {}

  public String getStudentId() {
    return studentId;
  }

  public String getTerm() {
    return term;
  }

  public int getCreditsEnrolled() {
    return creditsEnrolled;
  }

  public int getCreditsApproved() {
    return creditsApproved;
  }

  public BigDecimal getTermGpa() {
    return termGpa;
  }

  public BigDecimal getCumulativeGpa() {
    return cumulativeGpa;
  }

  public AcademicStanding getAcademicStanding() {
    return academicStanding;
  }
}
