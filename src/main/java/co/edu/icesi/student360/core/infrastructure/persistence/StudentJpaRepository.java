package co.edu.icesi.student360.core.infrastructure.persistence;

import co.edu.icesi.student360.core.domain.model.Student;
import co.edu.icesi.student360.core.domain.port.StudentRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentJpaRepository extends JpaRepository<Student, String>, StudentRepository {}
