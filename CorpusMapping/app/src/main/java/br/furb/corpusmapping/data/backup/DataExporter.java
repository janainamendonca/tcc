package br.furb.corpusmapping.data.backup;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

import br.furb.corpusmapping.util.Preconditions;

public abstract class DataExporter implements Closeable {
    private final OutputStream outputStream;

    protected DataExporter(OutputStream outputStream) {
        this.outputStream = Preconditions.notNull(outputStream, "OutputStream cannot be null.");
    }

    @Override public void close() throws IOException {
        outputStream.close();
    }

    public void exportData() throws Exception {
        exportData(outputStream);
    }

    public abstract void exportData(OutputStream outputStream) throws Exception;
}
