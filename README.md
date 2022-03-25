# Diff-JJoules · [![tests](https://github.com/davidson-consulting/diff-jjoules/actions/workflows/main.yml/badge.svg)](https://github.com/davidson-consulting/diff-jjoules/actions/workflows/main.yml)  [![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=davidson-consulting_diff-jjoules&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=davidson-consulting_diff-jjoules) [![codecov](https://codecov.io/gh/davidson-consulting/diff-jjoules/branch/main/graph/badge.svg?token=XH4Q36YMME)](https://codecov.io/gh/davidson-consulting/diff-jjoules)

`diff-jjoules` is a tool to be integrated in your continuous integration in order to 
* measure the impact of commits on the energy consumption of the program 
* break the build in case of an energy regression has been detected.

## Prerequisites

* Java 8+
* Maven3

To use `diff-jjoules`, you need to install:

* [TLPC-sensor](https://github.com/davidson-consulting/tlpc-sensor) and the maven plugin in [examples/tlpc-sensor](https://github.com/davidson-consulting/tlpc-sensor/tree/main/examples/tlpc-sensor)

## Install

Then, you can install `diff-jjoules`:

```sh
git clone https://github.com/davidson-consulting/diff-jjoules.git
cd diff-jjoules
mvn clean install -DskipTest
```

You should also install the maven plugin, from `diff-jjoules` folder:

```sh
cd diff-jjoules-maven
mvn clean install -DskipTest
```

## Usage

The easiest way to use `diff-jjoules`, you need to clone your project twice: one for the version before applying the 
commit, and one for the version after applying the commit. 
In the following, we consider **path/v1** as the path to the version of the program before applying the commit, and 
**path/v2** as the path to the version of the program after applying the commit.

You can run `diff-jjoules` with the following command line, from **path/v1**, where your `pom.xml` is:

```shell
mvn fr.davidson:diff-jjoules:diff-jjoules-maven -Dpath-dir-second-version=path/v2
```

## Detailed Documentation

We provide a detailed documentation [here](./doc/documentation.md).

## Contributing

If you have any questions, remarks, suggestions or bug reports, please do not hesitate to open an issue.
Diff-JJoules is licensed under GNU GPL.
Contributions and pull requests are very welcome :smiley:. 
For more information on contributing, see the dedicated [CONTRIBUTING.md](./CONTRIBUTING.md).