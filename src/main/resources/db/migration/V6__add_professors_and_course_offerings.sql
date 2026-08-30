-- Support network contract (student360-infra/docs/network-contract.md §3): who currently teaches
-- a student is a deterministic fact, not an opinion, so it stays relational here rather than in
-- the rated support graph (network-service). No change to course_grade; course_offering is looked
-- up by (term, course_code).

CREATE TABLE core.professor (
    id          BIGSERIAL PRIMARY KEY,
    full_name   TEXT NOT NULL,
    email       TEXT,
    department  TEXT
);

CREATE TABLE core.course_offering (
    id             BIGSERIAL PRIMARY KEY,
    term           TEXT NOT NULL,
    course_code    TEXT NOT NULL,
    course_name    TEXT NOT NULL,
    professor_id   BIGINT NOT NULL REFERENCES core.professor (id),
    CONSTRAINT uq_course_offering_term_code UNIQUE (term, course_code)
);

CREATE INDEX idx_course_offering_term_code ON core.course_offering (term, course_code);

INSERT INTO core.professor (full_name, email, department) VALUES
    ('Dr. Andrés Salazar',      'andres.salazar@icesi.edu.co',      'Engineering'),
    ('Dra. Marcela Trujillo',   'marcela.trujillo@icesi.edu.co',    'Mathematics'),
    ('Dr. Felipe Correa',       'felipe.correa@icesi.edu.co',       'Humanities'),
    ('Dra. Lucía Fernández',    'lucia.fernandez@icesi.edu.co',     'Psychology'),
    ('Dr. Ricardo Vanegas',     'ricardo.vanegas@icesi.edu.co',     'Psychology'),
    ('Dra. Paula Escobar',      'paula.escobar@icesi.edu.co',       'Psychology'),
    ('Dr. Óscar Medina',        'oscar.medina@icesi.edu.co',        'Statistics'),
    ('Dra. Camila Restrepo',    'camila.restrepo@icesi.edu.co',     'Health Sciences'),
    ('Dr. Iván Cárdenas',       'ivan.cardenas@icesi.edu.co',       'Health Sciences'),
    ('Dra. Natalia Ríos',       'natalia.rios@icesi.edu.co',        'Health Sciences'),
    ('Dr. Julián Ospina',       'julian.ospina@icesi.edu.co',       'Design'),
    ('Dra. Valeria Muñoz',      'valeria.munoz@icesi.edu.co',       'Design'),
    ('Dr. Santiago Beltrán',    'santiago.beltran@icesi.edu.co',    'Design'),
    ('Dr. Mauricio Lara',       'mauricio.lara@icesi.edu.co',       'Engineering'),
    ('Dra. Diana Herrera',      'diana.herrera@icesi.edu.co',       'Law'),
    ('Dr. Sebastián Quintero',  'sebastian.quintero@icesi.edu.co',  'Law'),
    ('Dr. Carlos Pardo',        'carlos.pardo@icesi.edu.co',        'Engineering'),
    ('Dra. Estefanía Gómez',    'estefania.gomez@icesi.edu.co',     'Engineering'),
    ('Dr. Rodrigo Aponte',      'rodrigo.aponte@icesi.edu.co',      'Economics');

INSERT INTO core.course_offering (term, course_code, course_name, professor_id) VALUES
    ('2026-2', 'ISI-301', 'Software Architecture',       (SELECT id FROM core.professor WHERE full_name = 'Dr. Andrés Salazar')),
    ('2026-2', 'ISI-401', 'Cloud Computing',             (SELECT id FROM core.professor WHERE full_name = 'Dr. Carlos Pardo')),
    ('2026-2', 'ISI-410', 'Ingeniería de Software',      (SELECT id FROM core.professor WHERE full_name = 'Dra. Estefanía Gómez')),
    ('2026-2', 'MAT-201', 'Calculus II',                 (SELECT id FROM core.professor WHERE full_name = 'Dra. Marcela Trujillo')),
    ('2026-2', 'HUM-110', 'Critical Thinking',           (SELECT id FROM core.professor WHERE full_name = 'Dr. Felipe Correa')),
    ('2026-2', 'HUM-210', 'Ética Profesional',           (SELECT id FROM core.professor WHERE full_name = 'Dr. Felipe Correa')),
    ('2026-2', 'PSI-301', 'Psicopatología',              (SELECT id FROM core.professor WHERE full_name = 'Dra. Lucía Fernández')),
    ('2026-2', 'PSI-310', 'Neuropsicología',             (SELECT id FROM core.professor WHERE full_name = 'Dr. Ricardo Vanegas')),
    ('2026-2', 'PSI-320', 'Psicología Organizacional',   (SELECT id FROM core.professor WHERE full_name = 'Dra. Paula Escobar')),
    ('2026-2', 'PSI-401', 'Psicología Clínica',          (SELECT id FROM core.professor WHERE full_name = 'Dra. Lucía Fernández')),
    ('2026-2', 'PSI-410', 'Evaluación Psicológica',      (SELECT id FROM core.professor WHERE full_name = 'Dr. Ricardo Vanegas')),
    ('2026-2', 'PSI-420', 'Psicología Social',           (SELECT id FROM core.professor WHERE full_name = 'Dra. Paula Escobar')),
    ('2026-2', 'EST-201', 'Estadística II',              (SELECT id FROM core.professor WHERE full_name = 'Dr. Óscar Medina')),
    ('2026-2', 'MED-501', 'Medicina Interna',            (SELECT id FROM core.professor WHERE full_name = 'Dra. Camila Restrepo')),
    ('2026-2', 'MED-510', 'Farmacología Clínica',        (SELECT id FROM core.professor WHERE full_name = 'Dr. Iván Cárdenas')),
    ('2026-2', 'MED-520', 'Salud Pública',               (SELECT id FROM core.professor WHERE full_name = 'Dra. Natalia Ríos')),
    ('2026-2', 'DIS-301', 'Diseño de Interacción',       (SELECT id FROM core.professor WHERE full_name = 'Dr. Julián Ospina')),
    ('2026-2', 'DIS-310', 'Tipografía',                  (SELECT id FROM core.professor WHERE full_name = 'Dra. Valeria Muñoz')),
    ('2026-2', 'DIS-320', 'Diseño de Producto',          (SELECT id FROM core.professor WHERE full_name = 'Dr. Santiago Beltrán')),
    ('2026-2', 'DER-301', 'Derecho Penal',               (SELECT id FROM core.professor WHERE full_name = 'Dra. Diana Herrera')),
    ('2026-2', 'DER-310', 'Derecho Laboral',             (SELECT id FROM core.professor WHERE full_name = 'Dr. Sebastián Quintero')),
    ('2026-2', 'CIV-501', 'Estructuras Avanzadas',       (SELECT id FROM core.professor WHERE full_name = 'Dr. Mauricio Lara')),
    ('2026-2', 'CIV-510', 'Geotecnia',                   (SELECT id FROM core.professor WHERE full_name = 'Dr. Mauricio Lara')),
    ('2026-2', 'ECO-501', 'Econometría',                 (SELECT id FROM core.professor WHERE full_name = 'Dr. Rodrigo Aponte')),
    ('2026-2', 'ECO-510', 'Finanzas Públicas',           (SELECT id FROM core.professor WHERE full_name = 'Dr. Rodrigo Aponte'));
