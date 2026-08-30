package co.edu.icesi.student360.core.application.query;

/** {@code reference} is a student id ({@code S-1001}) or a professor one ({@code PROF-4}). */
public record FindDirectoryProfileQuery(String reference) {

  /** The audit aspect records the first argument's string form as the subject id. */
  @Override
  public String toString() {
    return reference;
  }
}
