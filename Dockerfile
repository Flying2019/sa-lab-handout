FROM eclipse-temurin:17-jdk-noble

# Python is used only by the build and grading scripts.
RUN apt-get update \
    && apt-get install -y --no-install-recommends python3 python-is-python3 make libgomp1 libstdc++6 \
    && rm -rf /var/lib/apt/lists/*

ENV GRADLE_USER_HOME=/opt/gradle-cache \
    PYTHONDONTWRITEBYTECODE=1
COPY handout/ /workspace/handout/
COPY grader/ /workspace/grader/
WORKDIR /workspace/handout
RUN chmod +x run_build.sh run_test.sh \
    && mkdir -p /input /output /cases /evaluation
CMD ["bash"]
