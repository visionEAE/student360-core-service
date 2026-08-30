-- Seed consistent with auth-service (same S-* ids as the ref claims) and with lms-service.
-- S-1001 is engaged and current; S-1002 is in between; S-1003 is the at-risk student the
-- demonstration thread must detect: overdue balance, financial hold, AT_RISK standing.

INSERT INTO core.program (code, name, faculty) VALUES
    ('ISI', 'Systems Engineering', 'Engineering'),
    ('ADM', 'Business Administration', 'Business and Economics'),
    ('PSI', 'Psychology', 'Social Sciences');

INSERT INTO core.student (id, first_name, last_name, email, program_id, admission_term, status, created_at) VALUES
    ('S-1001', 'Ana',   'Torres', 'ana.torres@u.icesi.edu.co',  (SELECT id FROM core.program WHERE code = 'ISI'), '2024-1', 'ACTIVE', now()),
    ('S-1002', 'Luis',  'Gómez',  'luis.gomez@u.icesi.edu.co',  (SELECT id FROM core.program WHERE code = 'ADM'), '2024-2', 'ACTIVE', now()),
    ('S-1003', 'María', 'Rojas',  'maria.rojas@u.icesi.edu.co', (SELECT id FROM core.program WHERE code = 'PSI'), '2025-1', 'ACTIVE', now());

INSERT INTO core.enrollment (student_id, term, credits_enrolled, credits_approved, term_gpa, cumulative_gpa, academic_standing) VALUES
    ('S-1001', '2026-1', 18, 18, 4.35, 4.28, 'GOOD'),
    ('S-1001', '2026-2', 18, 0,  NULL, 4.28, 'GOOD'),
    ('S-1002', '2026-1', 15, 12, 3.40, 3.55, 'GOOD'),
    ('S-1002', '2026-2', 15, 0,  NULL, 3.55, 'PROBATION'),
    ('S-1003', '2026-1', 16, 9,  2.70, 2.95, 'PROBATION'),
    ('S-1003', '2026-2', 12, 0,  NULL, 2.95, 'AT_RISK');

INSERT INTO core.financial_status (student_id, outstanding_balance, overdue_balance, days_overdue, payment_plan, financial_hold, updated_at) VALUES
    ('S-1001',       0.00,       0.00,  0, FALSE, FALSE, now()),
    ('S-1002', 2450000.00,       0.00,  0, TRUE,  FALSE, now()),
    ('S-1003', 6800000.00, 4100000.00, 62, FALSE, TRUE,  now());
