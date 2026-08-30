package co.edu.icesi.student360.core.application.query.model;

import co.edu.icesi.student360.core.domain.model.Professor;
import co.edu.icesi.student360.core.domain.model.Student;

/**
 * A directory hit: just enough to identify a person and let a caller reference them elsewhere (e.g.
 * network-service's support-network graph) — never grades, balances or contact details.
 */
public record DirectoryEntryModel(String reference, String kind, String displayName) {

  public static DirectoryEntryModel ofStudent(Student student) {
    return new DirectoryEntryModel(student.getId(), "STUDENT", student.fullName());
  }

  public static DirectoryEntryModel ofProfessor(Professor professor) {
    return new DirectoryEntryModel(
        "PROF-" + professor.getId(), "PROFESSOR", professor.getFullName());
  }
}
