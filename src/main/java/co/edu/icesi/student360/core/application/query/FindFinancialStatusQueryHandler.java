package co.edu.icesi.student360.core.application.query;

import co.edu.icesi.student360.common.api.exception.NotFoundException;
import co.edu.icesi.student360.common.audit.Audited;
import co.edu.icesi.student360.common.authorization.StudentRecordAccessPolicy;
import co.edu.icesi.student360.core.application.query.model.FinancialStatusModel;
import co.edu.icesi.student360.core.domain.model.FinancialStatus;
import co.edu.icesi.student360.core.domain.port.FinancialStatusRepository;
import co.edu.icesi.student360.core.domain.port.StudentRepository;
import co.edu.icesi.student360.core.domain.port.TuitionPaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** READ_FINANCIAL_STATUS guards the most sensitive data this service owns. */
@Service
public class FindFinancialStatusQueryHandler {

  private final StudentRepository students;
  private final FinancialStatusRepository financialStatuses;
  private final TuitionPaymentRepository payments;
  private final StudentRecordAccessPolicy accessPolicy;

  public FindFinancialStatusQueryHandler(
      StudentRepository students,
      FinancialStatusRepository financialStatuses,
      TuitionPaymentRepository payments,
      StudentRecordAccessPolicy accessPolicy) {
    this.students = students;
    this.financialStatuses = financialStatuses;
    this.payments = payments;
    this.accessPolicy = accessPolicy;
  }

  @Audited(action = "READ_FINANCIAL_STATUS", subjectType = "STUDENT")
  @Transactional(readOnly = true)
  public FinancialStatusModel handle(FindFinancialStatusQuery query) {
    accessPolicy.assertCanRead(query.studentId());
    if (students.findById(query.studentId()).isEmpty()) {
      throw new NotFoundException(FindStudentQueryHandler.RESOURCE, query.studentId());
    }
    FinancialStatus status =
        financialStatuses
            .findByStudentId(query.studentId())
            .orElseThrow(
                () -> new NotFoundException("Financial status of student", query.studentId()));
    return FinancialStatusModel.from(
        status, payments.findByStudentIdOrderByDueDate(query.studentId()));
  }
}
