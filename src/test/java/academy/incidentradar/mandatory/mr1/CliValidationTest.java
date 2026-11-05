package academy.incidentradar.mandatory.mr1;

import static org.assertj.core.api.Assertions.assertThat;

import academy.incidentradar.support.CliRunner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Обязательные тесты: валидация аргументов командной строки. */
@DisplayName("MR1. Валидация аргументов CLI")
class CliValidationTest {

    @Test
    @DisplayName("Не передан обязательный --path -> exit code 2")
    void missingPathExitsWithTwo() {
        var result = CliRunner.run();

        assertThat(result.exitCode()).isEqualTo(2);
    }

    @ParameterizedTest
    @ValueSource(strings = {"xml", "csv", "txt"})
    @DisplayName("Неподдерживаемый --output-format \"{0}\" -> exit code 2")
    void unsupportedOutputFormatExitsWithTwo(String format) {

        var result = CliRunner.run("-p", "file.log", "-f", format);

        assertThat(result.exitCode()).isEqualTo(2);
        assertThat(result.stderr()).contains("expected one of [JSON, MARKDOWN]");
    }

    @Test
    @DisplayName("--from больше или равен --to -> exit code 2")
    void fromNotBeforeToExitsWithTwo() {
        var result = CliRunner.run("-p", "file.log", "--from", "2025-03-05", "--to", "2025-02-04");

        assertThat(result.exitCode()).isEqualTo(2);
        assertThat(result.stderr()).contains("Границы указаны неверно");
    }

    @Test
    @DisplayName("Несуществующий локальный файл -> exit code 2")
    void missingLocalFileExitsWithTwo() {
        var result = CliRunner.run("-p", "notExistingFile.log");

        assertThat(result.exitCode()).isEqualTo(2);
        assertThat(result.stderr()).contains("Файл не найден");
    }

    @Test
    @DisplayName("Расширение файла отчёта не соответствует формату -> exit code 2")
    void outputExtensionMismatchExitsWithTwo() {
        var result = CliRunner.run("-p", "file.log", "-f", "json", "-o", "report.md");

        assertThat(result.exitCode()).isEqualTo(2);
        assertThat(result.stderr()).contains("Расширение отчета не совпадает с форматом");
    }
}
