package co.edu.icesi.student360.core.application.query.model;

import co.edu.icesi.student360.core.domain.model.Student;

/** Contract v2 {@code StudentProfile}. */
public record StudentProfileModel(
    String id,
    String code,
    String fullName,
    String firstName,
    String lastName,
    String email,
    ProgramModel program,
    int currentSemester,
    String admissionTerm,
    String status,
    String enrollmentStatus) {

  public record ProgramModel(String code, String name, String faculty, int totalSemesters) {}

  public static StudentProfileModel from(Student student) {
    return new StudentProfileModel(
        student.getId(),
        student.getCode(),
        student.fullName(),
        student.getFirstName(),
        student.getLastName(),
        student.getEmail(),
        new ProgramModel(
            student.getProgram().getCode(),
            student.getProgram().getName(),
            student.getProgram().getFaculty(),
            student.getProgram().getTotalSemesters()),
        student.getCurrentSemester(),
        student.getAdmissionTerm(),
        student.getStatus().name(),
        student.enrollmentStatus().name());
  }
}
