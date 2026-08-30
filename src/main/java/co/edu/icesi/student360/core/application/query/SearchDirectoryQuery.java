package co.edu.icesi.student360.core.application.query;

/**
 * {@code kind} narrows the search to {@code STUDENT} or {@code PROFESSOR}; {@code null} searches
 * both.
 */
public record SearchDirectoryQuery(String text, String kind) {}
