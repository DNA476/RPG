# Codex Development Rules

These rules apply to future work in this repository.

## Before Coding

1. Read `PROJECT_CONTEXT.md`, `ARCHITECTURE.md`, `GAME_DESIGN.md`, and
   `ROADMAP.md`.
2. Inspect the current module structure, relevant implementations, tests, and
   `git status` before choosing an approach.
3. Preserve existing user changes. Do not revert or overwrite unrelated work.
4. Prefer the project's current patterns and dependencies over introducing a
   new framework.

## Architecture

- Keep camera and MediaPipe code independent from exercise, combat, and UI
  rules.
- Keep exercise detectors independent from enemies and damage.
- Put reusable fitness contracts in `:domain`, combat rules in `:game`,
  content/repositories in `:data`, and Android/Compose orchestration in `:app`.
- Store balance and content in catalogs/configuration rather than UI branches.
- Add extension points only where a known future mechanic needs them.
- Experimental features must fail safely and be clearly marked for users and
  developers.

## Implementation

- Complete requested work end to end: implementation, tests, build, and
  relevant manual QA.
- Keep release behavior free of debug-only controls.
- Add focused tests for new rules, edge cases, and regression-prone behavior.
- When adding generated or third-party assets, keep stable filenames, optimize
  file size, and update notices when licensing requires it.
- Do not add network services, analytics, camera uploads, medical claims, or
  automatic exercise selection without an explicit product decision.

## Verification

- Run unit tests for affected modules.
- Compile both debug and release code for user-facing changes.
- Run Android lint when UI, resources, manifest, or Android integration changes.
- For interaction changes, validate the main flow on an emulator when
  available, including error-free logs.
- State clearly when hardware-dependent camera or pose behavior could not be
  fully verified.

## Documentation And Delivery

- Update `PROJECT_CONTEXT.md`, `ARCHITECTURE.md`, `GAME_DESIGN.md`, and
  `ROADMAP.md` whenever product behavior, architecture, balance, or priorities
  change.
- Keep documentation factual and aligned with the code.
- Review the final diff and exclude IDE-only or unrelated files.
- Finish completed tasks with a concise commit describing the delivered
  behavior.
- Push the branch and provide a short GitHub summary when publication is part
  of the request.
