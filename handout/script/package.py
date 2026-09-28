"""Package a submission without generated files or teacher materials."""
import argparse
import io
import os
from pathlib import Path
import tarfile
import tempfile

HANDOUT = Path(__file__).resolve().parents[1]
EXCLUDED = {'.git', '.work', '.gradle', '.kotlin', 'build', '__pycache__',
            '.venv', 'venv', '.pytest_cache', '.idea', '.vscode', 'results',
            '.env', 'compose.override.yaml', '.DS_Store', 'Thumbs.db'}


def files(folder, source=False):
    for path in sorted(folder.iterdir()):
        excluded = EXCLUDED - {'build', 'results'} if source else EXCLUDED
        if path.name in excluded or path.suffix in {'.pyc', '.pyo'}:
            continue
        if path.is_symlink():
            raise ValueError('Replace symbolic links with project files: '+str(path))
        if path.is_dir():
            yield from files(path, source or path.name=='src')
        elif path.is_file():
            yield path


def package(handout, output):
    handout, output = handout.resolve(), output.resolve()
    if handout == output or handout in output.parents:
        raise ValueError('Place the submission archive outside handout/')
    if not output.name.endswith('.tar.gz'):
        raise ValueError('Use a .tar.gz archive name')
    root = handout.parent
    for path in (root/'Dockerfile', handout/'run_build.sh', handout/'run_test.sh'):
        if not path.is_file(): raise ValueError('Missing required file: '+str(path))
    sources = [root/'Dockerfile']
    if (root/'.dockerignore').is_file(): sources.append(root/'.dockerignore')
    sources.extend(files(handout))
    if output in sources: raise ValueError('Output would overwrite a project file')
    output.parent.mkdir(parents=True, exist_ok=True)
    with tempfile.NamedTemporaryFile(dir=output.parent, prefix=output.name+'.',
                                     suffix='.tmp', delete=False) as stream:
        temporary = Path(stream.name)
    try:
        with tarfile.open(temporary, 'w:gz') as archive:
            for path in sources:
                data = path.read_bytes()
                shell = path.suffix=='.sh' or path.name=='gradlew'
                if shell: data = data.replace(b'\r\n', b'\n')
                info = archive.gettarinfo(str(path), path.relative_to(root).as_posix())
                info.size = len(data)
                info.mode = 0o755 if shell or info.mode & 0o111 else 0o644
                info.uid = info.gid = 0
                info.uname = info.gname = ''
                archive.addfile(info, io.BytesIO(data))
        os.replace(temporary, output)
    finally:
        temporary.unlink(missing_ok=True)
    print(f'PACKAGED: {output}; {len(sources)} files; {output.stat().st_size} bytes')


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--output', type=Path, default=HANDOUT.parent/'submission.tar.gz',
                        help='output tar.gz outside handout/')
    args = parser.parse_args()
    try: package(HANDOUT, args.output)
    except (OSError, ValueError) as error: parser.exit(1, f'ERROR: {error}\n')


if __name__ == '__main__': main()
