package fr.davidson.diff.jjoules.mark.strategies;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.util.FullQualifiedName;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.davidson.diff.jjoules.util.MethodNamesPerClassNames;
import fr.davidson.diff.jjoules.util.wrapper.WrapperEnum;

import java.io.File;
import java.util.HashSet;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/05/2022
 */
public abstract class AbstractMarkStrategyTest {

    protected abstract MarkStrategyEnum getStrategy();

    public boolean runStrategy() {
        final Datas dataV1 = JSONUtils.read("src/test/resources/json/gson_data_v1.json", Datas.class);
        final Datas dataV2 = JSONUtils.read("src/test/resources/json/gson_data_v2.json", Datas.class);
        final Deltas deltas = JSONUtils.read("src/test/resources/json/gson_deltas.json", Deltas.class);
        return this.runStrategy(dataV1, dataV2, deltas);
    }

    public boolean runStrategy(Datas dataV1,
                               Datas dataV2,
                               Deltas deltas) {
        final Configuration configuration = new Configuration(
                new File("src/test/resources/v1").getAbsolutePath(),
                new File("src/test/resources/v2").getAbsolutePath(),
                1,
                false
        );
        configuration.setOutput("target");
        configuration.setWrapperEnum(WrapperEnum.MAVEN);
        final MethodNamesPerClassNames consideredTest = new MethodNamesPerClassNames();
        for (String testMethodFullQualifiedName : deltas.keySet()) {
            final FullQualifiedName fullQualifiedName = FullQualifiedName.fromString(testMethodFullQualifiedName);
            if (!consideredTest.containsKey(fullQualifiedName.className)) {
                consideredTest.put(fullQualifiedName.className, new HashSet<>());
            }
            consideredTest.get(fullQualifiedName.className).add(fullQualifiedName.methodName);
        }
        return this.getStrategy().getStrategy().applyStrategy(
                configuration,
                dataV1,
                dataV2,
                deltas,
                consideredTest
        );
    }

}
