package br.furb.corpusmapping.data.backup;

import android.content.Context;

import java.io.OutputStream;

public class BackupDataExporter extends DataExporter {
    public static final int VERSION = 9;

    private static final String CHARSET_NAME = "UTF-8";

    private final Context context;

    public BackupDataExporter(OutputStream outputStream, Context context) {
        super(outputStream);
        this.context = context;
    }

    @Override public void exportData(OutputStream outputStream) throws Exception {

    }

}
