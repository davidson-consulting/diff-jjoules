package fr.davidson.diff.jjoules.instrumentation;

import fr.davidson.diff.jjoules.DiffJJoulesMojo;
import fr.davidson.diff.jjoules.DiffJJoulesStep;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 25/08/2021
 */
@Mojo(name = "instrument")
public class InstrumentationMojo extends DiffJJoulesMojo {
    @Override
    protected DiffJJoulesStep getStep() {
        return new InstrumentationStep();
    }
}
