# Instrumentation

This plugin aims at instrumenting test cases in order to measure their energy consumption.

## Usage

 :construction:

## Details

According to the used JUnit version (only JUnit5, JUnit4 and JUnit3 are supported) the instrumentation is different.

For JUnit 5:
```diff
-@Test
+@EnergyTest
public void test() {
    // ...
}
```

For JUnit 4 \& JUnit 3:
```diff
@Test
public void test() {
    + org.powerapi.jjoules.junit4.EnergyTest.beforeTest("mypackage.MyTestClass", "test");
    // ...
    + org.powerapi.jjoules.junit4.EnergyTest.afterTest();
}
```

In both cases, the code comes from [JUnit-JJoules](https://github.com/davidson-consulting/junit-jjoules).