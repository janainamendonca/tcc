package br.furb.corpusmapping.util.errors;

public class ImportError extends AppError {
    public ImportError(String detailMessage) {
        super(detailMessage);
    }

    public ImportError(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
