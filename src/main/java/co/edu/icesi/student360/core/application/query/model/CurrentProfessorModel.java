package co.edu.icesi.student360.core.application.query.model;

import co.edu.icesi.student360.core.domain.model.CourseOffering;

/** Contract v2 network extension {@code current-professors}: who teaches a student right now. */
public record CurrentProfessorModel(
    String courseCode, String courseName, ProfessorModel professor) {

  public record ProfessorModel(Long id, String fullName, String department) {}

  public static CurrentProfessorModel from(CourseOffering offering) {
    return new CurrentProfessorModel(
        offering.getCourseCode(),
        offering.getCourseName(),
        new ProfessorModel(
            offering.getProfessor().getId(),
            offering.getProfessor().getFullName(),
            offering.getProfessor().getDepartment()));
  }
}
