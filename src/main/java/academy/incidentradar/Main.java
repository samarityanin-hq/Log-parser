package academy.incidentradar;

import academy.incidentradar.cli.Cli;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

/** Точка входа в Incident Radar. */
public final class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private Main() {}

    public static void main(String[] args) {
        CommandLine cli = new CommandLine(new Cli());
        cli.setCaseInsensitiveEnumValuesAllowed(true);

        int exitCode = cli.execute(args);
        System.exit(exitCode);
    }
}
