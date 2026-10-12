# 项目评测

宿主机需要 Python 3.8 或更高版本和 Docker。Java 编译、程序运行和判分均在容器内完成。在仓库根目录测试 A1 的公开数据：

```bash
python3 grader/grade_submission.py --submission handout --cases handout/tests --profile a1 --output results/a1
```

A1～A4 和 B1～B3 的正式评测仅包含公开测试。A5 保留独立隐藏测试。

评测脚本将 `handout/`、课程提供的 `grader/` 和根目录 `Dockerfile` 复制到临时目录，排除已有构建产物、缓存和测试答案。`--dockerfile` 可指定其他 Dockerfile。该文件的构建上下文包含 `handout/` 和 `grader/`，脚本在 `/workspace/handout/` 中运行。

镜像构建完成后，执行一次 `./run_build.sh`，随后逐个调用 `./run_test.sh <input.java> <output>`。每个样例在独立容器中运行，只复制该样例的输入程序；独立的判分容器接收标准答案，并使用课程提供的编译和回放工具计算分数。

镜像构建和项目构建各限时 900 秒，单例限时 120 秒。报告写入输出目录的 `report.json`，同时保留镜像构建日志、项目构建日志、单例输出和判分日志。
