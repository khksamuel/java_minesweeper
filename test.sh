#!/usr/bin/env sh
set -eu

PROJECT_ROOT=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
SRC_DIR="$PROJECT_ROOT/src"
TEST_DIR="$PROJECT_ROOT/tests"
BIN_DIR="$PROJECT_ROOT/bin"
TEST_BIN_DIR="$PROJECT_ROOT/bin-test"
LIB_DIR="$PROJECT_ROOT/lib"
JUNIT_VERSION="1.10.2"
JUNIT_JAR="$LIB_DIR/junit-platform-console-standalone-$JUNIT_VERSION.jar"
JUNIT_URL="https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/$JUNIT_VERSION/junit-platform-console-standalone-$JUNIT_VERSION.jar"

if [ ! -d "$SRC_DIR" ]; then
  echo "Source directory not found: $SRC_DIR" >&2
  exit 1
fi

if [ ! -d "$TEST_DIR" ]; then
  echo "Test directory not found: $TEST_DIR" >&2
  exit 1
fi

mkdir -p "$BIN_DIR" "$TEST_BIN_DIR" "$LIB_DIR"

if [ ! -f "$JUNIT_JAR" ]; then
  echo "Downloading JUnit platform console standalone $JUNIT_VERSION..."
  curl -fL "$JUNIT_URL" -o "$JUNIT_JAR"
fi

echo "Compiling source files..."
javac -d "$BIN_DIR" "$SRC_DIR"/*.java

echo "Compiling test files..."
javac -cp "$JUNIT_JAR:$BIN_DIR" -d "$TEST_BIN_DIR" "$TEST_DIR"/*.java

echo "Running tests..."
java -jar "$JUNIT_JAR" --class-path "$BIN_DIR:$TEST_BIN_DIR" --scan-class-path
