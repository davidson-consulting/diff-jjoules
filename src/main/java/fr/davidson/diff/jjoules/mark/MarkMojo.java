package fr.davidson.diff.jjoules.mark;

import fr.davidson.diff.jjoules.DiffJJoulesMojo;
import fr.davidson.diff.jjoules.DiffJJoulesStep;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 21/06/2021
 */
@Mojo(name = "mark")
public class MarkMojo extends DiffJJoulesMojo {

    @Override
    protected DiffJJoulesStep getStep() {
        return new MarkStep();
    }
}
