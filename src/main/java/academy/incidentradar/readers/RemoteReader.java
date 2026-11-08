package academy.incidentradar.readers;

import academy.incidentradar.validation.UsageException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RemoteReader {
    private static final int TIMEOUT = 10;
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(TIMEOUT))
            .build();
    private static final int SUCCESSFUL = 200;
    private static final int REDIRECTION = 300;
    private static final int CLIENT_ERROR = 400;
    private static final int SERVER_ERROR = 500;

    private static final Logger LOG = LoggerFactory.getLogger(RemoteReader.class);

    private RemoteReader() {}

    public static Stream<String> readFromUrl(URI uri) throws IOException, InterruptedException {
        HttpRequest request = sendRequest(uri);
        HttpResponse<InputStream> response = getResponse(request);

        int code = response.statusCode();
        if (code < SUCCESSFUL || code >= REDIRECTION) {
            response.body().close();
            if (code >= CLIENT_ERROR && code < SERVER_ERROR) {
                throw new UsageException("Код ошибки: " + response.statusCode());
            }
            throw new IOException("Код ошибки: " + response.statusCode());
        }

        InputStream stream = response.body();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

        return reader.lines().onClose(() -> {
            try {
                reader.close();
            } catch (IOException e) {
                LOG.error("Ошибка чтения с URL: {}", e.getMessage());
            }
        });
    }

    private static HttpRequest sendRequest(URI uri) {
        return HttpRequest.newBuilder()
                .timeout(Duration.ofSeconds(TIMEOUT))
                .uri(uri)
                .GET()
                .build();
    }

    private static HttpResponse<InputStream> getResponse(HttpRequest request) throws InterruptedException, IOException {
        try {
            return CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());
        } catch (HttpTimeoutException e) {
            throw new UsageException("Превышено время ожидание ответа: " + e.getMessage());
        }
    }
}
