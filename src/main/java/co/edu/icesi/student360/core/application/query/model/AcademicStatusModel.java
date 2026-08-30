package co.edu.icesi.student360.core.application.query.model;

import co.edu.icesi.student360.core.domain.model.CourseGrade;
import co.edu.icesi.student360.core.domain.model.Enrollment;
import co.edu.icesi.student360.core.domain.model.Student;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

/** Contract v2 {@code AcademicStatus}: the current term plus the graded history behind it. */
public record AcademicStatusModel(
    String studentId,
    String currentTerm,
    int currentSemester,
    int totalSemesters,
    String academicStanding,
    String enrollmentStatus,
    BigDecimal cumulativeGpa,
    int creditsEnrolled,
    List<GpaPoint> gpaHistory,
    List<CourseGradeModel> currentCourses,
    Instant sourceUpdatedAt) {

  public record GpaPoint(Integer semester, String term, BigDecimal termGpa) {}

  public record CourseGradeModel(String code, String name, int credits, BigDecimal currentGrade) {}

  /**
   * @param history every enrollment of the student, any order
   * @param current the enrollment of the current term (the most recent one)
   */
  public static AcademicStatusModel from(
      Student student,
      Enrollment current,
      List<Enrollment> history,
      List<CourseGrade> gradebook,
      Instant sourceUpdatedAt) {
    List<GpaPoint> points =
        history.stream()
            .filter(Enrollment::isGraded)
            .sorted(Comparator.comparing(Enrollment::getTerm))
            .map(e -> new GpaPoint(e.getSemesterNumber(), e.getTerm(), e.getTermGpa()))
            .toList();
    List<CourseGradeModel> courses =
        gradebook.stream()
            .map(
                g ->
                    new CourseGradeModel(
                        g.getCourseCode(), g.getCourseName(), g.getCredits(), g.getCurrentGrade()))
            .toList();
    return new AcademicStatusModel(
        student.getId(),
        current.getTerm(),
        student.getCurrentSemester(),
        student.getProgram().getTotalSemesters(),
        current.getAcademicStanding().name(),
        student.enrollmentStatus().name(),
        current.getCumulativeGpa(),
        current.getCreditsEnrolled(),
        points,
        courses,
        sourceUpdatedAt);
  }
}
