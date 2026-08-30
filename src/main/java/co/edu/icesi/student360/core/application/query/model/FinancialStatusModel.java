package co.edu.icesi.student360.core.application.query.model;

import co.edu.icesi.student360.core.domain.model.FinancialStatus;
import co.edu.icesi.student360.core.domain.model.TuitionPayment;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/** Contract v2 {@code FinancialStatus}: the account picture plus the instalment history. */
public record FinancialStatusModel(
    String studentId,
    BigDecimal tuitionAmount,
    BigDecimal paidAmount,
    BigDecimal outstandingBalance,
    BigDecimal overdueBalance,
    int daysOverdue,
    boolean overdue,
    LocalDate dueDate,
    String paymentPlan,
    String scholarship,
    boolean financialHold,
    List<PaymentModel> payments,
    Instant updatedAt) {

  public record PaymentModel(
      LocalDate date, String description, BigDecimal amount, String status) {}

  public static FinancialStatusModel from(FinancialStatus status, List<TuitionPayment> payments) {
    return new FinancialStatusModel(
        status.getStudentId(),
        status.getTuitionAmount(),
        status.getPaidAmount(),
        status.getOutstandingBalance(),
        status.getOverdueBalance(),
        status.getDaysOverdue(),
        status.isOverdue(),
        status.getDueDate(),
        status.getPaymentPlanDescription(),
        status.getScholarship(),
        status.hasFinancialHold(),
        payments.stream()
            .map(
                p ->
                    new PaymentModel(
                        p.getPaidAt() != null ? p.getPaidAt() : p.getDueDate(),
                        p.getDescription(),
                        p.getAmount(),
                        p.getStatus().name()))
            .toList(),
        status.getUpdatedAt());
  }
}
