#!/bin/bash
# Run HMNEmulator CLI Simulator on macOS
# Equivalent of RunSimulator.bat (Windows)

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

java -Xms256m -Xmx512m \
  -classpath "bin:lib/jFuzzyLogic_2.0.7.jar:lib/jfreechart-1.0.12.jar:lib/jcommon-1.0.15.jar:lib/iText-2.1.3.jar" \
  dpnm.tool.HMNEmulator -c
