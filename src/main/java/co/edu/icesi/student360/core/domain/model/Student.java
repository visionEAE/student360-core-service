package co.edu.icesi.student360.core.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

/** Source of truth for a student's identity. The id is the cross-service key ({@code S-1001}). */
@Entity
@Table(name = "student", schema = "core")
public class Student {

  @Id private String id;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Column(nullable = false, unique = true)
  private String email;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "program_id", nullable = false)
  private Program program;

  @Column(name = "admission_term", nullable = false)
  private String admissionTerm;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private StudentStatus status;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected Student() {}

  public String getId() {
    return id;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String fullName() {
    return firstName + " " + lastName;
  }

  public String getEmail() {
    return email;
  }

  public Program getProgram() {
    return program;
  }

  public String getAdmissionTerm() {
    return admissionTerm;
  }

  public StudentStatus getStatus() {
    return status;
  }
}
