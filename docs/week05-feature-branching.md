# Week 5 – Feature Development with Branching

## Branch
`feature/VSS-1-registration-booking` → pull request #1 → merged into `develop` (merge commit, `--no-ff`).

## Commits on the branch
```
feat(registration): add volunteer, slot and booking domain with capacity rules
feat(booking): add registration, slot view, booking, status and coordinator pages
```

## What the feature contains (first core workflow)
- Domain: `Volunteer`, `Slot`, `Booking`, `BookingStatus` (JPA entities).
- Repositories: `VolunteerRepository`, `SlotRepository`, `BookingRepository`.
- Services: `VolunteerService` (duplicate-e-mail check), `SlotService` (`seatsLeft`),
  `BookingService` (duplicate + capacity rules inside a transaction).
- Web: `/register`, `/slots`, `/slots/{id}/book`, `/status`, `/coordinator`,
  Thymeleaf templates with stable element IDs for Selenium.

## Git evidence
```bash
git checkout develop
git merge --no-ff feature/VSS-1-registration-booking \
  -m "Merge pull request #1: feature/VSS-1-registration-booking (registration + slot + booking workflow)"
git log --oneline
# 58f467f Merge pull request #1 ...
# 27eac95 feat(booking): ...
# 2df67f6 feat(registration): ...
```

## Review checklist used (see PR template)
Code follows conventions, `mvn clean verify` green (14 tests), manual check at
http://localhost:8080 (register → slots → book → status), reviewer assigned.
