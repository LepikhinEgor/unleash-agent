package ru.baldenna.unleashagent.cli;

public record CliArgs(
        String unleashUrl,
        String configurationFilePath,
        String unleashLogin,
        String unleashPassword
) {
}
