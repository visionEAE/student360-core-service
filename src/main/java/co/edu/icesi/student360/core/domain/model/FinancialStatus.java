package co.edu.icesi.student360.core.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/** The ERP's view of a student's account: the most sensitive data this service owns. */
@Entity
@Table(name = "financial_status", schema = "core")
public class FinancialStatus {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "student_id", nullable = false, unique = true)
  private String studentId;

  @Column(name = "outstanding_balance", nullable = false)
  private BigDecimal outstandingBalance;

  @Column(name = "overdue_balance", nullable = false)
  private BigDecimal overdueBalance;

  @Column(name = "days_overdue", nullable = false)
  private int daysOverdue;

  @Column(name = "financial_hold", nullable = false)
  private boolean financialHold;

  @Column(name = "tuition_amount", nullable = false)
  private BigDecimal tuitionAmount;

  @Column(name = "paid_amount", nullable = false)
  private BigDecimal paidAmount;

  @Column(name = "due_date")
  private LocalDate dueDate;

  @Column(name = "payment_plan")
  private String paymentPlan;

  @Column private String scholarship;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  protected FinancialStatus() {}

  public String getStudentId() {
    return studentId;
  }

  public BigDecimal getOutstandingBalance() {
    return outstandingBalance;
  }

  public BigDecimal getOverdueBalance() {
    return overdueBalance;
  }

  public int getDaysOverdue() {
    return daysOverdue;
  }

  public BigDecimal getTuitionAmount() {
    return tuitionAmount;
  }

  public BigDecimal getPaidAmount() {
    return paidAmount;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  /** Short description of the active plan, or null when none. */
  public String getPaymentPlanDescription() {
    return paymentPlan;
  }

  public String getScholarship() {
    return scholarship;
  }

  public boolean hasFinancialHold() {
    return financialHold;
  }

  public boolean isOverdue() {
    return overdueBalance.signum() > 0;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
