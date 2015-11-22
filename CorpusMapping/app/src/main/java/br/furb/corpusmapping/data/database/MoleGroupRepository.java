package br.furb.corpusmapping.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import br.furb.corpusmapping.data.model.MoleClassification;
import br.furb.corpusmapping.data.model.MoleGroup;
import br.furb.corpusmapping.data.model.PointF;

/**
 * Created by Janaina on 16/08/2015.
 */
public class MoleGroupRepository {

    private static MoleGroupRepository instance;

    public static final String TABLE_MOLE_GROUP = "MOLE_GROUP";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "group_name";
    public static final String COLUMN_ANNOTATIONS = "annotations";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CLASSIFICATION = "classification";
    public static final String COLUMN_POINT_X = "point_x";
    public static final String COLUMN_POINT_Y = "point_y";
    public static final String COLUMN_PATIENT_ID = "patient";
    public static final String COLUMN_LAST_UPDATE = "last_update";
    private final CorpusMappingSQLHelper helper;

    public static final int CLICK_MARGIN = 25;

    private MoleGroupRepository(Context context) {
        helper = CorpusMappingSQLHelper.getInstance(context);
    }

    public static synchronized MoleGroupRepository getInstance(Context context) {
        if (instance == null) {
            instance = new MoleGroupRepository(context);
        }

        return instance;
    }


    public void save(MoleGroup moleGroup, SQLiteDatabase db) {
        if (moleGroup.getId() <= 0) {
            insert(moleGroup, db);
        } else {
            update(moleGroup, db);
        }
    }

    public void save(MoleGroup moleGroup) {
        save(moleGroup, null);
    }

    public void deleteAll() {
        SQLiteDatabase db = helper.getWritableDatabase();
        int affectedRows = db.delete(TABLE_MOLE_GROUP, null, null);
        db.close();
    }

    public int deleteByPatient(long patientId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int affectedRows = db.delete(
                TABLE_MOLE_GROUP,
                COLUMN_PATIENT_ID + " = ?",
                new String[]{String.valueOf(patientId)});
        db.close();
        return affectedRows;
    }

    public int delete(MoleGroup moleGroup) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            int affectedRows = db.delete(
                    TABLE_MOLE_GROUP,
                    COLUMN_ID + " = ?",
                    new String[]{String.valueOf(moleGroup.getId())});
            return affectedRows;
        } finally {
            db.close();
        }
    }

    public List<MoleGroup> getAll() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {

            String sql = "SELECT * FROM " + TABLE_MOLE_GROUP;
            sql += " ORDER BY " + COLUMN_NAME;

            cursor = db.rawQuery(sql, null);
            List<MoleGroup> moleGroups = new ArrayList<MoleGroup>();
            while (cursor.moveToNext()) {
                MoleGroup moleGroup = createMoleGroup(cursor);
                moleGroups.add(moleGroup);
            }
            cursor.close();

            return moleGroups;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    public List<MoleGroup> getByPatient(long patientId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {

            String sql = "SELECT * FROM " + TABLE_MOLE_GROUP + " WHERE " + COLUMN_PATIENT_ID + " = ?";
            sql += " ORDER BY " + COLUMN_NAME;

            cursor = db.rawQuery(sql, new String[]{String.valueOf(patientId)});
            List<MoleGroup> moleGroups = new ArrayList<MoleGroup>();
            while (cursor.moveToNext()) {
                MoleGroup moleGroup = createMoleGroup(cursor);
                moleGroups.add(moleGroup);
            }
            cursor.close();

            return moleGroups;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    public MoleGroup getById(long id) {
        SQLiteDatabase db = helper.getReadableDatabase();

        try {
            MoleGroup moleGroup = getById(id, db);
            return moleGroup;
        } finally {
            db.close();
        }
    }


    public MoleGroup getById(long id, SQLiteDatabase db) {
        String sql = "SELECT * FROM " + TABLE_MOLE_GROUP + " WHERE id = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});
        List<MoleGroup> moleGroups = new ArrayList<MoleGroup>();

        MoleGroup moleGroup = null;

        if (cursor.moveToFirst()) {
            moleGroup = createMoleGroup(cursor);
        }
        cursor.close();

        return moleGroup;
    }

    public MoleGroup getByPosition(long patient_id, PointF position) {
        SQLiteDatabase db = helper.getReadableDatabase();

        try {
            String sql = "SELECT * FROM " + TABLE_MOLE_GROUP + " WHERE patient = ? and point_x <= ? and point_x >= ? and point_y <= ? and point_y >= ?";

            Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(patient_id), String.valueOf(position.x + CLICK_MARGIN), String.valueOf(position.x - CLICK_MARGIN), String.valueOf(position.y + CLICK_MARGIN), String.valueOf(position.y - CLICK_MARGIN)});
            List<MoleGroup> moleGroups = new ArrayList<MoleGroup>();

            MoleGroup moleGroup = null;

            if (cursor.moveToFirst()) {
                moleGroup = createMoleGroup(cursor);
            }
            cursor.close();

            return moleGroup;
        } finally {
            db.close();
        }
    }

    private MoleGroup createMoleGroup(Cursor cursor) {
        long id = cursor.getLong(
                cursor.getColumnIndex(
                        COLUMN_ID));
        String name = cursor.getString(
                cursor.getColumnIndex(
                        COLUMN_NAME));
        String annotations = cursor.getString(
                cursor.getColumnIndex(
                        COLUMN_ANNOTATIONS));
        String description = cursor.getString(
                cursor.getColumnIndex(
                        COLUMN_DESCRIPTION));
        String classification = cursor.getString(
                cursor.getColumnIndex(
                        COLUMN_CLASSIFICATION));

        MoleGroup moleGroup = new MoleGroup(name, annotations, description);
        float x = cursor.getFloat(cursor.getColumnIndex(COLUMN_POINT_X));
        float y = cursor.getFloat(cursor.getColumnIndex(COLUMN_POINT_Y));
        moleGroup.setPosition(new PointF(x, y));
        moleGroup.setPatientId(cursor.getLong(cursor.getColumnIndex(COLUMN_PATIENT_ID)));
        moleGroup.setId(id);
        String value = cursor.getString(cursor.getColumnIndex(COLUMN_LAST_UPDATE));
        if (value != null) {
            moleGroup.setLastUpdate(LocalDateTime.parse(value));
        }

        if (classification != null) {
            moleGroup.setClassification(MoleClassification.valueOf(classification));
        }

        return moleGroup;
    }

    private long insert(MoleGroup moleGroup, SQLiteDatabase db) {

        boolean needClose = false;

        if (db == null) {
            needClose = true;
            db = helper.getWritableDatabase();
        }

        try {
            long id = System.currentTimeMillis();
            return insert(moleGroup, db, id);
        } finally {
            if (needClose) db.close();
        }

    }

    public long insert(MoleGroup moleGroup, SQLiteDatabase db, long id) {

        boolean needClose = false;

        if (db == null) {
            needClose = true;
            db = helper.getWritableDatabase();
        }

        try {
            moleGroup.setLastUpdate(LocalDateTime.now());
            ContentValues cv = buildContentValues(moleGroup);
            cv.put(COLUMN_ID, id);
            moleGroup.setId(id);
            db.insert(TABLE_MOLE_GROUP, null, cv);
            return id;
        } finally {
            if (needClose) db.close();
        }
    }

    private int update(MoleGroup moleGroup, SQLiteDatabase db) {
        boolean needClose = false;

        if (db == null) {
            needClose = true;
            db = helper.getWritableDatabase();
        }
        try {
            moleGroup.setLastUpdate(LocalDateTime.now());
            ContentValues cv = buildContentValues(moleGroup);

            int affectedRows = db.update(TABLE_MOLE_GROUP, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(moleGroup
                    .getId())});
            return affectedRows;

        } finally {
            if (needClose) db.close();
        }
    }

    private ContentValues buildContentValues(MoleGroup moleGroup) {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, moleGroup.getGroupName());
        cv.put(COLUMN_ANNOTATIONS, moleGroup.getAnnotations());
        cv.put(COLUMN_DESCRIPTION, moleGroup.getDescription());
        cv.put(COLUMN_POINT_X, moleGroup.getPosition().x);
        cv.put(COLUMN_POINT_Y, moleGroup.getPosition().y);
        cv.put(COLUMN_PATIENT_ID, moleGroup.getPatientId());
        cv.put(COLUMN_CLASSIFICATION, moleGroup.getClassification() != null ? moleGroup.getClassification().toString() : null);
        cv.put(COLUMN_LAST_UPDATE, moleGroup.getLastUpdate().toString());
        return cv;
    }


}
