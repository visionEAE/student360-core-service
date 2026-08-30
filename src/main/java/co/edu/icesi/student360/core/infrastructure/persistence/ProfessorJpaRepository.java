package co.edu.icesi.student360.core.infrastructure.persistence;

import co.edu.icesi.student360.core.domain.model.Professor;
import co.edu.icesi.student360.core.domain.port.ProfessorRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorJpaRepository
    extends JpaRepository<Professor, Long>, ProfessorRepository {}
