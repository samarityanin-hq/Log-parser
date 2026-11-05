package academy.incidentradar.readers;

import academy.incidentradar.validation.UsageException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.stream.Stream;

public final class GlobReader {

    private GlobReader() {}

    public static Stream<String> readFromGlob(String pattern) throws IOException {
        List<Path> files = findFiles(pattern);
        if (files.isEmpty()) {
            throw new UsageException("По маске ничего не найдено: " + pattern);
        }
        return files.stream().flatMap(p -> {
            try {
                return LineReader.open(p);
            } catch (IOException e) {
                throw new UncheckedIOException("Ошибка чтения: " + p, e);
            }
        });
    }

    private static List<Path> findFiles(String pattern) throws IOException {
        String normalizedPattern = pattern.replace("\\", "/");

        int firstMeta = normalizedPattern.length();

        for (int i = 0; i < normalizedPattern.length(); i++) {
            if ("*?[{".indexOf(normalizedPattern.charAt(i)) >= 0) {
                firstMeta = i;
                break;
            }
        }

        int lastSlash = normalizedPattern.lastIndexOf('/', firstMeta);

        String base;
        String glob;

        if (lastSlash < 0) {
            base = ".";
            glob = normalizedPattern;
        } else {
            base = normalizedPattern.substring(0, lastSlash + 1);
            glob = normalizedPattern.substring(lastSlash + 1);
        }

        Path basePath = Path.of(base).toAbsolutePath().normalize();
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + glob);

        try (Stream<Path> walk = Files.walk(basePath)) {
            return walk.filter(Files::isRegularFile)
                    .filter(path -> matcher.matches(basePath.relativize(path)))
                    .sorted()
                    .toList();
        }
    }
}
