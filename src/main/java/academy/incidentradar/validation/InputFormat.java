package academy.incidentradar.validation;

public enum InputFormat {
    LOG("log"),
    TXT("txt");

    private final String extension;

    InputFormat(String extension) {
        this.extension = extension;
    }

    public static InputFormat parse(String format) {
        format = format.trim().toLowerCase();
        int dotIndex = format.lastIndexOf(".");
        String ext = (dotIndex == -1) ? format : format.substring(dotIndex + 1);

        return switch (ext) {
            case "log" -> LOG;
            case "txt" -> TXT;
            default -> throw new UsageException("Неизвестный формат входного файла: " + ext);
        };
    }

    public String getExtension() {
        return extension;
    }
}
