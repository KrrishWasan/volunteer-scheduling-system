# Week 4 – Git and GitHub Repository Initialization

Repository: **https://github.com/KrrishWasan/volunteer-scheduling-system**

## What was created
| Item | Location |
|------|----------|
| README with stack, run instructions, structure | `README.md` |
| Java/Maven/IDE/log ignores | `.gitignore` |
| Folder structure (`src/main/java/com/vss`, `src/test`, `docs`, `ansible`, `.github`) | repo root |
| User-story, bug-report and DevOps-task issue templates | `.github/ISSUE_TEMPLATE/` |
| Pull-request template | `.github/PULL_REQUEST_TEMPLATE.md` |
| Branch policy (Git Flow: `main` / `develop` / `feature/*` / `bugfix/*` / `release/*` / `hotfix/*`) + Conventional Commits | `CONTRIBUTING.md` |
| Initial skeleton: Maven `pom.xml` (WAR), Spring Boot entry point, `ServletInitializer`, `application.properties`, home page, `/api/version` | `src/...` |

## Initial commits (meaningful messages)
```
docs: add problem definition, agile plan and architecture (weeks 1-3)
chore: add README, .gitignore, branch policy and GitHub issue/PR templates
build: initialise Maven Spring Boot project skeleton (WAR packaging)
feat(core): add home page, version endpoint, layout and health check
```

## Branching evidence
```bash
git branch -a
# * develop
#   feature/VSS-1-registration-booking
#   feature/VSS-2-coordinator-api
#   main
git log --oneline --graph --all   # see Week 6 doc for full graph
```

## Recommended GitHub settings (to apply on the repo page)
- Settings → Branches → protect `main` and `develop`: require pull request,
  1 approval, status checks (Jenkins) green, no force pushes.
- Issues → created from templates for stories VSS-1 … VSS-12.
- Projects → Kanban board with columns Backlog / To Do / In Progress / Review / Done.
