package ru.hse.pdg4j.impl.user.log;

import picocli.CommandLine;

public class LogOptions {
    @CommandLine.Option(names = {"-s", "--silent"},
            defaultValue = "false",
            description = "Set whether execution is silent",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    private boolean silent;

    public boolean isSilent() {
        return silent;
    }

    @Override
    public String toString() {
        return "LogOptions{" +
                "silent=" + silent +
                '}';
    }
}
