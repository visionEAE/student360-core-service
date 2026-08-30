package co.edu.icesi.student360.core.application.query;

import co.edu.icesi.student360.common.api.exception.NotFoundException;
import co.edu.icesi.student360.common.audit.Audited;
import co.edu.icesi.student360.common.authorization.StudentRecordAccessPolicy;
import co.edu.icesi.student360.core.application.query.model.CurrentProfessorModel;
import co.edu.icesi.student360.core.domain.model.CourseGrade;
import co.edu.icesi.student360.core.domain.model.Enrollment;
import co.edu.icesi.student360.core.domain.port.CourseGradeRepository;
import co.edu.icesi.student360.core.domain.port.CourseOfferingRepository;
import co.edu.icesi.student360.core.domain.port.EnrollmentRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Who currently teaches a student — a deterministic join (current courses → their offerings), never
 * rated or opinionated, unlike the support network graph (network-service).
 */
@Service
public class GetCurrentProfessorsQueryHandler {

  private final EnrollmentRepository enrollments;
  private final CourseGradeRepository gradebook;
  private final CourseOfferingRepository offerings;
  private final StudentRecordAccessPolicy accessPolicy;

  public GetCurrentProfessorsQueryHandler(
      EnrollmentRepository enrollments,
      CourseGradeRepository gradebook,
      CourseOfferingRepository offerings,
      StudentRecordAccessPolicy accessPolicy) {
    this.enrollments = enrollments;
    this.gradebook = gradebook;
    this.offerings = offerings;
    this.accessPolicy = accessPolicy;
  }

  @Audited(action = "READ_CURRENT_PROFESSORS", subjectType = "STUDENT")
  @Transactional(readOnly = true)
  public List<CurrentProfessorModel> handle(GetCurrentProfessorsQuery query) {
    accessPolicy.assertCanRead(query.studentId());
    List<Enrollment> history = enrollments.findByStudentIdOrderByTermDesc(query.studentId());
    if (history.isEmpty()) {
      throw new NotFoundException("Academic record of student", query.studentId());
    }
    String currentTerm = history.get(0).getTerm();
    List<CourseGrade> currentCourses =
        gradebook.findByStudentIdAndTermOrderByCourseCode(query.studentId(), currentTerm);
    List<String> codes = currentCourses.stream().map(CourseGrade::getCourseCode).toList();
    return offerings.findByTermAndCourseCodeInOrderByCourseCode(currentTerm, codes).stream()
        .map(CurrentProfessorModel::from)
        .toList();
  }
}
