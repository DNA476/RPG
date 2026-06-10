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
Start session
-> stand in camera view
-> enter squat
-> return to standing
-> repetition is validated
-> basic attack deals damage
-> repeat until boss HP reaches zero
-> victory
```

Current MVP rules:

- Exercise: squat.
- Valid cycle: standing -> squat bottom -> standing.
- One valid repetition produces one attack.
- Squat maps to `BasicAttack`.
- Damage uses `FlatDamageCalculator`.
- Training boss starts with 10 HP.
- There is no player damage, timer, stamina, combo, failure, reward, or
  progression loop yet.

The code already reserves `LUNGE`, `JUMP`, heavy attacks, critical attacks,
`DEFEAT`, player stats, equipment, and skills, but these are not complete player
features.

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

- idle: session has not started;
- tracking: camera/model is acquiring the player;
- battle: valid exercise input can damage the enemy;
- paused: future state for interruption and safety;
- victory: encounter goal completed;
- defeat/failure: reserved until a fair failure condition exists;
- recoverable error: permission, camera, model, or input-source problem.

The current `GameState` contains all except paused and explicit error.

## Safety And Privacy

- Pose inference currently stays on device.
- Do not upload camera frames or body landmarks by default.
- Provide a visible pause/stop path before introducing longer sessions.
- Avoid medical promises or diagnosis.
- Support rest, warm-up, accessibility, and alternative exercise paths as the
  product matures.
- Player-facing language should encourage control and form rather than maximum
  speed.

## Open Design Decisions

- Is the primary mode fixed-repetition, fixed-time, or enemy-HP based?
- Should enemies attack, and what fair action prevents or mitigates damage?
- How should calibration change thresholds without rewarding unsafe depth?
- Will one session use one exercise or prompted exercise sequences?
- Which progression rewards are functional versus cosmetic?
- How are pause, rest, and interrupted repetitions handled?
- What form feedback is useful without becoming medical guidance?
