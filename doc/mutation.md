# Mutation

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

## Usage

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