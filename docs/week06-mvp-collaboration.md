# Week 6 – MVP Completion and Git Collaboration

## Branch
`feature/VSS-2-coordinator-api` → pull request #2 → merged into `develop`.

## Commits
```
feat(api): add REST API, exception handlers and service/web-flow tests
docs: note coordinator dashboard workflow            (feature branch side)
docs: note coordinator workflow on develop           (develop side – conflicting change)
Merge pull request #2 ... (resolved README conflict)
```

## MVP functions delivered (all acceptance criteria in `docs/week02-agile-planning.md`)
1. Volunteer registration with validation + duplicate-e-mail check.
2. Availability/slot view with seats left; full slots show "Full".
3. Booking request → `PENDING` + reference `VSS-XXXXXX`; duplicates and over-booking rejected.
4. Confirmation (`PENDING → CONFIRMED`) from the coordinator dashboard.
5. Cancellation (`→ CANCELLED`, seat released, double-cancel rejected).
6. Status tracking by reference or e-mail; REST API + `/actuator/health`.

## Conflict creation & resolution (deliberate demonstration)
Both sides appended a different last line to `README.md`:
```bash
git merge --no-ff feature/VSS-2-coordinator-api
# Auto-merging README.md
# CONFLICT (content): Merge conflict in README.md
```
Resolution: kept one combined line
`Coordinator: confirm or cancel requests from the dashboard.`,
normalised the file to UTF-8, `git add README.md`, committed the merge.

## Tagging & release baseline
```bash
git tag -a v1.0.0 -m "Release v1.0.0: MVP (...)"
git log --oneline --graph --all
# * 22f159d Merge pull request #2 ...
# |\
# | * docs (feature side) + feat(api)
# * | docs (develop side)
# |/
# * 58f467f Merge pull request #1 ...
```

## Backlog update
VSS-1 … VSS-8, VSS-12 → Done. Remaining: VSS-9/10/11 (pipeline work) in progress,
VSS-13/14 parked as future enhancements.
