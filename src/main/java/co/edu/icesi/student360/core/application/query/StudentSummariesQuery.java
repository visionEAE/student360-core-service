package co.edu.icesi.student360.core.application.query;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Read a summary of several students at once (advisor overview). */
public record StudentSummariesQuery(List<String> studentIds) {

  public static final int MAX_IDS = 100;

  public StudentSummariesQuery {
    Set<String> unique = new LinkedHashSet<>(studentIds);
    if (unique.isEmpty() || unique.size() > MAX_IDS) {
      throw new IllegalArgumentException("ids must contain between 1 and " + MAX_IDS + " values");
    }
    studentIds = List.copyOf(unique);
  }

  /** The audit aspect records the first argument's string form as the subject id. */
  @Override
  public String toString() {
    return String.join(",", studentIds);
  }
}
