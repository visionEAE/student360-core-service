package co.edu.icesi.student360.core.application.query;

import co.edu.icesi.student360.common.audit.Audited;
import co.edu.icesi.student360.core.application.query.model.StudentSummaryModel;
import co.edu.icesi.student360.core.domain.model.Enrollment;
import co.edu.icesi.student360.core.domain.model.FinancialStatus;
import co.edu.icesi.student360.core.domain.model.Student;
import co.edu.icesi.student360.core.domain.policy.StaffAccessPolicy;
import co.edu.icesi.student360.core.domain.port.EnrollmentRepository;
import co.edu.icesi.student360.core.domain.port.FinancialStatusRepository;
import co.edu.icesi.student360.core.domain.port.StudentRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Staff only. Unknown ids are skipped; the result keeps the order of the request. */
@Service
public class StudentSummariesQueryHandler {

  static final String SUBJECT_TYPE = "STUDENT_BATCH";

  private final StudentRepository students;
  private final EnrollmentRepository enrollments;
  private final FinancialStatusRepository financialStatuses;
  private final StaffAccessPolicy staffPolicy;

  public StudentSummariesQueryHandler(
      StudentRepository students,
      EnrollmentRepository enrollments,
      FinancialStatusRepository financialStatuses,
      StaffAccessPolicy staffPolicy) {
    this.students = students;
    this.enrollments = enrollments;
    this.financialStatuses = financialStatuses;
    this.staffPolicy = staffPolicy;
  }

  @Audited(action = "READ_STUDENT_SUMMARIES", subjectType = SUBJECT_TYPE)
  @Transactional(readOnly = true)
  public List<StudentSummaryModel> handle(StudentSummariesQuery query) {
    staffPolicy.assertStaff(SUBJECT_TYPE, query.toString());
    Map<String, Student> found =
        students.findByIdIn(query.studentIds()).stream()
            .collect(Collectors.toMap(Student::getId, Function.identity()));
    // The most recent term is the current one.
    Map<String, Enrollment> current =
        enrollments.findByStudentIdIn(found.keySet()).stream()
            .collect(
                Collectors.toMap(
                    Enrollment::getStudentId,
                    Function.identity(),
                    (a, b) -> a.getTerm().compareTo(b.getTerm()) >= 0 ? a : b));
    Map<String, FinancialStatus> financial =
        financialStatuses.findByStudentIdIn(found.keySet()).stream()
            .collect(Collectors.toMap(FinancialStatus::getStudentId, Function.identity()));
    return query.studentIds().stream()
        .map(found::get)
        .filter(student -> student != null)
        .sorted(Comparator.comparing(Student::getId))
        .map(
            student ->
                StudentSummaryModel.from(
                    student,
                    Optional.ofNullable(current.get(student.getId())),
                    Optional.ofNullable(financial.get(student.getId()))))
        .toList();
  }
}
