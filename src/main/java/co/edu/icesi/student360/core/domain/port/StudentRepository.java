package co.edu.icesi.student360.core.domain.port;

import co.edu.icesi.student360.core.domain.model.Student;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface StudentRepository {

  Optional<Student> findById(String id);

  List<Student> findByIdIn(Collection<String> ids);

  List<Student>
      findTop10ByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrderByLastName(
          String firstName, String lastName);
}
