package co.edu.icesi.student360.core.infrastructure.persistence;

import co.edu.icesi.student360.core.domain.model.TuitionPayment;
import co.edu.icesi.student360.core.domain.port.TuitionPaymentRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TuitionPaymentJpaRepository
    extends JpaRepository<TuitionPayment, Long>, TuitionPaymentRepository {}
