package co.edu.icesi.student360.core.domain.port;

import co.edu.icesi.student360.core.domain.model.Professor;
import java.util.List;

public interface ProfessorRepository {

  List<Professor> findTop10ByFullNameContainingIgnoreCaseOrderByFullName(String fullName);
}
