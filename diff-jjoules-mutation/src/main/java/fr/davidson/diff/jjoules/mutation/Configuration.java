package fr.davidson.diff.jjoules.mutation;

import fr.davidson.diff.jjoules.util.Constants;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.davidson.diff.jjoules.util.TestList;
import fr.davidson.diff.jjoules.util.wrapper.Wrapper;
import fr.davidson.diff.jjoules.util.wrapper.WrapperEnum;
import picocli.CommandLine;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 28/02/2022
 */
@CommandLine.Command(name = "fr.davidson.diff.jjoules.mutations.Main", mixinStandardHelpOptions = true, version = "Configuration 0.0.1")
public class Configuration {

    @CommandLine.Option(names = {"-p", "--root-path-folder"}, description = "Path to the root folder of the program.", required = true)
    private String rootPathFolder;

    @CommandLine.Option(names = {"-s", "--src-path-folder"}, description = "Path to the src folder of the program.", defaultValue = "src/main/java")
    private String srcPathFolder;

    @CommandLine.Option(names = {"-m", "--method-list-path"}, description = "Path to the JSON file containing the method full qualified names to mutate.", required = true)
    private String methodListFilePath;

    private TestList methodList;

    @CommandLine.Option(names = {"-c", "--consumption"}, description = "Specify the amount to consume by the mutation.", defaultValue = "10000")
    private long consumption;

    @CommandLine.Option(
            names = "--wrapper",
            defaultValue = "MAVEN",
            description = "Specify the wrapper to be used." +
                    "Valid values: ${COMPLETION-CANDIDATES}" +
                    " Default value: ${DEFAULT-VALUE}"
    )
    private WrapperEnum wrapperEnum;

    private Wrapper wrapper;

    public Configuration() {

    }

    public Configuration(String rootPathFolder,
                         String methodListFilePath,
                         long consumption,
                         WrapperEnum wrapperEnum) {
        this.rootPathFolder = rootPathFolder;
        this.methodListFilePath = methodListFilePath;
        this.consumption = consumption;
        this.wrapperEnum = wrapperEnum;
        this.init();
    }

    public void init() {
        this.methodList = JSONUtils.read(Constants.joinFiles(this.rootPathFolder, methodListFilePath), TestList.class);
        this.wrapper = this.wrapperEnum.getWrapper();
        this.srcPathFolder = this.wrapper.getPathToSrcFolder();
    }

    public String getRootPathFolder() {
        return rootPathFolder;
    }

    public TestList getMethodList() {
        return methodList;
    }

    public long getConsumption() {
        return consumption;
    }

    public String getSrcPathFolder() {
        return srcPathFolder;
    }

    public Wrapper getWrapper() {
        return wrapper;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "rootPathFolder='" + rootPathFolder + '\'' +
                ", srcPathFolder='" + srcPathFolder + '\'' +
                ", methodList=" + methodList +
                ", consumption=" + consumption +
                ", wrapperEnum=" + wrapperEnum +
                '}';
    }
}
