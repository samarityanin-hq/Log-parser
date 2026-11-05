package academy.incidentradar.source;

import academy.incidentradar.validation.InputFormat;
import academy.incidentradar.validation.UsageException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

public sealed interface InputSource permits Remote, Glob, LocalFile {

    static InputSource parse(String path) {
        if (path == null || path.isEmpty()) {
            throw new UsageException("Передан пустой путь");
        }

        if (path.startsWith("http://") || path.startsWith("https://")) {
            URI uri;
            try {
                uri = new URI(path);

            } catch (URISyntaxException e) {
                throw new UsageException("некорректный URL: " + path, e);
            }
            if (uri.getHost() == null) {
                throw new UsageException("В URL нет хоста: " + path);
            }

            InputFormat.parse(uri.getPath() == null ? "" : uri.getPath());
            return new Remote(uri);
        }

        InputFormat.parse(path);
        return isGlob(path) ? new Glob(path) : new LocalFile(Path.of(path));
    }

    private static boolean isGlob(String str) {
        return str.indexOf('*') >= 0 || str.indexOf('?') >= 0 || str.indexOf('[') >= 0 || str.indexOf('{') >= 0;
    }
}
