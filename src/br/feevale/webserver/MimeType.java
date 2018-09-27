package br.feevale.webserver;

public enum MimeType {

    css("text/css"),
    csv("text/csv"),
    doc("application/msword"),
    docx("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    exe("application/x-msdos-program"),
    html("text/html; charset=utf-8"),
    htm("text/html; charset=utf-8"),
    js("text/javascript"),
    jpeg("image/jpeg"),
    jpe("image/jpeg"),
    jpg("image/jpeg"),
    pdf("application/pdf"),
    png("image/png"),
    txt("text/plain");

    private String mimeType;

    MimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public static MimeType getByExtension(String extension) {
        if (extension != null) {
            for (MimeType mime : MimeType.values()) {
                if (mime.name().equalsIgnoreCase(extension)) {
                    return mime;
                }
            }
        }

        return null;
    }

}