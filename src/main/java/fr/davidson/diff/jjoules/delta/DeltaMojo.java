package fr.davidson.diff.jjoules.delta;

import fr.davidson.diff.jjoules.DiffJJoulesMojo;
import fr.davidson.diff.jjoules.DiffJJoulesStep;
import org.apache.maven.plugins.annotations.Mojo;


/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 23/06/2021
 */
@Mojo(name = "delta")
public class DeltaMojo extends DiffJJoulesMojo {
    @Override
    protected DiffJJoulesStep getStep() {
        return new DeltaStep();
    }
}
