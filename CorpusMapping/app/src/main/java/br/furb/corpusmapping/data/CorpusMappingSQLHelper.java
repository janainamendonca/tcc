package br.furb.corpusmapping.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Janaina on 16/08/2015.
 */
public class CorpusMappingSQLHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "dbCorpusMapping";
    private static final int DB_VERSION = 1;

    public CorpusMappingSQLHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder();

        sql.append("CREATE TABLE PATIENT ( ");
        sql.append(" _id INTEGER PRIMARY KEY, ");
        sql.append(" name TEXT, ");
        sql.append(" cpf TEXT, ");
        sql.append(" gender TEXT, ");
        sql.append(" birth_date TEXT);");

        db.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
