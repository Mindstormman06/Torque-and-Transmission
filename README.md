# Create: Torque and Transmission

This mod targets NeoForge `1.21.1` and builds on Create to prototype manual drivetrain gameplay.

## Implemented now

- `Transmission` block with persistent gear state and reverse support.
- `Stick Shifter` block that sends client shift requests to the server.
- Server-authoritative gear shifting with synced transmission state.
- `Mechanic's Wrench` linking flow:
  - Right-click transmission to store target.
  - Right-click stick shifter to link it to that transmission.
- Config-driven ratios:
  - `gearRatios` (forward gears)
  - `reverseRatio`
  - `maxTargetRpm`
- Basic GameTest coverage and datagen providers for loot, recipes, models, and language keys.

## Planned next

**Transmission (The Variable Gearbox):**
- Prevent shifting under invalid load states (clutch logic / lockout rules).
- Expand ratio tuning and balancing against Create stress interactions.
- Deepen rotational integration with Create drivetrain internals.

**Stick Shifter (The Interface):**
- Add radial GUI and richer in-world indicators.
- Improve feedback for linkage and shift constraints.

**Accelerator (The Throttle Linkage):**
- Define target RPM behavior based on input (0-256) and active gear.
- Add RPM ramp/drop behavior under over-torque conditions.

**Manual Accelerator (The Pedal):**
- Seat-aware W/S input binding.
- Networked throttle percentage control.

**Torque (The Physics Interface):**
- Model torque vs RPM progression per gear.
- Build stall/restart behavior for overloaded drivetrains.

## Development

### Requirements

- Java 21
- Gradle wrapper (`gradlew` / `gradlew.bat`)

### Common commands

- Windows build: `gradlew.bat build`
- Run client: `gradlew.bat runClient`
- Run server: `gradlew.bat runServer`
- Run datagen: `gradlew.bat runData`
- Run gametests: `gradlew.bat runGameTestServer`

Generated resources are written to `src/generated/resources` and included by the main resource source set.
