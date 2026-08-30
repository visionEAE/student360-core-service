package co.edu.icesi.student360.core.domain.service;

import co.edu.icesi.student360.common.api.exception.NotFoundException;
import co.edu.icesi.student360.common.audit.Audited;
import co.edu.icesi.student360.common.authorization.StudentRecordAccessPolicy;
import co.edu.icesi.student360.core.domain.model.AcademicStatus;
import co.edu.icesi.student360.core.domain.model.Enrollment;
import co.edu.icesi.student360.core.domain.model.FinancialStatus;
import co.edu.icesi.student360.core.domain.model.Student;
import co.edu.icesi.student360.core.domain.port.EnrollmentRepository;
import co.edu.icesi.student360.core.domain.port.FinancialStatusRepository;
import co.edu.icesi.student360.core.domain.port.StudentRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reads of a student's institutional records. Authorization runs before existence is revealed: a
 * student asking about another student receives 403 whether or not that student exists, and every
 * read — allowed or denied — leaves an audit record with its basis.
 */
@Service
public class StudentRecordService {

  static final String RESOURCE = "Student";

  private final StudentRepository students;
  private final EnrollmentRepository enrollments;
  private final FinancialStatusRepository financialStatuses;
  private final StudentRecordAccessPolicy accessPolicy;

  public StudentRecordService(
      StudentRepository students,
      EnrollmentRepository enrollments,
      FinancialStatusRepository financialStatuses,
      StudentRecordAccessPolicy accessPolicy) {
    this.students = students;
    this.enrollments = enrollments;
    this.financialStatuses = financialStatuses;
    this.accessPolicy = accessPolicy;
  }

  @Audited(action = "READ_STUDENT_PROFILE", subjectType = "STUDENT")
  @Transactional(readOnly = true)
  public Student findStudent(String studentId) {
    accessPolicy.assertCanRead(studentId);
    return students
        .findById(studentId)
        .orElseThrow(() -> new NotFoundException(RESOURCE, studentId));
  }

  @Audited(action = "READ_ACADEMIC_STATUS", subjectType = "STUDENT")
  @Transactional(readOnly = true)
  public AcademicStatus findAcademicStatus(String studentId) {
    accessPolicy.assertCanRead(studentId);
    requireStudent(studentId);
    List<Enrollment> history = enrollments.findByStudentIdOrderByTermDesc(studentId);
    if (history.isEmpty()) {
      throw new NotFoundException("Academic record of student", studentId);
    }
    return new AcademicStatus(studentId, history.get(0), history);
  }

  @Audited(action = "READ_FINANCIAL_STATUS", subjectType = "STUDENT")
  @Transactional(readOnly = true)
  public FinancialStatus findFinancialStatus(String studentId) {
    accessPolicy.assertCanRead(studentId);
    requireStudent(studentId);
    return financialStatuses
        .findByStudentId(studentId)
        .orElseThrow(() -> new NotFoundException("Financial status of student", studentId));
  }

  private void requireStudent(String studentId) {
    if (students.findById(studentId).isEmpty()) {
      throw new NotFoundException(RESOURCE, studentId);
    }
  }
}
