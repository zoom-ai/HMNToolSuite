#!/bin/bash
# Run HMNEmulator GUI on macOS
# Equivalent of Run.bat (Windows)

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

java -Xms128m -Xmx256m \
  -classpath "lib/jfreechart-1.0.12.jar:lib/jFuzzyLogic_2.0.7.jar:lib/jcommon-1.0.15.jar:lib/iText-2.1.3.jar:bin" \
  dpnm.tool.HMNEmulator
