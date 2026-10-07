# Week 1 – Problem Definition and Scope

## 1. Background and real-time need
NGOs, college NSS/NCC units, hospitals, food banks and event organisers depend on volunteers.
Today most of them coordinate shifts through WhatsApp groups, phone calls, paper sign-up sheets
or shared spreadsheets. As the number of volunteers and events grows, this manual process fails:

- Coordinators do not know in real time how many people are coming to a shift.
- Popular shifts get **over-booked** while others stay **under-staffed**.
- Volunteers do not get a clear confirmation and often turn up for cancelled shifts.
- Cancellations are communicated late (or not at all), so gaps cannot be refilled.
- There is no single record of who served where and when (needed for certificates / hours).

A small, always-available web system that shows open slots, accepts booking requests, lets a
coordinator confirm them, and lets everyone see the live status solves these problems.

## 2. Problem statement
> Volunteer-driven organisations lack a simple, centralised and real-time way to publish shifts,
> collect volunteer sign-ups, confirm or cancel bookings and track their status. Manual
> coordination through chats and spreadsheets leads to over-booking, no-shows, late
> cancellations and wasted coordinator time. The **Volunteer Scheduling System (VSS)** will provide
> a web application, delivered through an automated DevOps pipeline, in which volunteers register,
> view available slots, request a booking, receive confirmation, cancel if needed and track the
> status of every booking.

## 3. Target users
| User | Description | Main goals |
|------|-------------|-----------|
| Volunteer | Student / citizen offering time | Register once, find a suitable slot, book, get confirmation, cancel easily |
| Coordinator | NGO / event staff managing shifts | See all requests, confirm/cancel, avoid over-booking |
| Administrator / DevOps engineer | Runs the system | Reliable deployments, health monitoring, quick rollback |

## 4. Existing pain points
| # | Pain point | Impact |
|---|-----------|--------|
| P1 | Sign-ups scattered across chats/sheets | Data loss, duplicate entries |
| P2 | No capacity control | Over-booking or under-staffing |
| P3 | No formal confirmation | Uncertainty, no-shows |
| P4 | Cancellations not tracked | Gaps discovered too late |
| P5 | No status visibility for volunteers | Repeated calls/messages to coordinators |
| P6 | Manual, error-prone software releases (for the IT team) | Downtime, "works on my machine" issues |

## 5. Stakeholders
| Stakeholder | Role | Interest / Influence |
|-------------|------|----------------------|
| Volunteers | Primary end users | High interest, medium influence |
| Volunteer coordinators | Primary end users / business owners | High interest, high influence |
| NGO / organisation management | Sponsor | Medium interest, high influence |
| Development team (student) | Builds the application | High interest, high influence |
| DevOps / operations | Builds pipeline, deploys, monitors | High interest, high influence |
| QA / tester | Designs Selenium test suite | Medium interest, medium influence |
| Faculty guide / evaluator | Reviews deliverables | Medium interest, high influence |

## 6. Objectives
1. Build a web MVP covering **registration, slot view, booking request, confirmation,
   cancellation and status tracking**.
2. Manage all source code in **Git/GitHub** using a branching strategy and pull requests.
3. Automate build and packaging with **Maven** and **Jenkins** (Pipeline as Code).
4. Enforce a **Selenium** UI quality gate so failed tests stop deployment.
5. Package and deploy the application as a **Docker** container with versioned images.
6. Provision and configure the target server with **Ansible**, idempotently, with rollback.

## 7. Constraints
| Type | Constraint |
|------|-----------|
| Time | 15 weeks, one-person/small team, part-time |
| Cost | Only free / open-source tools (GitHub, Jenkins, Docker, Ansible, H2) |
| Technology | Java 17 + Maven (course requirement), Tomcat/Nginx target, Selenium WebDriver |
| Infrastructure | Runs on a laptop (Windows + WSL Ubuntu) – no paid cloud |
| Security | No real personal data; demo coordinator access without full auth in MVP |
| Scope | Single organisation, no SMS/e-mail notifications, no payments |

## 8. Measurable success criteria
| ID | Criterion | Target |
|----|-----------|--------|
| S1 | All 6 MVP functions work end-to-end | 100 % of MVP acceptance criteria pass |
| S2 | Over-booking prevented | 0 bookings beyond slot capacity (automated test) |
| S3 | Automated test coverage of critical journeys | ≥ 5 Selenium journeys + unit/integration tests |
| S4 | Build & deploy automation | Commit → running container with **no manual step** |
| S5 | Pipeline duration | < 10 minutes end-to-end |
| S6 | Quality gate | A failing test **blocks** deployment (demonstrated) |
| S7 | Idempotent provisioning | Second Ansible run reports `changed=0` for config tasks |
| S8 | Recovery | Rollback to previous image in < 2 minutes, health check `UP` |

## 9. Approved MVP scope (frozen)
**In scope**
1. Volunteer registration (name, e-mail, phone, skills) with validation and duplicate e-mail check.
2. Availability / slot view – list of upcoming shifts with remaining seats.
3. Booking request – volunteer requests a slot using their registered e-mail → status `PENDING`.
4. Confirmation – coordinator confirms a pending booking → `CONFIRMED`.
5. Cancellation – volunteer or coordinator cancels → `CANCELLED`, seat released.
6. Status tracking – look up a booking by reference number or all bookings by e-mail.
7. REST API + health endpoint for automation.

**Out of scope (future enhancements)**
Login/authentication & roles, e-mail/SMS notifications, recurring shifts, calendar sync,
attendance & certificates, multi-organisation tenancy, mobile app, analytics dashboard.

**Approval:** Scope reviewed with faculty guide – Week 1. Changes after this point go into the
backlog as future enhancements.
