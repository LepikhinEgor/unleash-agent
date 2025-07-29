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
        unleashUrl.setRequired(true);
        options.addOption(unleashUrl);

        Option unleashLogin = new Option("l", "login", true, "Unleash login/username");
        unleashLogin.setRequired(true);
        options.addOption(unleashLogin);

        Option unleashPassword = new Option("p", "password", true, "Unleash user password");
        unleashPassword.setRequired(true);
        options.addOption(unleashPassword);

        CommandLineParser parser = new DefaultParser();

        try {
            var cliArgs = parser.parse(options, args);
            return new CliArgs(
                    cliArgs.getOptionValue("unleash-url"),
                    cliArgs.getOptionValue("file"),
                    cliArgs.getOptionValue("login"),
                    cliArgs.getOptionValue("password")
            );
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("utility-name", options);
            throw new RuntimeException(e);
        }
    }
}
