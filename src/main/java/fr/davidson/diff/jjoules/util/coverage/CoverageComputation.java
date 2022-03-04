package fr.davidson.diff.jjoules.util.coverage;

import eu.stamp_project.diff_test_selection.coverage.Coverage;
import eu.stamp_project.testrunner.EntryPoint;
import eu.stamp_project.testrunner.listener.CoveredTestResultPerTestMethod;
import eu.stamp_project.testrunner.listener.impl.CoverageDetailed;
import eu.stamp_project.testrunner.listener.impl.CoverageFromClass;
import eu.stamp_project.testrunner.runner.ParserOptions;
import fr.davidson.diff.jjoules.util.Constants;

import java.io.File;
import java.util.List;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 19/11/2021
 */
public class CoverageComputation {

    public static CoveredTestResultPerTestMethod getCoverage(
            String pathToRootOfProject,
            String classpath,
            boolean junit4,
            List<String> allFullQualifiedNameTestClasses,
            List<String> testMethodNames,
            String pathToBinaries) {
        try {
            EntryPoint.coverageDetail = ParserOptions.CoverageTransformerDetail.DETAIL_COMPRESSED;
            EntryPoint.workingDirectory = new File(pathToRootOfProject);
            EntryPoint.verbose = true;
            EntryPoint.jUnit5Mode = !junit4;
            EntryPoint.nbFailingLoadClass = 5;
            EntryPoint.timeoutInMs = 100000;
            //EntryPoint.JVMArgs = "-Djava.locale.providers=COMPAT,CLDR,SPI";
            return EntryPoint.runOnlineCoveredTestResultPerTestMethods(
                    classpath + Constants.PATH_SEPARATOR + pathToBinaries,
                    pathToBinaries,
                    allFullQualifiedNameTestClasses.toArray(new String[0]),
                    testMethodNames.toArray(new String[0])
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static CoveredTestResultPerTestMethod getCoverage(
            String pathToRootOfProject,
            String classpath,
            boolean junit4,
            List<String> allFullQualifiedNameTestClasses,
            String pathToBinaries) {
        try {
            EntryPoint.coverageDetail = ParserOptions.CoverageTransformerDetail.DETAIL_COMPRESSED;
            EntryPoint.workingDirectory = new File(pathToRootOfProject);
            EntryPoint.verbose = true;
            EntryPoint.jUnit5Mode = !junit4;
            EntryPoint.nbFailingLoadClass = 5;
            EntryPoint.timeoutInMs = 100000;
            //EntryPoint.JVMArgs = "-Djava.locale.providers=COMPAT,CLDR,SPI";
            return EntryPoint.runOnlineCoveredTestResultPerTestMethods(
                    classpath + Constants.PATH_SEPARATOR + pathToBinaries,
                    pathToBinaries,
                    allFullQualifiedNameTestClasses.toArray(new String[0])
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Coverage convert(CoveredTestResultPerTestMethod coveredTestResultPerTestMethod) {
        Coverage coverage = new Coverage();
        for (String fullQualifiedNameTestMethod : coveredTestResultPerTestMethod.getCoverageResultsMap().keySet()) {
            final String[] split = fullQualifiedNameTestMethod.split("#");
            final CoverageDetailed coverageOf = (CoverageDetailed) coveredTestResultPerTestMethod.getCoverageOf(fullQualifiedNameTestMethod);
            for (String pathName : coverageOf.getDetailedCoverage().keySet()) {
                final CoverageFromClass coverageFromClass = coverageOf.getDetailedCoverage().get(pathName);
                final String fullQualifiedNameClass = pathName.replaceAll("/", ".");
                for (Integer line : coverageFromClass.getCov().keySet()) {
                    if (coverageFromClass.getCov().get(line) > 0) {
                        coverage.addCoverage(split[0], split[1], fullQualifiedNameClass, line, 1);
                    }
                }
            }
        }
        return coverage;
    }

}
