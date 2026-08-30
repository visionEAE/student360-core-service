package co.edu.icesi.student360.core.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/** One course of the official gradebook for a student and term. */
@Entity
@Table(name = "course_grade", schema = "core")
public class CourseGrade {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "student_id", nullable = false)
  private String studentId;

  @Column(nullable = false)
  private String term;

  @Column(name = "course_code", nullable = false)
  private String courseCode;

  @Column(name = "course_name", nullable = false)
  private String courseName;

  @Column(nullable = false)
  private int credits;

  @Column(name = "current_grade")
  private BigDecimal currentGrade;

  protected CourseGrade() {}

  public String getTerm() {
    return term;
  }

  public String getCourseCode() {
    return courseCode;
  }

  public String getCourseName() {
    return courseName;
  }

  public int getCredits() {
    return credits;
  }

  public BigDecimal getCurrentGrade() {
    return currentGrade;
  }
}
