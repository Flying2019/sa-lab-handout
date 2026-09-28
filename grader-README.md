# 项目评测

grader/ 用于展示评测系统，最终评测脚本和 grader/ 下的评测脚本一致。本地测试时请将你的项目和 grader/ 文件夹放于同一目录下，并确保你的项目中包含 `run_build.sh` 和 `run_test.sh` 接口。

例如，当你的文件夹名称为 handout 时，可以在主目录（grader/ 的上级目录）下运行以下指令测试 a1 的公开数据，并将结果存储在 results/a1 文件夹内。

```bash
python3 grader/grade_submission.py --submission handout --cases handout/tests --profile a1 --output results/a1
```

省略 `--profile` 参数将会默认使用 `--profile=all` 测试所有数据。省略 `--cases` 将会默认测试 `grader/hidden/` 下的隐藏测试。

镜像构建和项目构建各限时 900 秒，单例限时 120 秒。结果将写入指定目录的 `report.json` 并保留构建和测试日志。
