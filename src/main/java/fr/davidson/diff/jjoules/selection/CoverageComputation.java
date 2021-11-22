package fr.davidson.diff.jjoules.selection;

import eu.stamp_project.diff_test_selection.coverage.Coverage;
import eu.stamp_project.testrunner.EntryPoint;
import eu.stamp_project.testrunner.listener.CoveredTestResultPerTestMethod;
import eu.stamp_project.testrunner.listener.impl.CoverageDetailed;
import eu.stamp_project.testrunner.listener.impl.CoverageFromClass;
import eu.stamp_project.testrunner.runner.ParserOptions;

import java.io.File;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 19/11/2021
 */
public class CoverageComputation {

    public static final String PATH_SEPARATOR = System.getProperty("path.separator");

    public static final String PATH_TO_BINARIES = "target/classes" + PATH_SEPARATOR + "target/test-classes";

    private String mainPackageName;

    public CoverageComputation(String mainPackageName) {
        this.mainPackageName = mainPackageName;
    }

    public static CoveredTestResultPerTestMethod getCoverage(
            String pathToRootOfProject,
            String classpath,
            boolean junit4) {
        try {
            EntryPoint.coverageDetail = ParserOptions.CoverageTransformerDetail.DETAIL_COMPRESSED;
            EntryPoint.workingDirectory = new File(pathToRootOfProject);
            EntryPoint.verbose = true;
            EntryPoint.jUnit5Mode = !junit4;
            return EntryPoint.runOnlineCoveredTestResultPerTestMethods(
                    classpath + PATH_SEPARATOR + PATH_TO_BINARIES,
                    PATH_TO_BINARIES,
                    "fr.davidson.diff_jjoules_demo.InternalListTest"
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Coverage convert(CoveredTestResultPerTestMethod coveredTestResultPerTestMethod) {
        Coverage coverage = new Coverage();
        for (String fullQualifiedNameTestMethod : coveredTestResultPerTestMethod.getCoverageResultsMap().keySet()) {
            final String[] split = fullQualifiedNameTestMethod.split("#");
            final eu.stamp_project.testrunner.listener.impl.CoverageDetailed coverageOf = (CoverageDetailed) coveredTestResultPerTestMethod.getCoverageOf(fullQualifiedNameTestMethod);
            for (String pathName : coverageOf.getDetailedCoverage().keySet()) {
                if (pathName.startsWith("fr/davidson/")) {
                    final CoverageFromClass coverageFromClass = coverageOf.getDetailedCoverage().get(pathName);
                    final String fullQualifiedNameClass = pathName.replaceAll("/", ".");
                    for (Integer line : coverageFromClass.getCov().keySet()) {
                        if (coverageFromClass.getCov().get(line) > 0) {
                            coverage.addCoverage(split[0], split[1], fullQualifiedNameClass, line, 1);
                        }
                    }
                }
            }
        }
        return coverage;
    }

}
