package fr.davidson.diff.jjoules.analysis.output;

import fr.davidson.diff.jjoules.analysis.lines.LinesDeltaClassifier;
import fr.davidson.diff.jjoules.analysis.tests.TestDeltaClassifier;

public interface Report {

    public void outputTestsClassification(TestDeltaClassifier testDeltaClassifier);

    public void outputLinesClassification(LinesDeltaClassifier linesDeltaClassifier);

}
