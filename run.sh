#!/usr/bin/env sh
set -eu

PROJECT_ROOT=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
BIN_DIR="$PROJECT_ROOT/bin"

"$PROJECT_ROOT/build.sh"

echo "Starting game..."
java -cp "$BIN_DIR" App
