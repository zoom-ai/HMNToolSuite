# HMNToolSuite: Heterogeneous Mobile Networks Simulator & Emulator

HMNToolSuite is a research-oriented simulation and emulation platform designed for studying **Vertical Handover (VHO)** in heterogeneous mobile network environments. It provides a comprehensive set of tools to model network infrastructures, mobile node behaviors, and advanced handover decision algorithms.

## Key Features

- **Multi-Network Support**: Simulate coexistence of diverse network technologies including:
  - **WLAN** (802.11)
  - **CDMA**
  - **WiBro** (Mobile WiMAX)
  - **HSDPA**
- **Advanced Handover Algorithms**: Implements various decision-making strategies:
  - **Autonomic Handover**: Uses APAV (Available Personal Access Value) and APSV (Available Personal Service Value) calculations.
  - **Context-Aware Handover**: Decisions based on speed, location, schedule, and user preferences.
  - **Fuzzy Logic Integration**: Built-in support for fuzzy logic based decision making using `jFuzzyLogic`.
- **Interactive Tools**:
  - **Network Editor**: A GUI for designing network layouts, placing BS/AP/RAS, and defining signal coverage.
  - **Mobile Node Emulator**: A real-time emulator to visualize node movement and monitor handover events.
  - **Network Simulator**: A simulation engine for batch processing and performance analysis.
- **Visualization & Logging**: Integrated `JFreeChart` support for real-time signal and performance graphing.

## Project Structure

- `src/`: Java source code.
- `lib/`: Required libraries (`jfreechart`, `jFuzzyLogic`).
- `data/`: Simulation configurations and network maps.
- `res/`: UI resources (icons, images).
- `paper/`: Scientific documentation and research materials.

## Requirements

- **Java JDK**: Version 8 or higher (Java 21 recommended).
- **Display**: GUI components require a graphical environment (Swing-based).

### Installing Java

**macOS** – Install via [SDKMAN](https://sdkman.io/):
```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 21.0.6-tem
```

**Windows** – Download and install the JDK from [Adoptium](https://adoptium.net/).

## Build from Source

A `build.sh` script is provided for macOS/Linux to recompile all source files:

```bash
./build.sh
```

This compiles all `.java` files in `src/` into the `bin/` directory.

## How to Run

### macOS / Linux

**Emulator GUI** (equivalent of `Run.bat`):
```bash
./run.sh
```

**CLI Simulator** (equivalent of `RunSimulator.bat`):
```bash
./run_simulator.sh
```

### Windows

**Emulator GUI**:
```bat
Run.bat
```

**CLI Simulator**:
```bat
RunSimulator.bat
```

## Credits

Developed and maintained by **Eliot J.M. Kang** (J.M. Kang) at **POSTECH DP&NM**.
Copyright (c) 2005 - 2010.

---
*For more technical details, please refer to the documentation in the `paper/` directory.*
