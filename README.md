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

## Analysis

## Location

## Mutation

You can to mutate your code in order to increase its energy consumption.
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