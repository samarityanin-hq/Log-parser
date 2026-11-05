package academy.incidentradar.mandatory.mr1;

import static org.assertj.core.api.Assertions.assertThat;

import academy.incidentradar.support.CliRunner;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Обязательные тесты: источники логов. */
@DisplayName("MR1. Источники логов")
class LogSourceTest {
    private static final Path SAMPLES = Path.of("samples", "logs");
    private static final String DAY_ONE = "access-2025-03-01.log";
    private static final String DAY_TWO = "access-2025-03-02.log";

    @TempDir
    private Path tempDir;

    private HttpServer server;

    @BeforeEach
    void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.createContext("/access.log", exchange -> {
            byte[] body = Files.readAllBytes(SAMPLES.resolve(DAY_ONE));
            exchange.sendResponseHeaders(200, body.length);
            try (var out = exchange.getResponseBody()) {
                out.write(body);
            }
        });
        server.start();
    }

    @AfterEach
    void serverStop() {
        server.stop(0);
    }

    @Test
    @DisplayName("Локальный файл читается")
    void readsLocalFile() {
        var result =
                CliRunner.run("-p", SAMPLES.resolve(DAY_ONE).toAbsolutePath().toString());

        assertThat(result.exitCode()).isZero();
    }

    @Test
    @DisplayName("Glob-маска: в отчёт попадают все подошедшие файлы")
    void readsAllFilesMatchingGlob() throws IOException {
        Files.copy(SAMPLES.resolve(DAY_ONE), tempDir.resolve(DAY_ONE));
        Files.copy(SAMPLES.resolve(DAY_TWO), tempDir.resolve(DAY_TWO));

        var matched = run("-p", "access-*.log");
        var notMatched = run("-p", "nothing-*.log");

        assertThat(matched.exitCode()).as("%s", matched).isZero();
        assertThat(notMatched.exitCode()).as("%s", notMatched).isEqualTo(2);
    }

    @Test
    @DisplayName("Лог читается по URL")
    void readsRemoteFile() {
        var result = run("-p", url("/access.log"));

        assertThat(result.exitCode()).as("%s", result).isZero();
    }

    @Test
    @DisplayName("URL вернул 404 -> exit code 2")
    void remote404ExitsWithTwo() {
        var result = run("-p", url("/missing.log"));

        assertThat(result.exitCode()).as("%s", result).isEqualTo(2);
        assertThat(result.stderr()).isNotBlank();
    }

    private CliRunner.Result run(String... args) {
        return CliRunner.run(tempDir, null, args);
    }

    private String url(String path) {
        return "http://127.0.0.1:" + server.getAddress().getPort() + path;
    }
}
