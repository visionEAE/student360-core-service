package co.edu.icesi.student360.core.infrastructure.persistence;

import co.edu.icesi.student360.core.domain.model.CourseOffering;
import co.edu.icesi.student360.core.domain.port.CourseOfferingRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseOfferingJpaRepository
    extends JpaRepository<CourseOffering, Long>, CourseOfferingRepository {

  @Override
  List<CourseOffering> findByTermAndCourseCodeInOrderByCourseCode(
      String term, Collection<String> courseCodes);
}
