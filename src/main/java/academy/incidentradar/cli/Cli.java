package academy.incidentradar.cli;

import academy.incidentradar.readers.Reader;
import academy.incidentradar.source.InputSource;
import academy.incidentradar.validation.ReportFormat;
import academy.incidentradar.validation.UsageException;
import academy.incidentradar.validation.Validator;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.concurrent.Callable;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "incident-radar", description = "Входные параметры утилиты", mixinStandardHelpOptions = true)
public class Cli implements Callable<Integer> {

    @Option(
            names = {"-p", "--path"},
            description = "Файл, glob-маска или URL",
            required = true)
    private String path = "";

    @Option(
            names = {"-f", "--output-format"},
            description = "Формат отчета (json или markdown), по умолчанию json")
    private ReportFormat reportFormat = ReportFormat.JSON;

    @Option(
            names = {"-o", "--output-path"},
            description = "Путь отчета, если не указан формируется сам report. + reportFormat")
    private @Nullable Path outPath;

    @Option(names = "--from", description = "начало границы yyyy-MM-dd", required = false)
    private @Nullable LocalDate from;

    @Option(names = "--to", description = "конец границы yyyy-MM-dd", required = false)
    private @Nullable LocalDate to;

    @Override
    public Integer call() {
        Path out = outPath != null ? outPath : Path.of("report." + reportFormat.getExtension());

        InputSource source;
        try {
            source = InputSource.parse(path);
            Validator.validateOutput(out, reportFormat);
            Validator.validateBorders(from, to);
            try (Stream<String> lines = Reader.getStream(source)) { // Заглушка чтения потока
                lines.forEach(l -> {});
            }
            return 0;

        } catch (UsageException e) {
            System.err.println(e.getMessage());
            return 2;
        } catch (IOException | UncheckedIOException e) {
            System.err.println("Ошибка чтения: " + e.getMessage());
            return 1;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return 1;
        }
    }
}
