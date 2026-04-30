# Create: Torque and Transmission

**Transmission (The Variable Gearbox):**
* Does not change gears if receiving rotational power (clutch must be engaged).
* Stores a GearRatio value (e.g., 1st = 0.2, 5th = 1.0). Definable by user.
* **Output RPM** = Engine RPM * GearRatio.
* **Output Stress Capacity** = Engine SU / GearRatio. (This ensures 1st gear is "strong" and 5th is "weak").
* Includes a reverse option (multiplies output speed by -1).

**Stick Shifter (The Interface):**
* Controls the GearRatio index in the Transmission.
* Radial GUI for gear selection.
* Sends a packet to the server to lock in the selected gear.
* Has as many gears as the Transmission it is linked to

**Accelerator (The Throttle Linkage):**
* Defines the **Target RPM** based on input (0–256).
* Calculates the **RPM Ramp Speed** based on the current Gear:
* * If Required Torque > Available Torque (e.g., trying to start in 5th gear or braking while moving), the RPM begins to drop.

**Manual Accelerator (The Pedal):**
* Binds to W and S while in a linked seat.
* Sends "Throttle %" data to the Accelerator block.
* Allows for gradual throttle (holding W longer increases the % value).

**Torque (The Physics Interface):**
* Leverages Create's Stress Units to interact with *Aeronautics* mass.
* If the car is too heavy for the current gear's Torque multiplier, the system overstresses and the engine **Stalls** (RPM hits 0 and requires a restart).
* High Torque (Low Gear) = High SU, Low RPM. Needed to break static friction and start moving heavy vehicles.
* Low Torque (High Gear) = Low SU, High RPM. Used for top speed once momentum is established.

**Linking System:**
* Mechanic's Wrench stores block coordinates in NBT to link the Dashboard (Shifter/Pedal) to the Drivetrain (Transmission/Accelerator).
