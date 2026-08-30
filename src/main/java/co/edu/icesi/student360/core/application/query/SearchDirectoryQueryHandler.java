package co.edu.icesi.student360.core.application.query;

import co.edu.icesi.student360.core.application.query.model.DirectoryEntryModel;
import co.edu.icesi.student360.core.domain.port.ProfessorRepository;
import co.edu.icesi.student360.core.domain.port.StudentRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Backs the support-network picker (network-service, {@code docs/network-contract.md}): a professor
 * or peer added to a student's support network must be a real person from the SIS, not a freely
 * typed name, so the graph can be traced back to core-service's own record.
 */
@Service
public class SearchDirectoryQueryHandler {

  private static final int MIN_QUERY_LENGTH = 2;

  private final StudentRepository students;
  private final ProfessorRepository professors;

  public SearchDirectoryQueryHandler(StudentRepository students, ProfessorRepository professors) {
    this.students = students;
    this.professors = professors;
  }

  @Transactional(readOnly = true)
  public List<DirectoryEntryModel> handle(SearchDirectoryQuery query) {
    String text = query.text() == null ? "" : query.text().trim();
    if (text.length() < MIN_QUERY_LENGTH) {
      return List.of();
    }
    List<DirectoryEntryModel> results = new ArrayList<>();
    if (wants(query.kind(), "STUDENT")) {
      students
          .findTop10ByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrderByLastName(
              text, text)
          .forEach(student -> results.add(DirectoryEntryModel.ofStudent(student)));
    }
    if (wants(query.kind(), "PROFESSOR")) {
      professors
          .findTop10ByFullNameContainingIgnoreCaseOrderByFullName(text)
          .forEach(professor -> results.add(DirectoryEntryModel.ofProfessor(professor)));
    }
    return results;
  }

  private static boolean wants(String requestedKind, String kind) {
    return requestedKind == null || requestedKind.isBlank() || kind.equalsIgnoreCase(requestedKind);
  }
}
