package br.furb.corpusmapping.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Janaina on 16/08/2015.
 */
public class PatientRepository {

    public static final String TABLE_PATIENT = "patient";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CPF = "cpf";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_BIRTH_DATE = "birth_date";
    private static PatientRepository instance;
    private final CorpusMappingSQLHelper helper;

    private PatientRepository(Context context) {
        helper = CorpusMappingSQLHelper.getInstance(context);
    }

    public static synchronized PatientRepository getInstance(Context context) {

        if (instance == null) {
            instance = new PatientRepository(context);
        }
        return instance;
    }

    public void save(Patient patient, SQLiteDatabase db) {
        if (patient.getId() <= 0) {
            insert(patient, db);
        } else {
            update(patient, db);
        }
    }


    public void save(Patient patient) {
        save(patient, null);
    }

    public int delete(Patient patient) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int affectedRows = db.delete(
                TABLE_PATIENT,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(patient.getId())});
        db.close();
        return affectedRows;
    }

    public void deleteAll() {
        SQLiteDatabase db = helper.getWritableDatabase();
        int affectedRows = db.delete(TABLE_PATIENT, null, null);
        db.close();
    }

    public List<Patient> getAll() {
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_PATIENT;
        sql += " ORDER BY " + COLUMN_NAME;

        Cursor cursor = db.rawQuery(sql, null);
        List<Patient> patients = new ArrayList<Patient>();
        while (cursor.moveToNext()) {
            Patient patient = createPatient(cursor);
            patients.add(patient);
        }
        cursor.close();
        db.close();

        return patients;
    }

    public Patient getById(long id) {
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_PATIENT + " WHERE _id = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});
        List<Patient> patients = new ArrayList<Patient>();

        Patient patient = null;

        if (cursor.moveToFirst()) {
            patient = createPatient(cursor);
        }
        cursor.close();
        db.close();

        return patient;
    }

    private Patient createPatient(Cursor cursor) {
        long id = cursor.getLong(
                cursor.getColumnIndex(
                        COLUMN_ID));
        String name = cursor.getString(
                cursor.getColumnIndex(
                        COLUMN_NAME));
        String cpf = cursor.getString(
                cursor.getColumnIndex(
                        COLUMN_CPF));
        String gender = cursor.getString(
                cursor.getColumnIndex(
                        COLUMN_GENDER));
        String birthDate = cursor.getString(
                cursor.getColumnIndex(
                        COLUMN_BIRTH_DATE));

        Patient p = new Patient(name, cpf, Gender.valueOf(gender), birthDate);
        p.setId(id);
        return p;
    }

    private long insert(Patient patient, SQLiteDatabase db) {
        boolean needClose = false;

        if (db == null) {
            needClose = true;
            db = helper.getWritableDatabase();
        }
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, patient.getName());
        cv.put(COLUMN_CPF, patient.getCpf());
        cv.put(COLUMN_GENDER, patient.getGender().name());
        cv.put(COLUMN_BIRTH_DATE, patient.getBirthDateStr());

        long id = db.insert(TABLE_PATIENT, null, cv);
        if (id != -1) {
            patient.setId(id);
        }
        if (needClose) db.close();
        return id;
    }

    private int update(Patient patient, SQLiteDatabase db) {
        boolean needClose = false;

        if (db == null) {
            needClose = true;
            db = helper.getWritableDatabase();
        }
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, patient.getName());
        cv.put(COLUMN_CPF, patient.getCpf());
        cv.put(COLUMN_GENDER, patient.getGender().name());
        cv.put(COLUMN_BIRTH_DATE, patient.getBirthDateStr());

        int affectedRows = db.update(TABLE_PATIENT, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(patient
                .getId())});
        if (needClose) db.close();
        return affectedRows;
    }


}
