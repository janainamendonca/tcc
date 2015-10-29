package br.furb.corpusmapping.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Janaina on 16/08/2015.
 */
public class CorpusMappingSQLHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "dbCorpusMapping";
    private static final int DB_VERSION = 6;

    private static CorpusMappingSQLHelper instance;

    private CorpusMappingSQLHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static synchronized CorpusMappingSQLHelper getInstance(Context context) {
        if (instance == null) {
            instance = new CorpusMappingSQLHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTablePatient(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (newVersion == 2) {
            createTableMoleGroup(db);
            createTableImageRegister(db);
        } else if (newVersion == 3) {
            dropTables(db);
            createTablePatient(db);
            createTableMoleGroup(db);
            createTableImageRegister(db);
        } else if (newVersion == 4) {
            StringBuilder sql = new StringBuilder();

            sql.append("ALTER TABLE MOLE_GROUP");
            sql.append(" ADD COLUMN classification TEXT;");

            db.execSQL(sql.toString());
        }else if(newVersion == 5){
            createTableIds(db);
        }else if(newVersion == 6){
            dropTables(db);

            createTablePatient(db);
            createTableMoleGroup(db);
            createTableImageRegister(db);
        }
    }

    private void createTableIds(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder();

        sql.append("CREATE TABLE id_mapping ( ");
        sql.append(" old_id INTEGER, ");
        sql.append(" new_id INTEGER);");

        db.execSQL(sql.toString());
    }

    private void dropTables(SQLiteDatabase db) {
        try {
            db.execSQL("DROP TABLE PATIENT");
            db.execSQL("DROP TABLE MOLE_GROUP");
            db.execSQL("DROP TABLE IMAGE_RECORD");
        } catch (Exception e) {
        }
    }

    private void createTablePatient(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder();

        sql.append("CREATE TABLE PATIENT ( ");
        sql.append(" id INTEGER PRIMARY KEY, ");
        sql.append(" name TEXT, ");
        sql.append(" cpf TEXT, ");
        sql.append(" gender TEXT, ");
        sql.append(" birth_date TEXT);");

        db.execSQL(sql.toString());
    }

    private void createTableMoleGroup(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder();

        sql.append("CREATE TABLE MOLE_GROUP ( ");
        sql.append(" id INTEGER PRIMARY KEY, ");
        sql.append(" group_name TEXT, ");
        sql.append(" description TEXT, ");
        sql.append(" patient LONG, ");
        sql.append(" point_x REAL, ");
        sql.append(" point_y REAL, ");
        sql.append(" classification TEXT, ");
        sql.append(" annotations TEXT); ");

        db.execSQL(sql.toString());
    }


    private void createTableImageRegister(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder();

        sql.append("CREATE TABLE IMAGE_RECORD ( ");
        sql.append(" id INTEGER PRIMARY KEY, ");
        sql.append(" body_part TEXT, ");
        sql.append(" image_date TEXT, ");
        sql.append(" point_x REAL, ");
        sql.append(" point_y REAL, ");
        sql.append(" patient LONG, ");
        sql.append(" annotations TEXT, ");
        sql.append(" mole_group LONG, ");
        sql.append(" image_type TEXT, ");
        sql.append(" image_path TEXT);");

        db.execSQL(sql.toString());
    }

}
