-- Completes the three original students with the contract v2 fields and adds three advisees
-- without a login (S-1004, S-1005, S-1006) so the advisor overview has something to show.
-- S-1003 keeps the design's financial numbers; her academic history declines so her AT_RISK
-- standing stays coherent with the risk rule.

UPDATE core.program SET total_semesters = 10 WHERE code IN ('ISI', 'ADM', 'PSI');
INSERT INTO core.program (code, name, faculty, total_semesters) VALUES
    ('MED', 'Medicine', 'Health Sciences', 12),
    ('DIS', 'Design',   'Engineering',      8);

-- Codes and progress of the original students. S-1003's admission moves back so that 2026-2 is
-- her seventh semester, as the design shows.
UPDATE core.student SET code = '2024145001', current_semester = 6 WHERE id = 'S-1001';
UPDATE core.student SET code = '2024287412', current_semester = 5 WHERE id = 'S-1002';
UPDATE core.student SET code = '2025145032', current_semester = 7, admission_term = '2023-2' WHERE id = 'S-1003';

INSERT INTO core.student (id, code, first_name, last_name, email, program_id, admission_term, current_semester, status, created_at) VALUES
    ('S-1004', '2019102873', 'Daniel',    'Herrera', 'daniel.herrera@u.icesi.edu.co',   (SELECT id FROM core.program WHERE code = 'MED'), '2022-1', 10, 'ACTIVE', now()),
    ('S-1005', '2020098215', 'Camila',    'Torres',  'camila.torres@u.icesi.edu.co',    (SELECT id FROM core.program WHERE code = 'PSI'), '2022-2',  9, 'ACTIVE', now()),
    ('S-1006', '2021077310', 'Valentina', 'Ospina',  'valentina.ospina@u.icesi.edu.co', (SELECT id FROM core.program WHERE code = 'DIS'), '2024-1',  6, 'ACTIVE', now());

ALTER TABLE core.student ALTER COLUMN code SET NOT NULL;
ALTER TABLE core.student ADD CONSTRAINT uq_student_code UNIQUE (code);

-- Semester numbers for the terms seeded before, then the earlier terms that make up the history.
UPDATE core.enrollment SET semester_number = 5 WHERE student_id = 'S-1001' AND term = '2026-1';
UPDATE core.enrollment SET semester_number = 6 WHERE student_id = 'S-1001' AND term = '2026-2';
UPDATE core.enrollment SET semester_number = 4 WHERE student_id = 'S-1002' AND term = '2026-1';
UPDATE core.enrollment SET semester_number = 5 WHERE student_id = 'S-1002' AND term = '2026-2';
UPDATE core.enrollment SET semester_number = 6 WHERE student_id = 'S-1003' AND term = '2026-1';
UPDATE core.enrollment SET semester_number = 7 WHERE student_id = 'S-1003' AND term = '2026-2';

INSERT INTO core.enrollment (student_id, term, semester_number, credits_enrolled, credits_approved, term_gpa, cumulative_gpa, academic_standing) VALUES
    ('S-1001', '2024-1', 1, 16, 16, 4.10, 4.10, 'GOOD'),
    ('S-1001', '2024-2', 2, 17, 17, 4.20, 4.15, 'GOOD'),
    ('S-1001', '2025-1', 3, 18, 18, 4.30, 4.20, 'GOOD'),
    ('S-1001', '2025-2', 4, 18, 18, 4.25, 4.21, 'GOOD'),
    ('S-1002', '2024-2', 1, 15, 15, 3.70, 3.70, 'GOOD'),
    ('S-1002', '2025-1', 2, 15, 15, 3.60, 3.65, 'GOOD'),
    ('S-1002', '2025-2', 3, 15, 12, 3.45, 3.58, 'GOOD'),
    ('S-1003', '2023-2', 1, 15, 15, 3.40, 3.40, 'GOOD'),
    ('S-1003', '2024-1', 2, 16, 16, 3.60, 3.50, 'GOOD'),
    ('S-1003', '2024-2', 3, 16, 16, 3.50, 3.50, 'GOOD'),
    ('S-1003', '2025-1', 4, 16, 13, 3.20, 3.42, 'GOOD'),
    ('S-1003', '2025-2', 5, 16, 10, 3.00, 3.32, 'PROBATION'),
    ('S-1004', '2025-1', 7, 18, 12, 2.90, 3.05, 'PROBATION'),
    ('S-1004', '2025-2', 8, 18, 10, 2.70, 2.98, 'PROBATION'),
    ('S-1004', '2026-1', 9, 16,  8, 2.60, 2.85, 'AT_RISK'),
    ('S-1004', '2026-2', 10, 14, 0, NULL, 2.85, 'AT_RISK'),
    ('S-1005', '2025-1', 6, 16, 16, 3.30, 3.35, 'GOOD'),
    ('S-1005', '2025-2', 7, 16, 12, 2.95, 3.20, 'PROBATION'),
    ('S-1005', '2026-1', 8, 16, 10, 2.80, 3.05, 'AT_RISK'),
    ('S-1005', '2026-2', 9, 15, 0, NULL, 3.05, 'AT_RISK'),
    ('S-1006', '2025-1', 3, 17, 17, 4.20, 4.25, 'GOOD'),
    ('S-1006', '2025-2', 4, 17, 17, 4.35, 4.28, 'GOOD'),
    ('S-1006', '2026-1', 5, 18, 18, 4.40, 4.31, 'GOOD'),
    ('S-1006', '2026-2', 6, 18, 0, NULL, 4.31, 'GOOD');

-- Gradebook of the current term (2026-2). Codes match the courses lms-service seeds.
INSERT INTO core.course_grade (student_id, term, course_code, course_name, credits, current_grade) VALUES
    ('S-1001', '2026-2', 'ISI-301', 'Software Architecture', 3, 4.40),
    ('S-1001', '2026-2', 'MAT-201', 'Calculus II',           4, 4.10),
    ('S-1001', '2026-2', 'HUM-110', 'Critical Thinking',     3, 4.50),
    ('S-1002', '2026-2', 'ISI-301', 'Software Architecture', 3, 3.40),
    ('S-1002', '2026-2', 'MAT-201', 'Calculus II',           4, 3.10),
    ('S-1002', '2026-2', 'HUM-110', 'Critical Thinking',     3, 3.80),
    ('S-1003', '2026-2', 'PSI-301', 'Psicopatología',              3, 3.20),
    ('S-1003', '2026-2', 'PSI-310', 'Neuropsicología',             3, 3.60),
    ('S-1003', '2026-2', 'PSI-320', 'Psicología Organizacional',   3, 3.90),
    ('S-1003', '2026-2', 'EST-201', 'Estadística II',              4, 2.80),
    ('S-1003', '2026-2', 'HUM-210', 'Ética Profesional',           3, 3.50),
    ('S-1004', '2026-2', 'MED-501', 'Medicina Interna',            6, 2.70),
    ('S-1004', '2026-2', 'MED-510', 'Farmacología Clínica',        4, 2.90),
    ('S-1004', '2026-2', 'MED-520', 'Salud Pública',               4, 3.10),
    ('S-1005', '2026-2', 'PSI-401', 'Psicología Clínica',          4, 2.90),
    ('S-1005', '2026-2', 'PSI-410', 'Evaluación Psicológica',      4, 3.00),
    ('S-1005', '2026-2', 'PSI-420', 'Psicología Social',           3, 3.30),
    ('S-1005', '2026-2', 'HUM-210', 'Ética Profesional',           3, 3.40),
    ('S-1006', '2026-2', 'DIS-301', 'Diseño de Interacción',       4, 4.50),
    ('S-1006', '2026-2', 'DIS-310', 'Tipografía',                  3, 4.20),
    ('S-1006', '2026-2', 'DIS-320', 'Diseño de Producto',          4, 4.30),
    ('S-1006', '2026-2', 'HUM-110', 'Critical Thinking',           3, 4.40);

-- Tuition picture. S-1003: 8 500 000 charged, 7 260 000 paid, the last instalment overdue.
UPDATE core.financial_status SET tuition_amount = 7900000, paid_amount = 7900000, due_date = DATE '2026-08-15', scholarship = NULL WHERE student_id = 'S-1001';
UPDATE core.financial_status SET tuition_amount = 7350000, paid_amount = 4900000, due_date = DATE '2026-10-15', payment_plan = 'Plan de 3 cuotas' WHERE student_id = 'S-1002';
UPDATE core.financial_status SET tuition_amount = 8500000, paid_amount = 7260000, outstanding_balance = 1240000, overdue_balance = 1240000,
       days_overdue = GREATEST(0, (CURRENT_DATE - DATE '2026-08-15')), due_date = DATE '2026-08-15', scholarship = '20% por mérito académico'
 WHERE student_id = 'S-1003';

INSERT INTO core.financial_status (student_id, outstanding_balance, overdue_balance, days_overdue, payment_plan, financial_hold, tuition_amount, paid_amount, due_date, scholarship, updated_at) VALUES
    ('S-1004', 5800000, 2900000, GREATEST(0, (CURRENT_DATE - DATE '2026-07-15')), NULL, TRUE,  11600000, 5800000, DATE '2026-07-15', NULL, now()),
    ('S-1005',       0,       0, 0, NULL, FALSE,  8200000, 8200000, DATE '2026-08-15', '50% beca socioeconómica', now()),
    ('S-1006',       0,       0, 0, NULL, FALSE,  9100000, 9100000, DATE '2026-08-15', NULL, now());

INSERT INTO core.tuition_payment (student_id, due_date, paid_at, description, amount, status) VALUES
    ('S-1001', DATE '2026-02-20', DATE '2026-02-18', 'Derechos de matrícula',   600000, 'PAID'),
    ('S-1001', DATE '2026-05-15', DATE '2026-05-10', 'Cuota de matrícula 1/3', 2433334, 'PAID'),
    ('S-1001', DATE '2026-07-10', DATE '2026-07-08', 'Cuota de matrícula 2/3', 2433333, 'PAID'),
    ('S-1001', DATE '2026-08-15', DATE '2026-08-12', 'Cuota de matrícula 3/3', 2433333, 'PAID'),
    ('S-1002', DATE '2026-02-20', DATE '2026-02-20', 'Derechos de matrícula',   600000, 'PAID'),
    ('S-1002', DATE '2026-05-15', DATE '2026-05-20', 'Cuota de matrícula 1/3', 2250000, 'PAID'),
    ('S-1002', DATE '2026-08-15', DATE '2026-08-14', 'Cuota de matrícula 2/3', 2050000, 'PAID'),
    ('S-1002', DATE '2026-10-15', NULL,              'Cuota de matrícula 3/3', 2450000, 'PENDING'),
    ('S-1003', DATE '2026-02-20', DATE '2026-02-20', 'Derechos de matrícula',   600000, 'PAID'),
    ('S-1003', DATE '2026-05-15', DATE '2026-05-15', 'Cuota de matrícula 1/3', 2833000, 'PAID'),
    ('S-1003', DATE '2026-07-10', DATE '2026-07-10', 'Cuota de matrícula 2/3', 2833000, 'PAID'),
    ('S-1003', DATE '2026-08-15', NULL,              'Cuota de matrícula 3/3', 1240000, 'OVERDUE'),
    ('S-1004', DATE '2026-02-20', DATE '2026-02-25', 'Derechos de matrícula',   800000, 'PAID'),
    ('S-1004', DATE '2026-05-15', DATE '2026-06-02', 'Cuota de matrícula 1/3', 5000000, 'PAID'),
    ('S-1004', DATE '2026-07-15', NULL,              'Cuota de matrícula 2/3', 2900000, 'OVERDUE'),
    ('S-1004', DATE '2026-09-15', NULL,              'Cuota de matrícula 3/3', 2900000, 'PENDING'),
    ('S-1005', DATE '2026-02-20', DATE '2026-02-19', 'Derechos de matrícula',   600000, 'PAID'),
    ('S-1005', DATE '2026-05-15', DATE '2026-05-12', 'Cuota de matrícula 1/2', 3800000, 'PAID'),
    ('S-1005', DATE '2026-08-15', DATE '2026-08-10', 'Cuota de matrícula 2/2', 3800000, 'PAID'),
    ('S-1006', DATE '2026-02-20', DATE '2026-02-15', 'Derechos de matrícula',   600000, 'PAID'),
    ('S-1006', DATE '2026-05-15', DATE '2026-05-14', 'Cuota de matrícula 1/2', 4250000, 'PAID'),
    ('S-1006', DATE '2026-08-15', DATE '2026-08-13', 'Cuota de matrícula 2/2', 4250000, 'PAID');
