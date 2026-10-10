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
mkdir -p "$(dirname "$2")"
output_dir=$(cd "$(dirname "$2")" && pwd)
output="$output_dir/$(basename "$2")"
root=$(CDPATH= cd -- "$(dirname "$0")" && pwd)
profile=${SA_PROFILE:-$(basename "$input_dir")}
name=$(basename "$input" .java)
package=$(sed -n 's/^[[:space:]]*package[[:space:]]\{1,\}\([A-Za-z0-9_.]*\)[[:space:]]*;.*/\1/p' "$input" | head -n 1)
package_dir=$(printf '%s' "$package" | tr . /)
entry=${package:+$package.}$name
mkdir -p "$root/.work"
work=$(mktemp -d "$root/.work/run.XXXXXX")
trap 'cd "$root"; case "$work" in "$root"/.work/run.*) rm -rf "$work";; esac' 0
mkdir -p "$work/$package_dir" "$work/benchmark/internal"
cp "$input" "$work/$package_dir/$name.java"
cp "$root/benchmark/internal/Benchmark.java" "$work/benchmark/internal/Benchmark.java"
case "$name" in
    *1Object) SA_CONTEXT=${SA_CONTEXT:-1-object};;
    *1Call) SA_CONTEXT=${SA_CONTEXT:-1-call};;
esac
export SA_CONTEXT
cd "$work"
case "$profile" in
    b1|b2|b3)
        set -- -a "pku-se=task:$profile" --input-classes "$entry";;
    *)
        set -- -a pku-course -m "$entry";;
esac
"${JAVA:-java}" "-Djava.library.path=$root/Tai-e/build/smt-native" \
    -jar "$root/Tai-e/build/tai-e-all-0.5.5-SNAPSHOT.jar" \
    -cp "$work" "$@" --output "$output"
