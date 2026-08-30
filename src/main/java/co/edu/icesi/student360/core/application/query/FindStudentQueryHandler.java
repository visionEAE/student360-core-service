package co.edu.icesi.student360.core.application.query;

import co.edu.icesi.student360.common.api.exception.NotFoundException;
import co.edu.icesi.student360.common.audit.Audited;
import co.edu.icesi.student360.common.authorization.StudentRecordAccessPolicy;
import co.edu.icesi.student360.core.application.query.model.StudentProfileModel;
import co.edu.icesi.student360.core.domain.port.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Authorization runs before existence: an unauthorized student learns nothing. */
@Service
public class FindStudentQueryHandler {

  static final String RESOURCE = "Student";

  private final StudentRepository students;
  private final StudentRecordAccessPolicy accessPolicy;

  public FindStudentQueryHandler(
      StudentRepository students, StudentRecordAccessPolicy accessPolicy) {
    this.students = students;
    this.accessPolicy = accessPolicy;
  }

  @Audited(action = "READ_STUDENT_PROFILE", subjectType = "STUDENT")
  @Transactional(readOnly = true)
  public StudentProfileModel handle(FindStudentQuery query) {
    accessPolicy.assertCanRead(query.studentId());
    return students
        .findById(query.studentId())
        .map(StudentProfileModel::from)
        .orElseThrow(() -> new NotFoundException(RESOURCE, query.studentId()));
  }
}
