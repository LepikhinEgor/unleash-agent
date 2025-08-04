package ru.baldenna.unleashagent.cli;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CliArgsValidator {

    public CliArgs validateAngGetArgs(String[] args) {
        Options options = new Options();

        Option configurationFIle = new Option("f", "file", true, "Path to file with unleash configurations");
        configurationFIle.setRequired(true);
        options.addOption(configurationFIle);

        Option unleashUrl = new Option("u", "unleash-url", true, "Unleash server URL");
        configurationFIle.setRequired(true);
        options.addOption(unleashUrl);

        CommandLineParser parser = new DefaultParser();

        try {
            var cliArgs = parser.parse(options, args);
            return new CliArgs(
                    cliArgs.getOptionValue("unleash-url"),
                    cliArgs.getOptionValue("file")
            );
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("utility-name", options);
            throw new RuntimeException(e);
        }
    }
}
