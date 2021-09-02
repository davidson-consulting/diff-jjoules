package fr.davidson.diff.jjoules.util.cobertura;

import eu.stamp_project.diff_test_selection.coverage.Coverage;
import fr.davidson.diff.jjoules.util.FullQualifiedName;
import net.sourceforge.cobertura.coveragedata.*;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 02/09/2021
 */
public class CoberturaDataCollector {

    private String testClassName;

    private List<String> testMethodNames;

    private Map<String, Coverage> coveragePerTestMethodName;

    public Map<String, Coverage> getCoveragePerTestMethodName() {
        return coveragePerTestMethodName;
    }

    public CoberturaDataCollector() {
        this.coveragePerTestMethodName = new HashMap<>();
    }

    public void collectData(String testClassName, List<String> testMethodNames, String rootPathFolder) {
        this.testClassName = testClassName;
        this.testMethodNames = testMethodNames;
        this.collectDataProject(
                CoverageDataFileHandler.loadCoverageData(
                        new File(rootPathFolder + "/target/cobertura/cobertura.ser")
                )
        );
    }

    private void collectDataProject(ProjectData projectData) {
        if (projectData.getNumberOfCoveredLines() > 0) {
            collectDataPackages(projectData);
        }
    }

    private void collectDataPackages(ProjectData projectData) {
        for (Object packageData : projectData.getPackages()) {
            this.collectDataPackage((PackageData) packageData);
        }
    }

    private void collectDataPackage(PackageData packageData) {
        if (packageData.getNumberOfCoveredLines() > 0) {
            collectDataSourceFiles(packageData);
        }
    }

    private void collectDataSourceFiles(PackageData packageData) {
        for (Object sourceFileData : packageData.getSourceFiles()) {
            collectDataSourceFile((SourceFileData) sourceFileData);
        }
    }

    private void collectDataSourceFile(SourceFileData sourceFileData) {
        if (sourceFileData.getNumberOfValidLines() > 0) {
            collectDataClasses(sourceFileData);
        }
    }

    private void collectDataClasses(SourceFileData sourceFileData) {
        for (Object classData : sourceFileData.getClasses()) {
            collectDataClass((ClassData) classData);
        }
    }

    private void collectDataClass(ClassData classData) {
        if (classData.getNumberOfCoveredLines() > 0) {
            collectDataMethods(classData);
        }
    }

    private void collectDataMethods(ClassData classData) {
        for (String methodNamesAndDescriptor : classData.getMethodNamesAndDescriptors()) {
            collectDataMethod(classData, methodNamesAndDescriptor);
        }
    }

    private void collectDataMethod(ClassData classData, String methodNamesAndDescriptor) {
        collectDataLines(classData, classData.getLines(methodNamesAndDescriptor));
    }

    private void collectDataLines(ClassData classData, Collection<CoverageData> lines) {
        for (CoverageData line : lines) {
            collectDataLine(classData, (LineData) line);
        }
    }

    private void collectDataLine(ClassData classData, LineData line) {
        if (line.isCovered()) {
            final int lineNumber = line.getLineNumber();
            final long hits = line.getHits();
            for (String testMethodName : this.testMethodNames) {
                final String key = new FullQualifiedName(testClassName, testMethodName).toString();
                if (!this.coveragePerTestMethodName.containsKey(key)) {
                    this.coveragePerTestMethodName.put(key, new Coverage());
                }
                this.coveragePerTestMethodName.get(key).addCoverage(
                        this.testClassName,
                        testMethodName,
                        classData.getPackageName() + "." + classData.getBaseName(),
                        lineNumber,
                        (int) hits
                );
            }
        }
    }
}
