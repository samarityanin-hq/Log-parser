package academy.incidentradar.source;

import java.nio.file.Path;

public record LocalFile(Path path) implements InputSource {}
