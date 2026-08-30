package co.edu.icesi.student360.core.domain.port;

import co.edu.icesi.student360.core.domain.model.CourseOffering;
import java.util.Collection;
import java.util.List;

public interface CourseOfferingRepository {

  List<CourseOffering> findByTermAndCourseCodeInOrderByCourseCode(
      String term, Collection<String> courseCodes);

  /** Most recent term first, so a caller can take just what the professor teaches right now. */
  List<CourseOffering> findByProfessorIdOrderByTermDescCourseCodeAsc(Long professorId);
}
