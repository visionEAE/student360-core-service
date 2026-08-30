package co.edu.icesi.student360.core.domain.port;

import co.edu.icesi.student360.core.domain.model.CourseGrade;
import java.util.List;

public interface CourseGradeRepository {

  List<CourseGrade> findByStudentIdAndTermOrderByCourseCode(String studentId, String term);
}
