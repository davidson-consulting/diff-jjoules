package fr.davidson.diff.jjoules.report.markdown;

import fr.davidson.diff.jjoules.DiffJJoulesMojo;
import fr.davidson.diff.jjoules.DiffJJoulesStep;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 25/06/2021
 */
@Mojo(name = "markdown")
public class MarkdownMojo extends DiffJJoulesMojo {
    @Override
    protected DiffJJoulesStep getStep() {
        return new MarkdownStep();
    }
}
