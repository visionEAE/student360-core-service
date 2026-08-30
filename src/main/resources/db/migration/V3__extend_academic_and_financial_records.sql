-- Contract v2: what the 360 view shows beyond the first proof of concept — institutional code,
-- semester progress, the gradebook of the current term, and the tuition/payment picture.
-- Columns are added nullable here and completed by the seed migration that follows.

ALTER TABLE core.program ADD COLUMN total_semesters INT NOT NULL DEFAULT 10;

ALTER TABLE core.student ADD COLUMN code TEXT;
ALTER TABLE core.student ADD COLUMN current_semester INT NOT NULL DEFAULT 1;

ALTER TABLE core.enrollment ADD COLUMN semester_number INT;

ALTER TABLE core.financial_status ADD COLUMN tuition_amount NUMERIC(12, 2) NOT NULL DEFAULT 0;
ALTER TABLE core.financial_status ADD COLUMN paid_amount    NUMERIC(12, 2) NOT NULL DEFAULT 0;
ALTER TABLE core.financial_status ADD COLUMN due_date       DATE;
ALTER TABLE core.financial_status ADD COLUMN scholarship    TEXT;      -- null = none

-- payment_plan was a boolean ("has one?"); the contract needs the plan's short description
-- instead (null = none active), so the column is replaced rather than repurposed in place.
ALTER TABLE core.financial_status DROP COLUMN payment_plan;
ALTER TABLE core.financial_status ADD COLUMN payment_plan TEXT;

-- Official gradebook (SIS): one row per student, term and course.
CREATE TABLE core.course_grade (
    id             BIGSERIAL PRIMARY KEY,
    student_id     TEXT          NOT NULL REFERENCES core.student (id),
    term           TEXT          NOT NULL,
    course_code    TEXT          NOT NULL,
    course_name    TEXT          NOT NULL,
    credits        INT           NOT NULL,
    current_grade  NUMERIC(3, 2),                      -- null until something is graded
    CONSTRAINT uq_course_grade UNIQUE (student_id, term, course_code)
);

CREATE INDEX idx_course_grade_student_term ON core.course_grade (student_id, term);

-- Tuition instalments (ERP): what was charged, when it was due, whether it was paid.
CREATE TABLE core.tuition_payment (
    id           BIGSERIAL PRIMARY KEY,
    student_id   TEXT           NOT NULL REFERENCES core.student (id),
    due_date     DATE           NOT NULL,
    paid_at      DATE,
    description  TEXT           NOT NULL,
    amount       NUMERIC(12, 2) NOT NULL,
    status       TEXT           NOT NULL,             -- PAID | PENDING | OVERDUE
    CONSTRAINT chk_tuition_payment_status CHECK (status IN ('PAID', 'PENDING', 'OVERDUE')),
    CONSTRAINT chk_tuition_payment_paid CHECK ((status = 'PAID') = (paid_at IS NOT NULL))
);

CREATE INDEX idx_tuition_payment_student ON core.tuition_payment (student_id, due_date);
