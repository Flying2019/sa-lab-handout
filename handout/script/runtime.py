"""Bounded local execution. This is a convenience runner, not a security sandbox."""
import os
from pathlib import Path
import signal
import subprocess
import time


def stop(process):
    if os.name == "nt":
        if process.poll() is None:
            subprocess.run(["taskkill", "/PID", str(process.pid), "/T", "/F"],
                           stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
            if process.poll() is None:
                process.kill()
    else:
        try:
            os.killpg(process.pid, signal.SIGKILL)
        except ProcessLookupError:
            pass
    process.wait()


def run(command, cwd, log, timeout, output=None, limit=2_000_000):
    log = Path(log)
    start = time.monotonic()
    status = "ok"
    try:
        with log.open("wb") as stream:
            p = subprocess.Popen(command, cwd=cwd, stdin=subprocess.DEVNULL,
                                 stdout=stream, stderr=subprocess.STDOUT,
                                 start_new_session=os.name != "nt")
            try:
                while p.poll() is None:
                    if time.monotonic() - start > timeout:
                        status = "timeout"
                        break
                    paths = [log] + ([Path(output)] if output else [])
                    if any(q.exists() and q.stat().st_size > limit for q in paths):
                        status = "output_limit"
                        break
                    time.sleep(0.03)
            finally:
                stop(p)
            if status == "ok" and p.returncode != 0:
                status = "crash"
        paths = [log] + ([Path(output)] if output else [])
        if any(q.exists() and q.stat().st_size > limit for q in paths):
            status = "output_limit"
        return {"status": status, "returncode": p.returncode,
                "seconds": round(time.monotonic() - start, 3)}
    except OSError as error:
        return {"status": "launch_error", "error": str(error)}
