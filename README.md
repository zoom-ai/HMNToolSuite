<div align="center">

# 🌐 HMNToolSuite

### Heterogeneous Mobile Networks Simulator & Emulator

*A research platform for studying Vertical Handover (VHO) in heterogeneous wireless network environments.*

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://adoptium.net/)
[![Platform](https://img.shields.io/badge/Platform-macOS%20%7C%20Windows-lightgrey?style=for-the-badge&logo=apple&logoColor=white)](https://github.com/zoom-ai/HMNToolSuite)
[![License](https://img.shields.io/badge/License-Research-blue?style=for-the-badge)](paper/)
[![POSTECH](https://img.shields.io/badge/POSTECH-DP%26NM-003580?style=for-the-badge)](http://dpnm.postech.ac.kr)

</div>

---

## 📖 Overview

**HMNToolSuite** is a research-oriented simulation and emulation suite developed at **POSTECH DP&NM** for studying **Vertical Handover (VHO)** — the process by which a mobile device seamlessly transitions between different wireless network technologies (e.g., WLAN → CDMA → WiBro) while maintaining an active session.

---

## ✨ Key Features

### 📡 Multi-Network Simulation

| Technology | Type | Standard |
|:---:|:---:|:---:|
| 🛜 **WLAN** | Wireless LAN | IEEE 802.11 |
| 📶 **CDMA** | Cellular | IS-95 |
| 🌀 **WiBro** | Mobile WiMAX | IEEE 802.16e |
| ⚡ **HSDPA** | 3.5G Cellular | 3GPP |

### 🧠 Handover Decision Algorithms

- **🤖 Autonomic Handover** — Selects the best network using **APAV** (Available Personal Access Value) and **APSV** (Available Personal Service Value) scores
- **🧩 Context-Aware Handover** — Decisions based on location, speed, schedule, and user profile
- **🌫️ Fuzzy Logic Engine** — Integrated `jFuzzyLogic` for intelligent soft-decision making
- **🎲 Random Handover** — Baseline comparison algorithm

### 🛠️ Included Tools

| Tool | Description |
|:---|:---|
| 🗺️ **Network Editor** | Interactive GUI to design network topology (BS / AP / RAS placement) |
| 📱 **Mobile Node Emulator** | Real-time emulator to visualize node movement & handover events |
| 🔬 **Network Simulator** | Batch simulation engine with scripted scenario support |
| 📊 **Monitor View** | Live signal strength & performance charts via `JFreeChart` |

---

## 🏗️ Project Structure

```
HMNToolSuite/
├── 📂 src/          # Java source code (dpnm.* packages)
├── 📂 bin/          # Compiled class files
├── 📂 lib/          # Third-party libraries
├── 📂 data/         # Network map XML scenarios
├── 📂 res/          # UI resources (icons, FCL fuzzy rules)
├── 📂 log/          # Simulation output logs
├── 📂 paper/        # Research papers & documentation
├── 🐚 build.sh      # macOS/Linux build script
├── 🐚 run.sh        # macOS/Linux emulator launcher
├── 🐚 run_simulator.sh  # macOS/Linux CLI simulator launcher
├── 🪟 Run.bat       # Windows emulator launcher
└── 🪟 RunSimulator.bat  # Windows CLI simulator launcher
```

---

## ⚙️ Tech Stack

[![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://adoptium.net/)
[![Swing](https://img.shields.io/badge/Swing-GUI-4A90D9?style=flat-square&logo=java&logoColor=white)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![JFreeChart](https://img.shields.io/badge/JFreeChart-1.0.12-5C8A35?style=flat-square)](https://www.jfree.org/jfreechart/)
[![jFuzzyLogic](https://img.shields.io/badge/jFuzzyLogic-2.0.7-8E44AD?style=flat-square)](http://jfuzzylogic.sourceforge.net/)

---

## 🚀 Getting Started

### 1️⃣ Install Java

<details>
<summary><b>🍎 macOS</b> — Install via SDKMAN</summary>

```bash
# Install SDKMAN
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Install Java 21 (Temurin)
sdk install java 21.0.6-tem
```
</details>

<details>
<summary><b>🪟 Windows</b> — Download from Adoptium</summary>

Download and install the JDK from [Adoptium](https://adoptium.net/).
</details>

---

### 2️⃣ Build from Source

```bash
./build.sh
```

> Compiles all 138 `.java` source files from `src/` into `bin/`.

---

### 3️⃣ Run

#### 🍎 macOS / Linux

```bash
# GUI Emulator
./run.sh

# CLI Simulator
./run_simulator.sh
```

#### 🪟 Windows

```bat
:: GUI Emulator
Run.bat

:: CLI Simulator
RunSimulator.bat
```

---

## 👤 Credits

Developed and maintained by **Eliot J.M. Kang** at [**POSTECH DP&NM Lab**](http://dpnm.postech.ac.kr).
Copyright © 2005 – 2010. All rights reserved.

> 📄 For technical details, refer to the research paper in the [`paper/`](paper/) directory.
