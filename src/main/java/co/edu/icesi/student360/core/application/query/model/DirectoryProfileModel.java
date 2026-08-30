package co.edu.icesi.student360.core.application.query.model;

import co.edu.icesi.student360.core.domain.model.CourseOffering;
import co.edu.icesi.student360.core.domain.model.Professor;
import co.edu.icesi.student360.core.domain.model.Student;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The card shown when someone opens a person in a support-network graph: who they are and how to
 * reach them, in the institution's own words. Deliberately the same three display fields whatever
 * the kind, so a caller renders one card rather than branching per kind.
 *
 * <p>Only what a university directory already publishes — name, institutional email, program or
 * department — never grades, balances or anything a support case would hold.
 */
public record DirectoryProfileModel(
    String reference,
    String kind,
    String displayName,
    String email,
    String headline,
    String detail) {

  public static DirectoryProfileModel ofStudent(Student student) {
    return new DirectoryProfileModel(
        student.getId(),
        "STUDENT",
        student.fullName(),
        student.getEmail(),
        student.getProgram().getName(),
        student.getCurrentSemester() + ".º semestre");
  }

  /**
   * @param offerings what this professor teaches in the current term, possibly empty — the detail
   *     line then simply omits the course list rather than inventing one.
   */
  public static DirectoryProfileModel ofProfessor(
      Professor professor, List<CourseOffering> offerings) {
    String courses =
        offerings.stream()
            .map(CourseOffering::getCourseName)
            .distinct()
            .collect(Collectors.joining(", "));
    return new DirectoryProfileModel(
        "PROF-" + professor.getId(),
        "PROFESSOR",
        professor.getFullName(),
        professor.getEmail(),
        professor.getDepartment(),
        courses.isBlank() ? null : courses);
  }
}
