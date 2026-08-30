package co.edu.icesi.student360.core.domain.port;

import co.edu.icesi.student360.core.domain.model.FinancialStatus;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FinancialStatusRepository {

  Optional<FinancialStatus> findByStudentId(String studentId);

  List<FinancialStatus> findByStudentIdIn(Collection<String> studentIds);
}
