#!/bin/sh
set -eu
test "$#" -eq 0 || { echo 'usage: ./run_build.sh' >&2; exit 2; }
cd "$(CDPATH= cd -- "$(dirname "$0")" && pwd)"
exec make build GRADLE='sh ./gradlew --no-daemon'
