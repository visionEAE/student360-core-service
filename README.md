# student360-core-service

Simulated **SIS + ERP** for Student 360° (port **8082**, schema **`core`**): source of truth for
student identity, official academic status and financial status. Not an adapter over the real
systems — a simulation exposing the contract they would expose through the institutional
integration platform (declared assumptions 2 and 3 in `student360-infra/docs/context.md`).

| Method | Path | Audit action |
|---|---|---|
| `GET` | `/api/core/students/{id}` | `READ_STUDENT_PROFILE` |
| `GET` | `/api/core/students/{id}/academic-status` | `READ_ACADEMIC_STATUS` |
| `GET` | `/api/core/students/{id}/financial-status` | `READ_FINANCIAL_STATUS` |
| `GET` | `/api/core/students/{id}/current-professors` | `READ_CURRENT_PROFESSORS` |

Who currently teaches a student is a deterministic fact (`core.course_offering`, seeded from the
courses in the current term's gradebook) — never rated, unlike the support-network graph in
`network-service`; it exists to answer "which professors is this student closest to" by the
simplest honest signal available: who is teaching them right now.

Every `/api/**` call must carry a service token whose audience is `core-service` (the gateway
mints it); the user identity arrives as `X-User-*` headers.

## Fine-grained authorization (second layer)

`StudentRecordAccessPolicy` from `student360-common`, called inside each `@Audited` method:

| Caller | Rule | `authorization_basis` |
|---|---|---|
| `STUDENT` | `ref` claim must equal `{id}` | `SELF` |
| `ADVISOR` | any student (assignments are enforced by support-service, which owns them) | `STAFF_ROLE` |
| `ADMIN` | any student | `ADMIN_ROLE` |
| otherwise | `403` **and** an audit record with `outcome = DENIED` | `NONE` |

**Authorization before existence**: a student asking about another student gets `403` whether or
not that student exists; `404` is reserved for callers allowed to know. This is deliberate and
applied consistently.

## Seed

`S-1001` Ana Torres — good standing, no balance · `S-1002` Luis Gómez — probation, balance on a
payment plan · `S-1003` María Rojas — **at risk**: `AT_RISK` standing, 62 days overdue, financial
hold. `S-1003` is also the disengaged student in lms-service; the convergence of signals is what
the support-service rule detects.

## Run · Verify

```bash
cd ../student360-infra && make up && make build-common && make run-core-service
mvn verify   # format, style, Testcontainers tests = phase gate 3
```
