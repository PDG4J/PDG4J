package ru.hse.pdg4j.impl.user.export;

import picocli.CommandLine;

import java.util.Set;

public class ExportOptions {

    @CommandLine.ArgGroup(exclusive = false)
    private Export export;

    public Export getExport() {
        return export;
    }

    static class Export {
        @CommandLine.Option(names = "-ex", description = "A set of structures to export", required = true)
        private Set<ExportType> exportTypeSet;

        @CommandLine.Option(names = "-exr", description = "Folder in which exported files are located", required = true)
        private String exportRoot;

        public Set<ExportType> getExportTypeSet() {
            return exportTypeSet;
        }

        public String getExportRoot() {
            return exportRoot;
        }
    }
}
