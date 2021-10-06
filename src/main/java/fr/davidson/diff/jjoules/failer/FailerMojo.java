package fr.davidson.diff.jjoules.failer;

import fr.davidson.diff.jjoules.DiffJJoulesMojo;
import fr.davidson.diff.jjoules.DiffJJoulesStep;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 30/06/2021
 */
@Mojo(name = "failer")
public class FailerMojo extends DiffJJoulesMojo {
    @Override
    protected DiffJJoulesStep getStep() {
        return new FailerStep();
    }
}
