package ru.hse.pdg4j.impl.user.launcher;

import picocli.CommandLine;

public class LauncherOptions {
    @CommandLine.Option(names = "-src", description = "Path to source root", required = true)
    private String sourcePath;

    @CommandLine.Option(names = "-cp", description = "Classpath")
    private String classpath;

    public String getSourcePath() {
        return sourcePath;
    }

    public String getClasspath() {
        return classpath;
    }

    @Override
    public String toString() {
        return "LauncherOptions{" +
                "sourcePath='" + sourcePath + '\'' +
                ", classpath='" + classpath + '\'' +
                '}';
    }
}
