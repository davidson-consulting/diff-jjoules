# Diff-JJoules

`diff-jjoules` is a set of maven plugins that measures the impact of commits on the energy consumption of the program.

## Install

To use Diff-JJoules, you need to install [JJoules](https://github.com/davidson-consulting/j-joules), [JUnit-JJoules](https://github.com/davidson-consulting/junit-jjoules) and [Diff-Test-Selection](https://github.com/STAMP-project/dspot/tree/master/dspot-diff-test-selection).

Then, you can install `diff-jjoules`:

```sh
mvn clean install -DskipTest
```

## Usage

The easiest way to use `diff-jjoules`, we need to clone your project twice: one for the version before applying the commit, and one for the version after applying the commit. 
It the following, we consider **path/v1** as the path to the version of the program before applying the commit, and **path/v2** as the path to the version of the program after applying the commit.

You can run `diff-jjoules` with the following command line, from **path/v1**, where your `pom.xml` is:

```shell
mvn fr.davidson:diff-jjoules:diff-jjoules -Dpath-dir-second-version=path/v2
```

Calling this maven plugin will apply the whole process as follow:

1. `test-selection`: select the tests that execute the code changes (this is done by `diff-test-selection`;
2. `instrumention`: instrument the selected tests with probes to measure their energy consumption;
3. `delta`: computation of the energy consumption delta test-wise (&Delta;SEC(t) = SEC(v2,t) - SEC(v1,t));
4. `mark`: apply a strategy to mark as passing :heavy_check_mark: or failing :x: the commit;
5. `failer`: instrument the tests that have a positive &Delta, meaning that are consuming energy after applying the commit; 
6. `suspect`: run fault localization to rate the modified line according to their suspiciousness;
7. `report`: generate a readable report;

## Plugins

### Instrument

### Delta

### Mark

### Failer

### Suspect

### Report

## Detailed Usage

### Parameters 

```text
Diff-JJoules is a maven plugin that measure the impact of commits on the
  energy consumption of the program.

diff-jjoules:diff-jjoules

  Available parameters:

    classpathPath (Default: classpath)
      Specify the path to a file that contains the full classpath of the project
      for the version before the code changes. We advise use to use the
      following goal to generate it : dependency:build-classpath
      -Dmdep.outputFile=classpath
      User property: classpath-path-v1

    classpathPathV2 (Default: classpath)
      Specify the path to a file that contains the full classpath of the project
      for the version after the code changes. We advise use to use the following
      goal to generate it : dependency:build-classpath
      -Dmdep.outputFile=classpath
      User property: classpath-path-v2

    iterations (Default: 5)
      Number of execution to do to measure the energy consumption of tests.
      User property: iterations

    outputPath (Default: diff-jjoules)
      Specify the path to output the files that produces this plugin
      User property: output-path

    pathDirSecondVersion
      Specify the path to root directory of the project in the second version.
      User property: path-dir-second-version

    pathToDiff (Default: )
      Specify the path of a diff file. If it is not specified, it will be
      computed using diff command line.
      User property: path-to-diff

    pathToExecLinesAdditions (Default: exec_additions.json)
      Specify the path to a json file that contains the list of test methods
      that are executing the additions of the commit.
      User property: path-exec-lines-additions

    pathToExecLinesDeletions (Default: exec_deletions.json)
      Specify the path to a json file that contains the list of test methods
      that are executing the deletions of the commit.
      User property: path-exec-lines-deletions

    pathToJSONConsideredTestMethodNames (Default: consideredTestMethods.json)
      Specify the path to a json file that contains the list of test methods
      that is considered to compute the delta omega.
      User property: path-considered-test-method-names

    pathToJSONDataV1 (Default: data_v1.json)
      Specify the path to a json file that contains the measure of the energy
      consumption of tests for the version before the code changes.
      User property: path-json-data-first-version

    pathToJSONDataV2 (Default: data_v2.json)
      Specify the path to a json file that contains the measure of the energy
      consumption of tests for the version after the code changes.
      User property: path-json-data-second-version

    pathToJSONDelta (Default: deltas.json)
      Specify the path to a json file that contains the deltas per test
      User property: path-json-delta

    pathToJSONDeltaOmega (Default: deltaOmega.json)
      Specify the path to a json file that contains the delta omega to decide to
      pass or fail the build
      User property: path-json-delta-omega

    pathToJSONSuspiciousV1 (Default: suspicious_v1.json)
      Specify the path to a json file that contains the list of test methods
      that are suspicious regarding the version before the commit.
      User property: path-json-suspicious-v2

    pathToJSONSuspiciousV2 (Default: suspicious_v2.json)
      Specify the path to a json file that contains the list of test methods
      that are suspicious regarding the version after the commit.
      User property: path-json-suspicious-v2

    pathToReport (Default: .github/workflows/template.md)
      Specify the path to output the report
      User property: path-to-report

    pathToRepositoryV1
      Specify the path to the root directory of the project before applying the
      commit. This is useful when it is used on multi-modules project.
      User property: path-repo-v1

    pathToRepositoryV2
      Specify the path to the root directory of the project after applying the
      commit. This is useful when it is used on multi-modules project.
      User property: path-repo-v2

    reportType (Default: MARKDOWN)
      Specify the type of report to generate
      User property: report

    shouldSuspect (Default: true)
      Enable or disable the suspect (and failer) goals when running diff-jjoules
      User property: suspect

    testsList (Default: testsThatExecuteTheChange.csv)
      Specify the path to a CSV file that contains the list of tests to be
      instrumented.
      User property: tests-list
```

## Mutation

You can mutate your code in order to increase artificially its energy consumption.
This is done for research purpose on the energy consumption of Software.

For now, there is one mutation. It injects at the beginning of the targeted methods an invokation to the following methods:

```java
public static void consumeEnergy(final long energyToConsume) {
    final EnergySample energySample = RaplDevice.RAPL.recordEnergy();
    long random = new Random().nextLong();
    while (energySample.getEnergyReport().get("package|uJ") < energyToConsume) {
        random += new Random(random).nextLong();
    }
    energySample.stop();
}
```

Which will loop until a given energy amount has been consumed.

### Usage

To use the mutation of `diff-jjoules`, there is a dedicated maven plugin.

The basic command line is the following:
```sh
mvn dependency:build-classpath -Dmdep.outputFile=classpath davidson.fr:diff-jjoules:mutate -DclasspathPath=classpath -DenergyToConsume=10000 -DmethodNamesPerFullQualifiedNames=methodNames.csv
```

It will instrument all the methods of the file `methodNames.csv` specified with the following format:

```csv
fullQualifiedNameClass;methodName1;methodName2;...;methodNameN
```

One line per class.

```txt
diff-jjoules:mutate
  
  Available parameters:

    classpathPath (Default: classpath)
      User property: classpath
      [Optional] Specify the path to a file that contains the full classpath of
      the project. We advise use to use the following goal right before this
      one : dependency:build-classpath -Dmdep.outputFile=classpath

    energyToConsume (Default: 10000)
      User property: energy-to-consume
      [Optional] Specify the amount of energy to be consumed by the mutation.

    methodNamesPerFullQualifiedNames
      User property: method-names-per-full-qualified-names
      [Mandatory] Specify the path to a CSV file that contains the list of
      methods names per full qualified names to be mutated. example :
      fr.davidson.UntareJjoulesMojo;execute
```