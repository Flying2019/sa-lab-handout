PYTHON ?= python
JAVA ?= java
JAVAC ?= javac
PROFILE ?= all
CASES ?= tests
JAR ?= Tai-e/build/tai-e-all-0.5.5-SNAPSHOT.jar
TIMEOUT ?= 30
COMMAND ?=
INPUT ?=
OUTPUT ?=

ifeq ($(OS),Windows_NT)
GRADLE ?= gradlew.bat
else
GRADLE ?= ./gradlew
endif

.PHONY: all build check test smt-check a1 a2 a3 a4 a5 b1 b2 b3 help
all: build

build:
	cd Tai-e && $(GRADLE) fatJar

smt-check:
	cd Tai-e && $(GRADLE) smtCheck

check:
	$(PYTHON) script/test.py validate --compile --profile $(PROFILE) --cases "$(CASES)" --java "$(JAVA)" --javac "$(JAVAC)"

test:
ifneq ($(strip $(INPUT)),)
ifneq ($(strip $(COMMAND)),)
	$(PYTHON) script/test.py analyze --input "$(INPUT)" --output "$(OUTPUT)" --profile $(PROFILE) --java "$(JAVA)" --javac "$(JAVAC)" --timeout $(TIMEOUT) --command $(COMMAND)
else
	$(PYTHON) script/test.py analyze --input "$(INPUT)" --output "$(OUTPUT)" --profile $(PROFILE) --java "$(JAVA)" --javac "$(JAVAC)" --timeout $(TIMEOUT) --jar "$(JAR)"
endif
else
ifneq ($(strip $(COMMAND)),)
	$(PYTHON) script/test.py judge --profile $(PROFILE) --cases "$(CASES)" --java "$(JAVA)" --javac "$(JAVAC)" --timeout $(TIMEOUT) --command $(COMMAND)
else
	$(PYTHON) script/test.py judge --profile $(PROFILE) --cases "$(CASES)" --jar "$(JAR)" --java "$(JAVA)" --javac "$(JAVAC)" --timeout $(TIMEOUT)
endif
endif

a1 a2 a3 a4 a5 b1 b2 b3:
	$(MAKE) test PROFILE=$@

help:
	@echo make build
	@echo make smt-check
	@echo make check
	@echo make test PROFILE=a1
	@echo make b1
