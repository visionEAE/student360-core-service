package co.edu.icesi.student360.core.infrastructure.persistence;

import co.edu.icesi.student360.core.domain.model.FinancialStatus;
import co.edu.icesi.student360.core.domain.port.FinancialStatusRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancialStatusJpaRepository
    extends JpaRepository<FinancialStatus, Long>, FinancialStatusRepository {}
