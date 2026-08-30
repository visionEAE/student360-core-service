package co.edu.icesi.student360.core.infrastructure.persistence;

import co.edu.icesi.student360.core.domain.model.CourseGrade;
import co.edu.icesi.student360.core.domain.port.CourseGradeRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseGradeJpaRepository
    extends JpaRepository<CourseGrade, Long>, CourseGradeRepository {}
