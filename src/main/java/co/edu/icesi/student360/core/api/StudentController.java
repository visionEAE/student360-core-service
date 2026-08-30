package co.edu.icesi.student360.core.api;

import co.edu.icesi.student360.core.application.query.FindAcademicStatusQuery;
import co.edu.icesi.student360.core.application.query.FindAcademicStatusQueryHandler;
import co.edu.icesi.student360.core.application.query.FindFinancialStatusQuery;
import co.edu.icesi.student360.core.application.query.FindFinancialStatusQueryHandler;
import co.edu.icesi.student360.core.application.query.FindStudentQuery;
import co.edu.icesi.student360.core.application.query.FindStudentQueryHandler;
import co.edu.icesi.student360.core.application.query.model.AcademicStatusModel;
import co.edu.icesi.student360.core.application.query.model.FinancialStatusModel;
import co.edu.icesi.student360.core.application.query.model.StudentProfileModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** HTTP ⇄ query translation only; the read models already have the contract's shape. */
@RestController
@RequestMapping("/api/core/students")
public class StudentController {

  private final FindStudentQueryHandler findStudent;
  private final FindAcademicStatusQueryHandler findAcademicStatus;
  private final FindFinancialStatusQueryHandler findFinancialStatus;

  public StudentController(
      FindStudentQueryHandler findStudent,
      FindAcademicStatusQueryHandler findAcademicStatus,
      FindFinancialStatusQueryHandler findFinancialStatus) {
    this.findStudent = findStudent;
    this.findAcademicStatus = findAcademicStatus;
    this.findFinancialStatus = findFinancialStatus;
  }

  @GetMapping("/{id}")
  public StudentProfileModel student(@PathVariable String id) {
    return findStudent.handle(new FindStudentQuery(id));
  }

  @GetMapping("/{id}/academic-status")
  public AcademicStatusModel academicStatus(@PathVariable String id) {
    return findAcademicStatus.handle(new FindAcademicStatusQuery(id));
  }

  @GetMapping("/{id}/financial-status")
  public FinancialStatusModel financialStatus(@PathVariable String id) {
    return findFinancialStatus.handle(new FindFinancialStatusQuery(id));
  }
}
