package fr.davidson.diff.jjoules.report;

import fr.davidson.diff.jjoules.DiffJJoulesMojo;
import fr.davidson.diff.jjoules.DiffJJoulesStep;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 02/05/2022
 */
public class ReportMojo extends DiffJJoulesMojo {
    @Override
    protected DiffJJoulesStep getStep() {
        return new ReportStep();
    }
}
