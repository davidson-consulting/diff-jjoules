# Diff-JJoules · [![tests](https://github.com/davidson-consulting/diff-jjoules/actions/workflows/master.yml/badge.svg)](https://github.com/davidson-consulting/diff-jjoules/actions/workflows/master.yml)  [![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=davidson-consulting_diff-jjoules&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=davidson-consulting_diff-jjoules) [![codecov](https://codecov.io/gh/davidson-consulting/diff-jjoules/branch/master/graph/badge.svg?token=XH4Q36YMME)](https://codecov.io/gh/davidson-consulting/diff-jjoules) 

`diff-jjoules` is a set of maven plugins that measures the impact of commits on the energy consumption of the program.

## Install

To use Diff-JJoules, you need to install [JJoules](https://github.com/davidson-consulting/j-joules), [JUnit-JJoules](https://github.com/davidson-consulting/junit-jjoules) and [Diff-Test-Selection](https://github.com/STAMP-project/dspot/tree/master/dspot-diff-test-selection).

Then, you can install `diff-jjoules`:

```sh
mvn clean install -DskipTest
```

## Usage

The easiest way to use `diff-jjoules`, you need to clone your project twice: one for the version before applying the 
commit, and one for the version after applying the commit. 
In the following, we consider **path/v1** as the path to the version of the program before applying the commit, and 
**path/v2** as the path to the version of the program after applying the commit.

You can run `diff-jjoules` with the following command line, from **path/v1**, where your `pom.xml` is:

```shell
mvn fr.davidson:diff-jjoules:diff-jjoules -Dpath-dir-second-version=path/v2
```

Calling this maven plugin will apply the whole process as follow:
InstrumentationStepTest
1. `test-selection`: select the tests that execute the code changes (this is done by `diff-test-selection`;
2. [`instrumention`](./doc/instrumentation.md): instrument the selected tests with probes to measure their energy consumption;
3. `delta`: computation of the energy consumption delta test-wise (&Delta;SEC(t) = SEC(v2,t) - SEC(v1,t));
4. `mark`: apply a strategy to mark as passing :heavy_check_mark: or failing :x: the commit;
5. `failer`: instrument the tests that have a positive &Delta, meaning that are consuming energy after applying the commit; 
6. `suspect`: run fault localization to rate the modified line according to their suspiciousness;
7. `report`: generate a readable report;

For more information, you can view a dedicated README for each plugin, however, we advise you to not run the plugin 
individually since they are made to work together.

## Options

In this section, we list the options that are configurables.

:construction:

## Mutation

Diff-JJoules offers a side maven plugin, which allows you to mutate the code in order to increase artificially its energy
consumption. This is done for research purpose on the energy consumption of Software.

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