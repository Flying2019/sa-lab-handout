#!/bin/sh
set -eu
if [ "$#" -ne 2 ]; then
    echo 'usage: ./run_test.sh <input.java> <output-file>' >&2
    exit 2
fi
case "$1" in *.java) ;; *) echo 'ERROR' >&2; exit 2 ;; esac
test -f "$1" || { echo 'ERROR' >&2; exit 2; }
input_dir=$(cd "$(dirname "$1")" && pwd)
input="$input_dir/$(basename "$1")"
output_dir=$(cd "$(dirname "$2")" && pwd)
output="$output_dir/$(basename "$2")"
cd "$(CDPATH= cd -- "$(dirname "$0")" && pwd)"
exec make test INPUT="$input" OUTPUT="$output"
