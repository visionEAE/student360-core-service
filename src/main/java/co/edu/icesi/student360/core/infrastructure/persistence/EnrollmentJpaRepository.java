package co.edu.icesi.student360.core.infrastructure.persistence;

import co.edu.icesi.student360.core.domain.model.Enrollment;
import co.edu.icesi.student360.core.domain.port.EnrollmentRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentJpaRepository
    extends JpaRepository<Enrollment, Long>, EnrollmentRepository {}
