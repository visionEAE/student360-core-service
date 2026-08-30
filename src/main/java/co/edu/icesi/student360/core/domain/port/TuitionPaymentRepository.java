package co.edu.icesi.student360.core.domain.port;

import co.edu.icesi.student360.core.domain.model.TuitionPayment;
import java.util.List;

public interface TuitionPaymentRepository {

  List<TuitionPayment> findByStudentIdOrderByDueDate(String studentId);
}
