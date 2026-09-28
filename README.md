# 《软件分析技术》实验手册

本实验目的是实现一个 Java 程序分析工具。其中 Lab-1 主要聚焦于课程的前半部分数据流分析与指针分析。Lab-2 主要聚焦于课程的后半部分符号执行。具体任务内容和文档如下：

| 作业 | 任务 | 截止时间 |
| --- | --- | --- |
| [A1](docs/a1.md) | 过程内符号分析 | 10.25 |
| [A2](docs/a2.md) | 过程间符号分析 | 11.1 |
| [A3](docs/a3.md) | 上下文非敏感指针分析 | 11.8 |
| [A4](docs/a4.md) | 上下文敏感指针分析 | 11.15 |
| [A5](docs/a5.md) | 符号分析与指针分析的组合 | 11.22 |
| [B1](docs/b1.md) | 单过程符号执行 | 11.29 |
| [B2](docs/b2.md) | 函数调用与递归 | 12.6 |
| [B3](docs/b3.md) | 数组符号执行 | (Optional) 12.20 |

如果你计划使用 Tai-e 作为 Java 程序的静态程序分析框架，可以先阅读南京大学《软件分析》课程实验作业中关于 [Tai-e 框架配置](https://tai-e.pascal-lab.net/intro/setup.html) 的内容，然后阅读后续章节。如果你计划使用其他工具，可以直接跳转到 “构建与测试” 章节。

## 获取与更新

你可以选择直接 clone 课程仓库，并创建自己的实现分支。

```bash
git clone https://github.com/Flying2019/sa-lab-handout sa-labs
cd sa-labs
git switch -c solution
```

后续 Git 操作在 `sa-labs/` 仓库根目录执行。课程后续作业和修订会提交到课程仓库的 `main` 分支。在 `solution` 分支上更新：

```bash
git pull --no-rebase origin main
```

你也可以选择 fork 课程仓库进行后续开发，或者直接下载模板，并手动更新作业和修订。

## 实现位置

如果你采用 Tai-e 框架，需要在下发文件的 `handout/Tai-e/src/main/java/pku/` 目录中实现分析算法。该目录下已经创建了模板文件，你可以在模板基础上进行修改。

| 文件 | 用途 |
| --- | --- |
| CourseAnalysis.java | Lab1 的默认分析入口 |
| Preprocess.java | 针对分配标记与查询的预处理 |
| CourseResult.java | Lab1 打印结果 |
| TrivialSolver.java | CourseAnalysis 默认使用的分析过程 |
| SymbolicExecution.java | Lab2 分析入口 |
| SmtExample.java | Java-SMT 模板展示 |

## 构建与测试

本机运行需要 JDK 17+、Python 3.8+ 和 Make。在 `handout/` 目录运行 `make build` 或 `./run_build.sh` 将构建整个项目。运行 `make a1` 将测试 A1 实验的公开数据。

本项目使用 JavaSMT 调用 Z3，Z3 依赖由 Gradle 自动管理。在 `handout/` 下运行 `make smt-check` 可验证 JavaSMT 和 Z3 的加载与求解。Java 示例见 [SMT 指南](docs/smt.md)，其余命令见 [Makefile](handout/Makefile)。

Docker 测试在仓库根目录运行，本机只需 Python 和 Docker：

```bash
python3 grader/grade_submission.py --submission handout --cases handout/tests --profile a1 --output results/a1
```

脚本将项目和评测工具复制到临时目录，使用根目录的 `Dockerfile` 构建环境。Dockerfile 仅用于评测使用，提交前请确保代码经过 Docker 测试，以避免和助教机器环境不同导致的问题。

## 提交与运行接口

你需要提交包含根目录 `Dockerfile` 和 `handout/` 的压缩文件。`handout/` 中必须保留 `run_build.sh` 和 `run_test.sh`。评测工具 grader/ 不要放入压缩文件。**建议非必要不更改 Dockerfile 配置**。

在 `handout/` 目录运行以下命令，生成仓库根目录的 `submission.tar.gz`：

```bash
make package
```

也可运行 `python script/package.py`。自定义文件名使用 `make package PACKAGE=../学号.tar.gz`。

评测时会先运行一次 `./run_build.sh`。构建成功后针对每个测试用例分别运行 `./run_test.sh <input.java> <output>`。下发的模板项目中，默认构建脚本执行 `make build`，测试脚本执行 `make test INPUT="<input.java>" OUTPUT="<output>"`。

测试时作业编号通过环境变量 `SA_PROFILE` 提供（例如 `SA_PROFILE=a1` 表示当前调用 a1 测试）。如果有必要，你可以通过该环境变量判断运行方式。具体的本地测试命令见 [评测说明](grader-README.md)。

## 结果与计分

每个测试点满分 2 分，正常结束即可得 1 分。正常运行后，每个测试点实际得分为 1+精度分。精度分设置见下表。

| 查询 | 判定条件 | 精度分 |
| --- | --- | --- |
| 符号分析 | 输出的抽象值位于下界和上界之间 | 1 |
| 指针分析 | 输出集合包含标准集合 | `(标准集合大小 + 1) / (输出集合大小 + 1)` |
| 符号执行 | 可达目标给出合法输入，完整执行访问目标并正常返回；不可达目标输出 `unreachable` | 1 |
