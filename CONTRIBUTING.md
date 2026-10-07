# Contributing & Branch Policy

## Branching model (simplified Git Flow)

| Branch | Purpose | Merges into | Protected |
|--------|---------|-------------|-----------|
| `main` | Production-ready, tagged releases only | — | Yes (PR + 1 review + green Jenkins build) |
| `develop` | Integration branch for the next release | `main` (via release) | Yes (PR + green build) |
| `feature/VSS-<id>-<short-desc>` | New user story | `develop` | No |
| `bugfix/VSS-<id>-<short-desc>` | Non-urgent defect fix | `develop` | No |
| `release/v<major>.<minor>.<patch>` | Release stabilisation | `main` and `develop` | No |
| `hotfix/v<version>-<short-desc>` | Urgent production fix | `main` and `develop` | No |
| `chore/<short-desc>` / `ci/<short-desc>` | Tooling, CI/CD, docs | `develop` | No |

**Naming rules**
- lowercase, words separated by hyphens: `feature/VSS-3-slot-booking`
- always include the backlog/issue ID where one exists
- delete the branch after it is merged

## Commit messages (Conventional Commits)
```
<type>(<scope>): <imperative summary, max 72 chars>

<optional body explaining what & why>
Refs: #<issue>
```
Types: `feat`, `fix`, `test`, `docs`, `ci`, `build`, `refactor`, `chore`.

Examples:
- `feat(booking): allow volunteers to request a slot`
- `fix(booking): count pending bookings against slot capacity`
- `ci(jenkins): add Selenium quality gate stage`

## Pull requests
1. Push the branch and open a PR against `develop` using the PR template.
2. At least **one reviewer** approves; Jenkins build must be green.
3. Use **"Create a merge commit"** (`--no-ff`) so feature history is visible.
4. Tag releases on `main` with semantic versions: `v1.0.0`, `v1.1.0`, ...

## Recommended GitHub branch protection (Settings → Branches)
- `main`, `develop`: Require a pull request before merging, require 1 approval,
  require status checks (Jenkins) to pass, disallow force pushes.
