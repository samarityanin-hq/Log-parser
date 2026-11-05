package academy.incidentradar.validation;

public enum ReportFormat {
    JSON("json"),
    MARKDOWN("md");

    private final String extension;

    ReportFormat(String ext) {
        this.extension = ext;
    }

    public String getExtension() {
        return extension;
    }

    public static ReportFormat parse(String format) {
        format = format.trim().toLowerCase();
        int dotIndex = format.lastIndexOf(".");
        String ext = (dotIndex == -1) ? format : format.substring(dotIndex + 1);
        return switch (ext) {
            case "json" -> JSON;
            case "md" -> MARKDOWN;
            default -> throw new UsageException("Неизвестный формат файла: " + ext);
        };
    }
}
