-- Fills out the advisor overview with the mix of risk levels the design mockup shows: four more
-- advisees, reusing the ISI program and adding three new ones (Law, Civil Engineering, Economics).

INSERT INTO core.program (code, name, faculty, total_semesters) VALUES
    ('DER', 'Law',                 'Law and Social Sciences', 10),
    ('CIV', 'Civil Engineering',   'Engineering',             10),
    ('ECO', 'Economics',           'Business and Economics',   9);

INSERT INTO core.student (id, code, first_name, last_name, email, program_id, admission_term, current_semester, status, created_at) VALUES
    ('S-1007', '2021087441', 'Juan Pablo',    'Gómez',  'juan.gomez@u.icesi.edu.co',    (SELECT id FROM core.program WHERE code = 'ISI'), '2022-1', 9, 'ACTIVE', now()),
    ('S-1008', '2022041098', 'Santiago',      'Molina', 'santiago.molina@u.icesi.edu.co', (SELECT id FROM core.program WHERE code = 'DER'), '2023-1', 7, 'ACTIVE', now()),
    ('S-1009', '2020156734', 'Isabella',      'Zapata', 'isabella.zapata@u.icesi.edu.co', (SELECT id FROM core.program WHERE code = 'CIV'), '2021-1', 10, 'ACTIVE', now()),
    ('S-1010', '2019134422', 'Andrés Felipe', 'Ruiz',   'andres.ruiz@u.icesi.edu.co',    (SELECT id FROM core.program WHERE code = 'ECO'), '2020-2', 11, 'ACTIVE', now());

-- Juan Pablo: on track. Santiago: academic watch (probation). Isabella: on track academically but
-- behind financially. Andrés: on track, carrying a balance on a plan (financial watch).
INSERT INTO core.enrollment (student_id, term, semester_number, credits_enrolled, credits_approved, term_gpa, cumulative_gpa, academic_standing) VALUES
    ('S-1007', '2025-1', 7, 17, 17, 4.10, 3.95, 'GOOD'),
    ('S-1007', '2025-2', 8, 17, 17, 4.05, 3.97, 'GOOD'),
    ('S-1007', '2026-1', 9, 18, 15, 3.85, 3.95, 'GOOD'),
    ('S-1007', '2026-2', 9, 18, 0,  NULL, 3.95, 'GOOD'),
    ('S-1008', '2025-1', 5, 16, 13, 3.10, 3.28, 'GOOD'),
    ('S-1008', '2025-2', 6, 16, 11, 2.95, 3.18, 'PROBATION'),
    ('S-1008', '2026-1', 7, 15, 10, 2.85, 3.08, 'PROBATION'),
    ('S-1008', '2026-2', 7, 15, 0,  NULL, 3.08, 'PROBATION'),
    ('S-1009', '2025-1', 9, 18, 18, 3.90, 3.88, 'GOOD'),
    ('S-1009', '2025-2', 10, 16, 16, 3.95, 3.90, 'GOOD'),
    ('S-1009', '2026-1', 10, 12, 12, 4.00, 3.92, 'GOOD'),
    ('S-1009', '2026-2', 10, 6,  0,  NULL, 3.92, 'GOOD'),
    ('S-1010', '2025-1', 10, 15, 15, 3.75, 3.70, 'GOOD'),
    ('S-1010', '2025-2', 11, 12, 12, 3.80, 3.72, 'GOOD'),
    ('S-1010', '2026-1', 11, 9,  9,  3.85, 3.74, 'GOOD'),
    ('S-1010', '2026-2', 11, 9,  0,  NULL, 3.74, 'GOOD');

INSERT INTO core.course_grade (student_id, term, course_code, course_name, credits, current_grade) VALUES
    ('S-1007', '2026-2', 'ISI-401', 'Cloud Computing',        3, 4.10),
    ('S-1007', '2026-2', 'ISI-410', 'Ingeniería de Software', 4, 3.90),
    ('S-1007', '2026-2', 'HUM-110', 'Critical Thinking',      3, 4.30),
    ('S-1008', '2026-2', 'DER-301', 'Derecho Penal',          4, 2.90),
    ('S-1008', '2026-2', 'DER-310', 'Derecho Laboral',        3, 3.10),
    ('S-1008', '2026-2', 'HUM-110', 'Critical Thinking',      3, 3.00),
    ('S-1009', '2026-2', 'CIV-501', 'Estructuras Avanzadas',  3, 4.00),
    ('S-1009', '2026-2', 'CIV-510', 'Geotecnia',              3, 3.85),
    ('S-1010', '2026-2', 'ECO-501', 'Econometría',            3, 3.70),
    ('S-1010', '2026-2', 'ECO-510', 'Finanzas Públicas',      3, 3.80),
    ('S-1010', '2026-2', 'HUM-210', 'Ética Profesional',      3, 3.75);

INSERT INTO core.financial_status (student_id, outstanding_balance, overdue_balance, days_overdue, payment_plan, financial_hold, tuition_amount, paid_amount, due_date, scholarship, updated_at) VALUES
    ('S-1007',        0,       0, 0, NULL,                    FALSE,  8900000, 8900000, DATE '2026-08-15', NULL, now()),
    ('S-1008',        0,       0, 0, NULL,                    FALSE,  7600000, 7600000, DATE '2026-08-15', '30% beca deportiva', now()),
    ('S-1009', 3200000, 3200000, GREATEST(0, (CURRENT_DATE - DATE '2026-07-20')), NULL, TRUE, 9800000, 6600000, DATE '2026-07-20', NULL, now()),
    ('S-1010', 2100000,       0, 0, 'Plan de 4 cuotas',       FALSE,  6500000, 4400000, DATE '2026-11-15', NULL, now());

INSERT INTO core.tuition_payment (student_id, due_date, paid_at, description, amount, status) VALUES
    ('S-1007', DATE '2026-02-20', DATE '2026-02-18', 'Derechos de matrícula', 600000, 'PAID'),
    ('S-1007', DATE '2026-08-15', DATE '2026-08-10', 'Matrícula del semestre', 8300000, 'PAID'),
    ('S-1008', DATE '2026-02-20', DATE '2026-02-19', 'Derechos de matrícula', 600000, 'PAID'),
    ('S-1008', DATE '2026-08-15', DATE '2026-08-14', 'Matrícula del semestre', 7000000, 'PAID'),
    ('S-1009', DATE '2026-02-20', DATE '2026-02-15', 'Derechos de matrícula', 600000, 'PAID'),
    ('S-1009', DATE '2026-05-15', DATE '2026-05-15', 'Cuota de matrícula 1/2', 3300000, 'PAID'),
    ('S-1009', DATE '2026-07-20', NULL,              'Cuota de matrícula 2/2', 3200000, 'OVERDUE'),
    ('S-1010', DATE '2026-02-20', DATE '2026-02-20', 'Derechos de matrícula', 500000, 'PAID'),
    ('S-1010', DATE '2026-06-15', DATE '2026-06-15', 'Cuota de matrícula 1/3', 1950000, 'PAID'),
    ('S-1010', DATE '2026-08-15', DATE '2026-08-15', 'Cuota de matrícula 2/3', 1950000, 'PAID'),
    ('S-1010', DATE '2026-11-15', NULL,              'Cuota de matrícula 3/3', 2100000, 'PENDING');
