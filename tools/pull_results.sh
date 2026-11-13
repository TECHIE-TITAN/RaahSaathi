#!/usr/bin/env bash
# Copy the OCR spike results out of the app's private storage (debug builds only).
# Run from the repo root:  bash tools/pull_results.sh [output-path]
set -euo pipefail

PKG=dev.techietitan.raahsaathi
OUT="${1:-data/bstd/ocr_results.json}"

mkdir -p "$(dirname "$OUT")"
adb exec-out run-as "$PKG" cat files/ocr_results.json > "$OUT"

echo "Saved $OUT ($(wc -c < "$OUT") bytes)"
