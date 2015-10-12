package br.furb.corpusmapping.data.backup;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.InputStream;

import br.furb.corpusmapping.data.CorpusMappingSQLHelper;

public class BackupDataImporter extends DataImporter {
    private static final int MIN_VALID_VERSION = 7;

    private final Context context;
    private final CorpusMappingSQLHelper dbHelper;
    private final boolean merge;

    public BackupDataImporter(InputStream inputStream, Context context, boolean merge) {
        super(inputStream);
        this.context = context.getApplicationContext();
        this.dbHelper = CorpusMappingSQLHelper.getInstance(context);
        this.merge = merge;
    }

    @Override protected void importData(InputStream inputStream) throws Exception {
     //   final JsonObject json = inputStreamToJson(inputStream);
     //   final int version = validate(json);

        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            database.beginTransaction();

            if (!merge) {
              //  cleanDatabase(database);
            }





            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

}
