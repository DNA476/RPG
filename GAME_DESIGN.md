# Game Design

## Design Intent

The game should turn correct physical repetitions into immediate, legible combat
actions. The player should understand three things at all times:

- whether the body is being tracked;
- whether the current movement is valid;
- what the movement did in the game.

The fitness action is the primary input device. Touch input should configure,
start, pause, or navigate the session, not replace the exercise.

## Current Core Loop

```text
Choose exercise
-> choose one of three random enemies
-> start battle
-> stand in camera view
-> enter squat
-> return to standing
-> repetition is validated
-> basic attack deals damage
-> repeat until boss HP reaches zero
-> victory
```

Current rules:

- On first launch, weight, height, and sex are requested but all fields may be
  skipped.
- The menu offers seven exercises.
- Squat is ready for live pose detection; the other six are experimental.
- Valid cycle: standing -> squat bottom -> standing.
- One valid repetition produces one attack.
- Damage equals the selected exercise's configured base damage: squat/crunch 1,
  lunge/push-up/jumping jack/plank 2, pull-up 3.
- Six normal enemies currently exist with 10-20 HP.
- Each enemy has one weakness (`1.5x`) and one resistance (`0.75x`).
- The offered trio always contains at least one neutral or vulnerable matchup.
- There is no reroll action for the offered enemies.
- Every 15 seconds the enemy uses an ability that reduces player attack by 25%
  for 10 seconds. This replaces player HP for the current prototype.
- Damage cannot fall below 1, so resistance never makes a battle impossible.
- Every successful attack triggers damage text, a sword slash, red flash, and
  short enemy shake.
- There is no player damage, timer, stamina, combo, failure, reward, or
  progression loop yet.
- Every accepted live repetition is added immediately to local daily
  statistics, even if the player leaves before victory.
- Debug-simulated repetitions do not affect fitness history.
- Statistics can be viewed for 7, 30, or 90 days and filtered by exercise.
- Calories are explicitly approximate. Weight personalizes the estimate when
  available; otherwise the UI states that a 70 kg default is used.

Debug builds can simulate a repetition, allowing every configured exercise and
damage value to complete the battle before its live detector is ready.

## Design Principles

### Accuracy Before Spectacle

The player must trust repetition counting. Visual effects and progression cannot
compensate for false positives, double counts, or unexplained misses.

### Immediate Feedback

Each valid repetition should trigger synchronized feedback:

- repetition count changes;
- damage number appears;
- enemy HP changes;
- attack animation/sound can be added later;
- form or tracking feedback remains readable.

### Exercise Integrity

Combat balance must not encourage unsafe speed, shallow range of motion, or
poor form. Difficulty should come from session structure and game decisions,
not from requiring risky movement.

### Short Sessions, Clear Wins

The initial target experience is a short encounter with a visible endpoint.
Long-term progression can connect multiple encounters after one battle is
reliable and satisfying.

### Tactical Choice Without Traps

The enemy screen should make the relevant matchup understandable before battle.
Cards show only two tactical facts for the selected exercise:

- whether the enemy is weak, resistant, or neutral;
- the enemy's timed weakening ability.

Full immunity is intentionally excluded. A player should be able to finish any
offered battle with the exercise they already selected.

### Explain Failure

When a repetition is not counted, feedback should distinguish at least:

- person not visible;
- required joints not visible;
- movement not deep enough;
- movement not completed by returning to stance;
- tracking or model error.

## Future Combat Model

The intended expansion path is:

- different exercises map to distinct attack types;
- exercise quality may modify damage within safe, bounded limits;
- combos reward consistent valid form, not raw speed;
- enemies introduce session variety through weaknesses, phases, or exercise
  mixes;
- player stats and equipment modify game expression without invalidating the
  physical workload target;
- rewards unlock content, cosmetics, or build choices.

Avoid mechanics that let progression trivialize exercise requirements. RPG power
may improve feedback, variety, or tactical options, but a completed exercise
must remain the source of an attack.

## Exercise Detection Contract

A detector should:

- be explicit about start/rest and active phases;
- count only complete cycles;
- tolerate ordinary landmark noise;
- avoid duplicate events while the player remains in one phase;
- reset predictably;
- expose thresholds through configuration;
- eventually provide structured rejection/form feedback.

Any quality score must be interpretable and calibrated. Do not derive health
advice or injury claims from pose landmarks.

## Difficulty And Balance Variables

Potential session-level variables:

- enemy HP;
- enemy weakness and resistance;
- enemy attack interval and debuff duration;
- damage per valid repetition;
- selected exercise set;
- target repetitions;
- rest intervals;
- encounter duration;
- detector thresholds calibrated to the player;
- enemy phases that change exercise prompts.

Balance should be expressed in configuration/content rather than hard-coded UI
branches.

## UX States

Required high-level states:

- main menu: exercise selection;
- idle: selected session has not started;
- tracking: camera/model is acquiring the player;
- battle: valid exercise input can damage the enemy;
- paused: future state for interruption and safety;
- victory: encounter goal completed;
- defeat/failure: reserved until a fair failure condition exists;
- recoverable error: permission, camera, model, or input-source problem.

The current `GameState` contains all except paused and explicit error.

## Safety And Privacy

- Pose inference currently stays on device.
- The optional profile and fitness history stay on device.
- Do not upload camera frames or body landmarks by default.
- Provide a visible pause/stop path before introducing longer sessions.
- Avoid medical promises or diagnosis.
- Support rest, warm-up, accessibility, and alternative exercise paths as the
  product matures.
- Player-facing language should encourage control and form rather than maximum
  speed.

## Open Design Decisions

- Is the primary mode fixed-repetition, fixed-time, or enemy-HP based?
- How should exercise difficulty and player fitness affect enemy attack timing?
- How should calibration change thresholds without rewarding unsafe depth?
- Will one session use one exercise or prompted exercise sequences?
- Which progression rewards are functional versus cosmetic?
- How are pause, rest, and interrupted repetitions handled?
- What form feedback is useful without becoming medical guidance?
