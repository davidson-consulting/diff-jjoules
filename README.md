# Diff-JJoules

**Diff-JJoules** is a project that allows to measure the energy consumption delta
between two versions of the same program.

This project can be apply on Maven project in Java and with JUnit (4 or 5).

It relies on the two following projects:

1. https://github.com/davidson-consulting/junit-jjoules to measure the energy consumption
2. https://github.com/STAMP-project/dspot/tree/master/dspot-diff-test-selection to select
tests methods that execute the lines that changed.

## Install

First, clone `junit-jjoules` and install it:

```sh
git clone https://github.com/davidson-consulting/junit-jjoules
cd junit-jjoules
mvn clean install
```

## Usage

We advise you to use this project as maven project. You can run everything from the command-line.
We advise you also to have a versions management, because this project modifies your source code.

1. Ensure that you have both versions of your project. In the remainder of this step-by-step, we will
refer to the version of the program before change. as `v1` and `v2` is the version of the program after
the changes. Both of them should be compiled, _e.g._ run `mvn clean install -DskipTests`

2. Go to the root of `v1` and run:

```sh
mvn eu.stamp-project:dspot-diff-test-selection:3.1.1-SNAPSHOT:list -Dpath-dir-second-version=/tmp/v2 \
dependency:build-classpath -Dmdep.outputFile=classpath \
fr.davidson:diff-jjoules:instrument -Dtests-list=/tmp/v1/testsThatExecuteTheChange.csv
```

With:
* `eu.stamp-project:dspot-diff-test-selection:3.1.1-SNAPSHOT:list -Dpath-dir-second-version=/tmp/v2`
the maven goal to select the tests that execute the change (based on a UNIX diff between `v1` and `v2`).
This selection will be in the file `testsThatExecuteTheChange.csv` at the root of `v1`.
* `dependency:build-classpath -Dmdep.outputFile=classpath·` retrieve the complete classpath of the
project, required for the instrumentation. This goal generates a file named `classpath` that will be
used by the next maven goal.
* `fr.davidson:diff-jjoules:instrument -Dtests-list=/tmp/v1/testsThatExecuteTheChange.csv` that will
instrument the selected tests in  **BOTH** versions of the program `v1` and `v2`.

Then, you can run the tests. You can collect the data in `target/jjoules-reports/`.
We advise you to keep the data and run several time the tests to increase the confidence
in the measures.

## Continuous integration

**TO BE DONE**

Here, we want to propose a bunch of scripts to enable the easy usage of this approach
in the CI.

## TODO

13 / 833
Run for javapoet 304176e 14 a5f4f0f 13 output_path data/output/december_2020/

#### Remove callgraph from `junit-jjoules`
#### Deploy `junit-jjoules` on maven central
#### Support multi-module projects
#### Potential projects:
* AuthZForce PDP Core
* Amazon Web Services SDK
* Apache Commons CLI
* Apache Commons Codec
* Apache Commons Collections
* Apache Commons IO
* Apache Commons Lang
* Apache Flink
* Google Gson
* Jaxen XPath Engine
* JFreeChart
* Java Git
* Joda-Time
* JOpt Simple
* jsoup
* SAT4J Core
* Apache PdfBox
* SCIFIO
* Spoon
* Urban Airship Client Library
* XWiki Rendering Engine
* jpush-api-java-client
* yahoofinance-api
* gson-fire
* j2html
* spring-petclinic
* javapoet
* eaxy
* java-html-sanitizer
* cron-utils
* TridentSDK
* jcodemodel

#### In case of a new feature

We can just instrument V2 and measure the energy consumption of thise new feature.
The process would be the following:

1. Detect if it is a new feature or not
2. if there are new tests that execute the new feature, we can go to step 3.
2. Instrument V2
3. Run X times the tests of the new feature

Use case : https://github.com/apache/commons-io/commit/397f69d2438f95f7946d83f1b7f240f93febbb3a
