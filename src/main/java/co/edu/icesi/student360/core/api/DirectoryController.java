package co.edu.icesi.student360.core.api;

import co.edu.icesi.student360.core.application.query.FindDirectoryProfileQuery;
import co.edu.icesi.student360.core.application.query.FindDirectoryProfileQueryHandler;
import co.edu.icesi.student360.core.application.query.SearchDirectoryQuery;
import co.edu.icesi.student360.core.application.query.SearchDirectoryQueryHandler;
import co.edu.icesi.student360.core.application.query.model.DirectoryEntryModel;
import co.edu.icesi.student360.core.application.query.model.DirectoryProfileModel;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** HTTP ⇄ query translation only; open to any authenticated role (students search it too). */
@RestController
@RequestMapping("/api/core/directory")
public class DirectoryController {

  private final SearchDirectoryQueryHandler search;
  private final FindDirectoryProfileQueryHandler findProfile;

  public DirectoryController(
      SearchDirectoryQueryHandler search, FindDirectoryProfileQueryHandler findProfile) {
    this.search = search;
    this.findProfile = findProfile;
  }

  @GetMapping("/search")
  public List<DirectoryEntryModel> search(
      @RequestParam("q") String q, @RequestParam(value = "kind", required = false) String kind) {
    return search.handle(new SearchDirectoryQuery(q, kind));
  }

  @GetMapping("/{reference}")
  public DirectoryProfileModel profile(@PathVariable("reference") String reference) {
    return findProfile.handle(new FindDirectoryProfileQuery(reference));
  }
}
