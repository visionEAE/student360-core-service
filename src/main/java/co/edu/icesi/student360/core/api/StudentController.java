package co.edu.icesi.student360.core.api;

import co.edu.icesi.student360.core.api.dto.AcademicStatusResponse;
import co.edu.icesi.student360.core.api.dto.FinancialStatusResponse;
import co.edu.icesi.student360.core.api.dto.StudentResponse;
import co.edu.icesi.student360.core.domain.service.StudentRecordService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/core/students")
public class StudentController {

  private final StudentRecordService records;

  public StudentController(StudentRecordService records) {
    this.records = records;
  }

  @GetMapping("/{id}")
  public StudentResponse student(@PathVariable String id) {
    return StudentResponse.from(records.findStudent(id));
  }

  @GetMapping("/{id}/academic-status")
  public AcademicStatusResponse academicStatus(@PathVariable String id) {
    return AcademicStatusResponse.from(records.findAcademicStatus(id));
  }

  @GetMapping("/{id}/financial-status")
  public FinancialStatusResponse financialStatus(@PathVariable String id) {
    return FinancialStatusResponse.from(records.findFinancialStatus(id));
  }
}
