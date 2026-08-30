package co.edu.icesi.student360.core.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "program", schema = "core")
public class Program {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, unique = true)
  private String code;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String faculty;

  @Column(name = "total_semesters", nullable = false)
  private int totalSemesters;

  protected Program() {}

  public Integer getId() {
    return id;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public String getFaculty() {
    return faculty;
  }

  public int getTotalSemesters() {
    return totalSemesters;
  }
}
