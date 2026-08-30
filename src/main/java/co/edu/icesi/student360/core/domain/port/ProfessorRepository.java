package co.edu.icesi.student360.core.domain.port;

import co.edu.icesi.student360.core.domain.model.Professor;
import java.util.List;
import java.util.Optional;

public interface ProfessorRepository {

  Optional<Professor> findById(Long id);

  List<Professor> findTop10ByFullNameContainingIgnoreCaseOrderByFullName(String fullName);
}
