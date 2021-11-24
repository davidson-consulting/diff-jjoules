package fr.davidson.diff.jjoules;

import picocli.CommandLine;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 24/11/2021
 */
public class Main {

    public static void main(String[] args) {
        final Configuration configuration = parse(args);
        final DiffJJoulesStep diffJJoulesStep = new DiffJJoulesStep();
        //diffJJoulesStep.run(configuration);
        diffJJoulesStep.report();
    }

    public static Configuration parse(String[] args) {
        Configuration configuration = new Configuration();
        final CommandLine commandLine = new CommandLine(configuration);
        commandLine.setUsageHelpWidth(120);
        try {
            commandLine.parseArgs(args);
        } catch (Exception e) {
            e.printStackTrace();
            commandLine.usage(System.err);
            System.exit(-1);
        }
        if (commandLine.isUsageHelpRequested()) {
            commandLine.usage(System.out);
            System.exit(0);
        }
        if (commandLine.isVersionHelpRequested()) {
            commandLine.printVersionHelp(System.out);
            System.exit(0);
        }
        configuration.init();
        return configuration;
    }

}
