package co.edu.icesi.student360.core.domain.port;

import co.edu.icesi.student360.core.domain.model.Enrollment;
import java.util.Collection;
import java.util.List;

public interface EnrollmentRepository {

  /** Most recent term first. */
  List<Enrollment> findByStudentIdOrderByTermDesc(String studentId);

  List<Enrollment> findByStudentIdIn(Collection<String> studentIds);
}
