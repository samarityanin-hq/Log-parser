package academy.incidentradar.validation;

import java.nio.file.Path;
import java.time.LocalDate;
import org.jspecify.annotations.Nullable;

public final class Validator {

    private Validator() {}

    public static void validateOutput(@Nullable Path path, ReportFormat format) {
        if (path == null) {
            return;
        }
        String name = path.getFileName().toString().toLowerCase();
        ReportFormat.parse(path.toString());
        if (!name.endsWith("." + format.getExtension())) {
            throw new UsageException("Расширение отчета не совпадает с форматом");
        }
    }

    public static void validateBorders(@Nullable LocalDate from, @Nullable LocalDate to) {
        if (from == null || to == null) {
            return;
        }

        if (from.isAfter(to) || from.isEqual(to)) {
            throw new UsageException("Границы указаны неверно");
        }
    }
}
