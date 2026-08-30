package co.edu.icesi.student360.core.domain.model;

import java.util.List;

/** The academic picture composed from the enrollment history: current term first. */
public record AcademicStatus(String studentId, Enrollment current, List<Enrollment> history) {}
