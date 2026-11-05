package academy.incidentradar.readers;

import academy.incidentradar.source.Glob;
import academy.incidentradar.source.InputSource;
import academy.incidentradar.source.LocalFile;
import academy.incidentradar.source.Remote;
import java.io.IOException;
import java.util.stream.Stream;

public final class Reader {

    private Reader() {}

    public static Stream<String> getStream(InputSource src) throws IOException, InterruptedException {
        return switch (src) {
            case Remote r -> RemoteReader.readFromUrl(r.uri());
            case LocalFile f -> LineReader.open(f.path());
            case Glob g -> GlobReader.readFromGlob(g.pattern());
        };
    }
}
