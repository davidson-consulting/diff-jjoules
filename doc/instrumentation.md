# Instrumentation

This plugin aims at instrumenting test cases in order to measure their energy consumption. 
It injects probes from [TLPC-sensor](https://github.com/davidson-consulting/tlpc-sensor) in selected tests.  

The corresponding package in `Diff-JJoules` is [fr.davidson.diff.jjoules.instrumentation](src/main/java/fr/davidson/diff/jjoules/instrumentation).

## Maven plugin Usage

```text
diff-jjoules:instrument
  Implementation: fr.davidson.diff.jjoules.instrumentation.InstrumentationMojo
  Language: java

  Available parameters:

    measureEnergyConsumption (Default: false)
      User property: measure
      (no description available)

    outputPath (Default: diff-jjoules)
      User property: output-path
      Specify the path to output the files that produces this plugin

    pathDirSecondVersion
      Required: true
      User property: path-dir-second-version
      Specify the path to root directory of the project in the second version.

    pathToRepositoryV1
      User property: path-repo-v1
      Specify the path to the root directory of the project before applying the
      commit. This is useful when it is used on multi-modules project.

    pathToRepositoryV2
      User property: path-repo-v2
      Specify the path to the root directory of the project after applying the
      commit. This is useful when it is used on multi-modules project.
```

## Instrumentation example:

Imagine you have the following test method:

```java
public class TestClass {
    @Test
    public void test() {
        // testing something
    }
}
```

The instrumentation would result with the following diff:

```diff
public class TestClass {
    @Test
    public void test() {
    +   fr.davidson.tlpc.sensor.TLPCSensor.start("TestClass#test");
        // testing something
    +   fr.davidson.tlpc.sensor.TLPCSensor.stop("TestClass#test");
    }
}
```

An additionnal instrumentation will be done one of the isnturmented test class:

```diff
public class TestClass {
    // ...
+    static {
+        java.lang.Runtime.getRuntime().addShutdownHook(new java.lang.Thread() {
+            @java.lang.Override
+            public void run() {
+               fr.davidson.tlpc.sensor.TLPCSensor.report("./diff-jjoules-measurements/measurements.json");
+            }
+        });
+    }
}
```

This is done in a performance perspective: it will write a unique JSON file containing all the measurements done during 
the run, as opposed to do on JSON file per measurement, therefore per test method monitored.

The `measurements.json` JSON files contain something like:

```json
{
  "fr.davidson.example.TestClass#test1": {
    "duration": 2001409379,
    "UNHALTED_REFERENCE_CYCLES": 3600313425,
    "LLC_REFERENCES": 936210,
    "BRANCH_INSTRUCTIONS_RETIRED": 1285076266,
    "LLC_MISSES": 302977,
    "MISPREDICTED_BRANCH_RETIRED": 144566,
    "RAPL_ENERGY_PKG": 302977,
    "INSTRUCTIONS_RETIRED": 6884095194
  },
  "fr.davidson.example.TestClass#test2": {
    "duration": 4000910497,
    "UNHALTED_REFERENCE_CYCLES": 7197788850,
    "LLC_REFERENCES": 1524894,
    "BRANCH_INSTRUCTIONS_RETIRED": 2579974282,
    "LLC_MISSES": 437354,
    "MISPREDICTED_BRANCH_RETIRED": 161963,
    "RAPL_ENERGY_PKG": 437354,
    "INSTRUCTIONS_RETIRED": 13821902254
  }
}
```