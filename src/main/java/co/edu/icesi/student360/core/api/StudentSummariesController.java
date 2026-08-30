package co.edu.icesi.student360.core.api;

import co.edu.icesi.student360.core.application.query.StudentSummariesQuery;
import co.edu.icesi.student360.core.application.query.StudentSummariesQueryHandler;
import co.edu.icesi.student360.core.application.query.model.StudentSummaryModel;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Separate controller so the literal path never competes with {@code /students/{id}}. */
@RestController
@Validated
public class StudentSummariesController {

  private final StudentSummariesQueryHandler handler;

  public StudentSummariesController(StudentSummariesQueryHandler handler) {
    this.handler = handler;
  }

  @GetMapping("/api/core/students/summaries")
  public List<StudentSummaryModel> summaries(@RequestParam @NotEmpty List<String> ids) {
    return handler.handle(new StudentSummariesQuery(ids));
  }
}
