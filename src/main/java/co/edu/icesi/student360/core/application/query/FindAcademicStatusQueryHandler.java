package co.edu.icesi.student360.core.application.query;

import co.edu.icesi.student360.common.api.exception.NotFoundException;
import co.edu.icesi.student360.common.audit.Audited;
import co.edu.icesi.student360.common.authorization.StudentRecordAccessPolicy;
import co.edu.icesi.student360.core.application.query.model.AcademicStatusModel;
import co.edu.icesi.student360.core.domain.model.Enrollment;
import co.edu.icesi.student360.core.domain.model.Student;
import co.edu.icesi.student360.core.domain.port.CourseGradeRepository;
import co.edu.icesi.student360.core.domain.port.EnrollmentRepository;
import co.edu.icesi.student360.core.domain.port.StudentRepository;
import java.time.Clock;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FindAcademicStatusQueryHandler {

  private final StudentRepository students;
  private final EnrollmentRepository enrollments;
  private final CourseGradeRepository gradebook;
  private final StudentRecordAccessPolicy accessPolicy;
  private final Clock clock;

  public FindAcademicStatusQueryHandler(
      StudentRepository students,
      EnrollmentRepository enrollments,
      CourseGradeRepository gradebook,
      StudentRecordAccessPolicy accessPolicy,
      Clock clock) {
    this.students = students;
    this.enrollments = enrollments;
    this.gradebook = gradebook;
    this.accessPolicy = accessPolicy;
    this.clock = clock;
  }

  @Audited(action = "READ_ACADEMIC_STATUS", subjectType = "STUDENT")
  @Transactional(readOnly = true)
  public AcademicStatusModel handle(FindAcademicStatusQuery query) {
    accessPolicy.assertCanRead(query.studentId());
    Student student =
        students
            .findById(query.studentId())
            .orElseThrow(
                () -> new NotFoundException(FindStudentQueryHandler.RESOURCE, query.studentId()));
    List<Enrollment> history = enrollments.findByStudentIdOrderByTermDesc(query.studentId());
    if (history.isEmpty()) {
      throw new NotFoundException("Academic record of student", query.studentId());
    }
    Enrollment current = history.get(0);
    return AcademicStatusModel.from(
        student,
        current,
        history,
        gradebook.findByStudentIdAndTermOrderByCourseCode(query.studentId(), current.getTerm()),
        clock.instant());
  }
}
