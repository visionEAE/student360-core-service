package co.edu.icesi.student360.core.application.query;

/** Read one student's financial status and instalments. */
public record FindFinancialStatusQuery(String studentId) {

  /** The audit aspect records the first argument's string form as the subject id. */
  @Override
  public String toString() {
    return studentId;
  }
}
