package co.edu.icesi.student360.core.application.query;

import co.edu.icesi.student360.common.api.exception.NotFoundException;
import co.edu.icesi.student360.common.audit.Audited;
import co.edu.icesi.student360.core.application.query.model.DirectoryProfileModel;
import co.edu.icesi.student360.core.domain.model.CourseOffering;
import co.edu.icesi.student360.core.domain.port.CourseOfferingRepository;
import co.edu.icesi.student360.core.domain.port.ProfessorRepository;
import co.edu.icesi.student360.core.domain.port.StudentRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Resolves one directory reference — {@code S-1001} or {@code PROF-4} — to the card network-service
 * shows when a person in a support-network graph is opened.
 *
 * <p>Audited as a data access: looking up how to contact someone is exactly the kind of read the
 * trail should be able to answer for afterwards, even though the fields themselves are the
 * directory's public ones.
 */
@Service
public class FindDirectoryProfileQueryHandler {

  static final String RESOURCE = "Directory entry";
  private static final String PROFESSOR_PREFIX = "PROF-";

  private final StudentRepository students;
  private final ProfessorRepository professors;
  private final CourseOfferingRepository offerings;

  public FindDirectoryProfileQueryHandler(
      StudentRepository students,
      ProfessorRepository professors,
      CourseOfferingRepository offerings) {
    this.students = students;
    this.professors = professors;
    this.offerings = offerings;
  }

  @Audited(action = "READ_DIRECTORY_PROFILE", subjectType = "DIRECTORY_ENTRY")
  @Transactional(readOnly = true)
  public DirectoryProfileModel handle(FindDirectoryProfileQuery query) {
    String reference = query.reference() == null ? "" : query.reference().trim();
    return reference.startsWith(PROFESSOR_PREFIX)
        ? professorProfile(reference)
        : studentProfile(reference);
  }

  private DirectoryProfileModel studentProfile(String reference) {
    return students
        .findById(reference)
        .map(DirectoryProfileModel::ofStudent)
        .orElseThrow(() -> new NotFoundException(RESOURCE, reference));
  }

  private DirectoryProfileModel professorProfile(String reference) {
    long professorId = parseProfessorId(reference);
    return professors
        .findById(professorId)
        .map(
            professor ->
                DirectoryProfileModel.ofProfessor(professor, currentOfferings(professorId)))
        .orElseThrow(() -> new NotFoundException(RESOURCE, reference));
  }

  /**
   * Only the newest term's offerings: the card says what they teach now, not their whole history.
   */
  private List<CourseOffering> currentOfferings(long professorId) {
    List<CourseOffering> all = offerings.findByProfessorIdOrderByTermDescCourseCodeAsc(professorId);
    if (all.isEmpty()) {
      return List.of();
    }
    String currentTerm = all.get(0).getTerm();
    return all.stream().filter(offering -> offering.getTerm().equals(currentTerm)).toList();
  }

  private long parseProfessorId(String reference) {
    try {
      return Long.parseLong(reference.substring(PROFESSOR_PREFIX.length()));
    } catch (NumberFormatException exception) {
      // A malformed PROF-<id> names nothing rather than being a bad request: the caller passed a
      // reference we simply do not have, which is what a 404 says.
      throw new NotFoundException(RESOURCE, reference);
    }
  }
}
