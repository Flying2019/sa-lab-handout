FROM eclipse-temurin:17-jdk-noble

# Python is used only by test/build scripts; Gradle provides JavaSMT and Z3.
RUN apt-get update \
    && apt-get install -y --no-install-recommends python3 python-is-python3 make libgomp1 libstdc++6 \
    && rm -rf /var/lib/apt/lists/*

ENV GRADLE_USER_HOME=/opt/gradle-cache \
    PYTHONDONTWRITEBYTECODE=1
WORKDIR /workspace
COPY . /workspace
RUN chmod +x run_build.sh run_test.sh
CMD ["bash"]
