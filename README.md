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

- **Java Runtime Environment (JRE) / JDK**: Version 8 or higher.
- **Display**: GUI components require a graphical environment (Swing-based).

## How to Run

### 1. Emulator GUI
Launches the main interactive emulation interface.
```bash
./Run.bat
```

### 2. CLI Simulator
Runs the network simulator in command-line mode.
```bash
./RunSimulator.bat
```

## Credits

Developed and maintained by **Eliot J.M. Kang** (J.M. Kang) at **POSTECH DP&NM**.
Copyright (c) 2005 - 2010.

---
*For more technical details, please refer to the documentation in the `paper/` directory.*
