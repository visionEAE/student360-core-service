-- core schema: the simulated SIS (students, programs, enrollment) and ERP (financial status).
-- Owned by core_user, migrated only by core-service. Schema-qualified on purpose.

CREATE TABLE core.program (
    id       SERIAL PRIMARY KEY,
    code     TEXT NOT NULL,
    name     TEXT NOT NULL,
    faculty  TEXT NOT NULL,
    CONSTRAINT uq_program_code UNIQUE (code)
);

-- The student id (S-1001, ...) is the external_reference auth-service puts in the ref claim and
-- the student_reference lms-service and support-service store: it is the cross-service key.
CREATE TABLE core.student (
    id               TEXT PRIMARY KEY,
    first_name       TEXT        NOT NULL,
    last_name        TEXT        NOT NULL,
    email            TEXT        NOT NULL,
    program_id       INT         NOT NULL REFERENCES core.program (id),
    admission_term   TEXT        NOT NULL,
    status           TEXT        NOT NULL,       -- ACTIVE | ON_LEAVE | WITHDRAWN
    created_at       TIMESTAMPTZ NOT NULL,
    CONSTRAINT uq_student_email UNIQUE (email),
    CONSTRAINT chk_student_status CHECK (status IN ('ACTIVE', 'ON_LEAVE', 'WITHDRAWN'))
);

CREATE INDEX idx_student_program ON core.student (program_id);

CREATE TABLE core.enrollment (
    id                 BIGSERIAL PRIMARY KEY,
    student_id         TEXT          NOT NULL REFERENCES core.student (id),
    term               TEXT          NOT NULL,   -- 2026-1, 2026-2
    credits_enrolled   INT           NOT NULL,
    credits_approved   INT           NOT NULL,
    term_gpa           NUMERIC(3, 2),            -- null while the term is in progress
    cumulative_gpa     NUMERIC(3, 2) NOT NULL,
    academic_standing  TEXT          NOT NULL,   -- GOOD | PROBATION | AT_RISK
    CONSTRAINT uq_enrollment_student_term UNIQUE (student_id, term),
    CONSTRAINT chk_enrollment_standing CHECK (academic_standing IN ('GOOD', 'PROBATION', 'AT_RISK'))
);

CREATE INDEX idx_enrollment_student ON core.enrollment (student_id, term DESC);

CREATE TABLE core.financial_status (
    id                   BIGSERIAL PRIMARY KEY,
    student_id           TEXT           NOT NULL REFERENCES core.student (id),
    outstanding_balance  NUMERIC(12, 2) NOT NULL,
    overdue_balance      NUMERIC(12, 2) NOT NULL,
    days_overdue         INT            NOT NULL,
    payment_plan         BOOLEAN        NOT NULL,
    financial_hold       BOOLEAN        NOT NULL,
    updated_at           TIMESTAMPTZ    NOT NULL,
    CONSTRAINT uq_financial_status_student UNIQUE (student_id)
);
