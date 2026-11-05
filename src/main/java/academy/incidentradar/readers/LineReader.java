package academy.incidentradar.readers;

import academy.incidentradar.validation.UsageException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public final class LineReader {

    private LineReader() {}

    public static Stream<String> open(Path path) throws IOException {
        if (!Files.isRegularFile(path)) {
            throw new UsageException("Файл не найден");
        }
        var decoder = StandardCharsets.UTF_8
                .newDecoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE);

        BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(path), decoder));

        return reader.lines().onClose(() -> {
            try {
                reader.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }
}
