package co.edu.icesi.student360.core.api.dto;

import co.edu.icesi.student360.core.domain.model.Student;

public record StudentResponse(
    String id,
    String fullName,
    String email,
    ProgramResponse program,
    String admissionTerm,
    String status) {

  public record ProgramResponse(String code, String name, String faculty) {}

  public static StudentResponse from(Student student) {
    return new StudentResponse(
        student.getId(),
        student.fullName(),
        student.getEmail(),
        new ProgramResponse(
            student.getProgram().getCode(),
            student.getProgram().getName(),
            student.getProgram().getFaculty()),
        student.getAdmissionTerm(),
        student.getStatus().name());
  }
}
