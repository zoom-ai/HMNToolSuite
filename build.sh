#!/bin/bash
# Build script for HMNToolSuite on macOS
# Compiles all Java source files into the bin/ directory

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "=== HMNToolSuite Build ==="

# Create bin directory if it doesn't exist
mkdir -p bin

# Collect all Java source files
find src -name "*.java" > sources.txt

echo "Found $(wc -l < sources.txt) source files."
echo "Compiling..."

javac -encoding EUC-KR \
  -classpath "lib/jfreechart-1.0.12.jar:lib/jFuzzyLogic_2.0.7.jar:lib/jcommon-1.0.15.jar:lib/iText-2.1.3.jar" \
  -d bin \
  @sources.txt

rm sources.txt
echo "Build successful. Classes are in bin/"
