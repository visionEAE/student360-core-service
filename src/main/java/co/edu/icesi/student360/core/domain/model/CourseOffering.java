package co.edu.icesi.student360.core.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Who teaches a course in a given term — a fact, not an opinion, unlike the support graph. */
@Entity
@Table(name = "course_offering", schema = "core")
public class CourseOffering {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String term;

  @Column(name = "course_code", nullable = false)
  private String courseCode;

  @Column(name = "course_name", nullable = false)
  private String courseName;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "professor_id", nullable = false)
  private Professor professor;

  protected CourseOffering() {}

  public String getTerm() {
    return term;
  }

  public String getCourseCode() {
    return courseCode;
  }

  public String getCourseName() {
    return courseName;
  }

  public Professor getProfessor() {
    return professor;
  }
}
