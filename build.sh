#!/usr/bin/env sh
set -eu

PROJECT_ROOT=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
SRC_DIR="$PROJECT_ROOT/src"
BIN_DIR="$PROJECT_ROOT/bin"

if [ ! -d "$SRC_DIR" ]; then
  echo "Source directory not found: $SRC_DIR" >&2
  exit 1
fi

mkdir -p "$BIN_DIR"

echo "Compiling Java sources from $SRC_DIR to $BIN_DIR..."
javac -d "$BIN_DIR" "$SRC_DIR"/*.java
echo "Build complete."
