package co.edu.icesi.student360.core.domain.port;

import co.edu.icesi.student360.core.domain.model.Student;
import java.util.Optional;

public interface StudentRepository {

  Optional<Student> findById(String id);
}
