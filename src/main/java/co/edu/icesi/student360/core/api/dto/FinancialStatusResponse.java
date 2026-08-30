package co.edu.icesi.student360.core.api.dto;

import co.edu.icesi.student360.core.domain.model.FinancialStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record FinancialStatusResponse(
    String studentId,
    BigDecimal outstandingBalance,
    BigDecimal overdueBalance,
    int daysOverdue,
    boolean overdue,
    boolean paymentPlan,
    boolean financialHold,
    Instant updatedAt) {

  public static FinancialStatusResponse from(FinancialStatus status) {
    return new FinancialStatusResponse(
        status.getStudentId(),
        status.getOutstandingBalance(),
        status.getOverdueBalance(),
        status.getDaysOverdue(),
        status.isOverdue(),
        status.hasPaymentPlan(),
        status.hasFinancialHold(),
        status.getUpdatedAt());
  }
}
