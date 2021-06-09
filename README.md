# Diff-JJoules

**Diff-JJoules** is a set of maven plugin to run various procedures that aims at measuring the energy consumption delta 
between two versions of the same program.

This project can be applied on Maven projects in Java and with JUnit.

It relies on the two following projects:

1. https://github.com/davidson-consulting/junit-jjoules to measure the energy consumption
2. https://github.com/STAMP-project/dspot/tree/master/dspot-diff-test-selection to select
tests methods that execute the lines that changed.

## Install

To use Diff-Jjoules, you need to install `junit-jjoules`. Please follow the instruction of the [README]()
Ensure that you do configure well `junit-jjoules`.

Then, you can install `diff-jjoules`:

```sh
mvn clean install -DskipTest
```

## Usage

To use easily `diff-jjoules`, we provide python scripts that use maven plugins.

```sh
$ python3 src/main/python/run.py --help                              
usage: run.py [-h] [-f FIRST_VERSION_PATH] [-s SECOND_VERSION_PATH] [-i ITERATION]

optional arguments:
  -h, --help            show this help message and exit
  -f FIRST_VERSION_PATH, --first-version-path FIRST_VERSION_PATH
                        Specify the path to the folder of the first version of the program, i.e. before the commit.
  -s SECOND_VERSION_PATH, --second-version-path SECOND_VERSION_PATH
                        Specify the path to the folder of the second version of the program, i.e. after the commit.
  -i ITERATION, --iteration ITERATION
                        Specify the number of time tests will be executed for measuring the energy consumption.
```

This script will output two json files that contains the average, over the iterations, of the energy consumption for each test that execute the changes, on per version.
It will also print on the standard output the delta between both versions, test method wise.

# Availaible Maven Plugins

## Instrumentation

The first maven plugin provided an automatic instrumentation of test methods in order to replace classical JUnit `@Test` by `@EnergyTest` to collect data about energy consumption of an unit test.

### Usage

To use this plugin, you must : 

* Have both versions of your program
* Compute both classpath for both versions of the program (use `dependency:build-classpath -Dmdep.outputFile=classpath` to compute it)
* Have a CSV file containing the list of tests to be instrumented with the following format :

```
fullQualifiedNameTestClass;testMethodName1;testMethodName2;...;testMethodNameN
```

Then run:

```
mvn fr.davidson:diff-jjoules:instrument -DclasspathPath=classpath -DclasspathPathV2=../v2/classpath -DpathDirSecondVersion=../v2/ -DtestsList=testsThatExecuteTheChange.csv
```

You can make collaborate maven plugin for easier usage. For exemple, you can compute the list of tests that execute the changes between you two version of the same program using `dspot-diff-test-selection` maven plugin.

For example:

Considering that `/tmp/v1` and `/tmp/v2` contains respectively the version of the program before the commit and after the commit, run the two subsequents maven command:
```
mvn -f /tmp/v2/pom.xml clean install -DskipTests dependency:build-classpath -Dmdep.outputFile=classpath -f /tmp/v2/pom.xml
mvn -f /tmp/v1/pom.xml clean install -DskipTests dependency:build-classpath -Dmdep.outputFile=classpath  fr.
eu.stamp-project:dspot-diff-test-selection:3.1.1-SNAPSHOT:list -Dpath-dir-second-version=/tmp/v2/ -Dtests-list=testsThatExecuteTheChange.csv
davidson:diff-jjoules:instrument -Dclasspath-path-v1=classpath -Dclasspath-path-v2=/tmp/v2/classpath
```

This two command lines will:

1. Build your V2 program and generate its classpath file
2. Build your V1 program and generate its classpath file
3. Select the test that execute the code changes between V1 and V2
4. Instrument the selected tests

This can be done because `dspot-diff-test-selection` and `diff-jjoules:instrument` shares the same options names.

```txt
diff-jjoules:instrument

  Available parameters:

    classpathPath (Default: classpath)
      User property: classpath-path-v1
      [Optional] Specify the path to a file that contains the full classpath of the project. We advise use to use the following goal right before this one : dependency:build-classpath -Dmdep.outputFile=classpath

    classpathPathV2 (Default: classpath)
      User property: classpath-path-v2
      [Optional] Specify the path to a file that contains the full classpath of the project. We advise use to use the following goal right before this one : dependency:build-classpath -Dmdep.outputFile=classpath

    pathDirSecondVersion
      User property: path-dir-second-version
      [Mandatory] Specify the path to root directory of the project in the second version.

    testsList
      User property: tests-list
      [Mandatory] Specify the path to a CSV file that contains the list of tests to be instrumented.
```

## Analysis

### Usage

## Location

### Usage

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

## Example

We provide an example to run `diff-jjoules`. This is `google/gson`, which an open-source project avalaible on GitHub.

To run this example, you can use the following python script:

```sh
python3 src/main/python/example.py
```

This script will:

1. clone twice `google/gson` in `/tmp/example_v1`, `/tmp/example_v2`.
2. set these folder to specific commits, that we know `diff-jjoules` works, _i.e._ the delta between the versions is measurable.
3. run the provided script `src/main/python/run.py` with the correct parameters:
    `$ python3 src/main/python/run.py --first-version-path /tmp/example_v1 --second-version-path /tmp/example_v2 --iteration 1`

At the end of the execution, you will have the delta between both versions, test method wise, printed on the stdout.
And two json files `avg_v1.json` and `avg_v2.json` that contains the average, over the iterations, of the energy consumption for each test that execute the changes, on per version.

## Detail behind the script `src/main/python/run.py`

The script `src/main/python/run.py` performs the following steps:

1. It runs `mvn clean install -DskipTests` on both versions.
2. It runs `dspot-diff-test-selection` maven plugin to select test methods that execute the lines that changed.
3. It runs `diff-jjoules` maven plugin to instrument the test methods selected at the previous step. It also inject some dependencies in the `pom.xml` of both versions.
4. It runs X times the tests, and compute the average of the energy consumption and the duration for each test methods.
5. It computes the delta of the energy consumption between both versions, test method wise.