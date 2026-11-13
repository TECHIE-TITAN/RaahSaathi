#!/usr/bin/env bash
# Copy the BSTD sample into the app's private storage (debug builds only).
# Run from the repo root:  bash tools/push_sample.sh [path-to-sample]
set -euo pipefail

PKG=dev.techietitan.raahsaathi
SRC="${1:-data/bstd/sample}"
TMP=/data/local/tmp/bstd_sample

[ -f "$SRC/sample.json" ] || { echo "No sample.json in $SRC" >&2; exit 1; }

adb shell rm -rf "$TMP"
adb push "$SRC" "$TMP"
# One quoted string, so the device shell (not the host shell) splits the inner command.
adb shell "run-as $PKG sh -c 'mkdir -p files && rm -rf files/bstd_sample && cp -r $TMP files/bstd_sample'"
adb shell rm -rf "$TMP"

echo "Copied. Contents of the app's files/bstd_sample:"
adb shell run-as "$PKG" ls files/bstd_sample
