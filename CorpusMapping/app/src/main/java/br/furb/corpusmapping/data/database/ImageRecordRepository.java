package br.furb.corpusmapping.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.furb.corpusmapping.data.model.ImageType;
import br.furb.corpusmapping.data.model.SpecificBodyPart;
import br.furb.corpusmapping.data.model.ImageRecord;
import br.furb.corpusmapping.data.model.PointF;

/**
 * Created by Janaina on 16/08/2015.
 */
public class ImageRecordRepository {

    public static final String TABLE_IMAGE_RECORD = "IMAGE_RECORD";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_BODY_PART = "body_part";
    public static final String COLUMN_IMAGE_DATE = "image_date";
    public static final String COLUMN_POINT_X = "point_x";
    public static final String COLUMN_POINT_Y = "point_y";
    public static final String COLUMN_PATIENT = "patient";
    public static final String COLUMN_ANNOTATIONS = "annotations";
    public static final String COLUMN_MOLE_GROUP = "mole_group";
    public static final String COLUMN_IMAGE_TYPE = "image_type";
    public static final String COLUMN_IMAGE_PATH = "image_path";
    public static final String COLUMN_LAST_UPDATE = "last_update";
    private final CorpusMappingSQLHelper helper;
    public static final int CLICK_MARGIN = 25;
    private MoleGroupRepository moleGroupRepository;

    private static ImageRecordRepository instance;

    private ImageRecordRepository(Context context) {
        helper = CorpusMappingSQLHelper.getInstance(context);
        moleGroupRepository = MoleGroupRepository.getInstance(context);
    }

    public static synchronized ImageRecordRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ImageRecordRepository(context);
        }
        return instance;
    }

    public void save(ImageRecord imageRecord, SQLiteDatabase db) {
        if (imageRecord.getId() <= 0) {
            insert(imageRecord, db);
        } else {
            update(imageRecord, db);
        }
    }

    public void save(ImageRecord imageRecord) {
        if (imageRecord.getId() <= 0) {
            insert(imageRecord, null);
        } else {
            update(imageRecord, null);
        }
    }

    public void deleteAll() {
        SQLiteDatabase db = helper.getWritableDatabase();
        int affectedRows = db.delete(TABLE_IMAGE_RECORD, null, null);
        db.close();
    }

    public int delete(ImageRecord imageRecord) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int affectedRows = db.delete(
                TABLE_IMAGE_RECORD,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(imageRecord.getId())});
        db.close();
        return affectedRows;
    }

    public int deleteByPatient(long patientId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int affectedRows = db.delete(
                TABLE_IMAGE_RECORD,
                COLUMN_PATIENT + " = ?",
                new String[]{String.valueOf(patientId)});
        db.close();
        return affectedRows;
    }

    public List<ImageRecord> getAll() {
        SQLiteDatabase db = helper.getReadableDatabase();

        try {

            String sql = "SELECT * FROM " + TABLE_IMAGE_RECORD;
            sql += " ORDER BY " + COLUMN_BODY_PART;

            Cursor cursor = db.rawQuery(sql, null);
            List<ImageRecord> imageRecords = new ArrayList<ImageRecord>();
            while (cursor.moveToNext()) {
                ImageRecord imageRecord = createImageRecord(cursor, db);
                imageRecords.add(imageRecord);
            }
            cursor.close();

            return imageRecords;
        } finally {
            db.close();
        }
    }

    public ImageRecord getById(long id) {
        SQLiteDatabase db = helper.getReadableDatabase();

        try {
            ImageRecord imageRecord = getById(id, db);
            return imageRecord;
        } finally {
            db.close();
        }

    }

    public ImageRecord getById(long id, SQLiteDatabase db) {

        String sql = "SELECT * FROM " + TABLE_IMAGE_RECORD + " WHERE id = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});

        ImageRecord imageRecord = null;

        if (cursor.moveToFirst()) {
            imageRecord = createImageRecord(cursor, db);
        }
        cursor.close();
        return imageRecord;

    }

    public List<ImageRecord> getByBodyPartAndPosition(long patientId, SpecificBodyPart bodyPart, PointF position) {
        SQLiteDatabase db = helper.getReadableDatabase();

        try {
            String sql = "SELECT * FROM " + TABLE_IMAGE_RECORD + " WHERE patient = ? and body_part = ? and point_x <= ? and point_x >= ? and point_y <= ? and point_y >= ?";

            Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(patientId), bodyPart.name(),//
                    String.valueOf(position.x + CLICK_MARGIN), String.valueOf(position.x - CLICK_MARGIN), String.valueOf(position.y + CLICK_MARGIN), String.valueOf(position.y - CLICK_MARGIN)});

            List<ImageRecord> imageRecords = new ArrayList<ImageRecord>();
            while (cursor.moveToNext()) {
                ImageRecord imageRecord = createImageRecord(cursor, db);
                imageRecords.add(imageRecord);
            }
            cursor.close();

            Collections.sort(imageRecords, new Comparator<ImageRecord>() {
                @Override
                public int compare(ImageRecord lhs, ImageRecord rhs) {
                    return rhs.getImageDate().compareTo(lhs.getImageDate());
                }
            });

            return imageRecords;
        } finally {
            db.close();
        }

    }

    public Map<Long, List<ImageRecord>> getLastMolesByPatientId(long patient_id) {

        SQLiteDatabase db = helper.getReadableDatabase();

        try {

            String sql = "SELECT * FROM " + TABLE_IMAGE_RECORD + " WHERE patient = ? order by " + COLUMN_BODY_PART;

            Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(patient_id)});


            Map<Long, List<ImageRecord>> map = new HashMap<>();

            while (cursor.moveToNext()) {
                ImageRecord imageRecord = createImageRecord(cursor, db);

                if (!map.containsKey(imageRecord.getMoleGroupId())) {
                    map.put(imageRecord.getMoleGroupId(), new ArrayList<ImageRecord>());
                }

                map.get(imageRecord.getMoleGroupId()).add(imageRecord);
            }
            cursor.close();

            for (List<ImageRecord> records : map.values()) {

                Collections.sort(records, new Comparator<ImageRecord>() {
                    @Override
                    public int compare(ImageRecord lhs, ImageRecord rhs) {
                        return rhs.getImageDate().compareTo(lhs.getImageDate());
                    }
                });

            }

            return map;

        } finally {
            db.close();
        }
    }

    public List<ImageRecord> getLastMolesByGroupId(long group_id) {

        SQLiteDatabase db = helper.getReadableDatabase();

        try {

            String sql = "SELECT * FROM " + TABLE_IMAGE_RECORD + " WHERE mole_group = ?";

            Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(group_id)});


            List<ImageRecord> list = new ArrayList<>();

            while (cursor.moveToNext()) {
                ImageRecord imageRecord = createImageRecord(cursor, db);
                list.add(imageRecord);
            }
            cursor.close();


            Collections.sort(list, new Comparator<ImageRecord>() {
                @Override
                public int compare(ImageRecord lhs, ImageRecord rhs) {
                    return rhs.getImageDate().compareTo(lhs.getImageDate());
                }
            });

            return list;

        } finally {
            db.close();
        }
    }

    public List<ImageRecord> getByPatientId(long patientId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        try {

            String sql = "SELECT * FROM " + TABLE_IMAGE_RECORD + " WHERE " + COLUMN_PATIENT+" = ?";

            Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(patientId)});


            List<ImageRecord> list = new ArrayList<>();

            while (cursor.moveToNext()) {
                ImageRecord imageRecord = createImageRecord(cursor, db);
                list.add(imageRecord);
            }
            cursor.close();
            return list;

        } finally {
            db.close();
        }
    }

    public List<ImageRecord> getByBodyPartId(long patient_id, SpecificBodyPart bodyPart) {

        SQLiteDatabase db = helper.getReadableDatabase();

        try {

            String sql = "SELECT * FROM " + TABLE_IMAGE_RECORD + " WHERE patient = ? and body_part = ?";

            Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(patient_id), bodyPart.name()});

            List<ImageRecord> imageRecords = new ArrayList<ImageRecord>();

            while (cursor.moveToNext()) {
                ImageRecord imageRecord = createImageRecord(cursor, db);
                imageRecords.add(imageRecord);
            }
            cursor.close();

            return imageRecords;

        } finally {
            db.close();
        }
    }

    private ImageRecord createImageRecord(Cursor cursor, SQLiteDatabase db) {
        long id = cursor.getLong(
                cursor.getColumnIndex(
                        COLUMN_ID));

        ImageRecord imageRecord = new ImageRecord();
        imageRecord.setId(id);
        imageRecord.setPatientId(cursor.getLong(cursor.getColumnIndex(COLUMN_PATIENT)));
        imageRecord.setAnnotations(cursor.getString(cursor.getColumnIndex(COLUMN_ANNOTATIONS)));
        imageRecord.setBodyPart(SpecificBodyPart.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_BODY_PART))));
        imageRecord.setImageDate(LocalDateTime.parse(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_DATE))));
        imageRecord.setImagePath(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_PATH)));
        imageRecord.setImageType(ImageType.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_TYPE))));
        float x = cursor.getFloat(cursor.getColumnIndex(COLUMN_POINT_X));
        float y = cursor.getFloat(cursor.getColumnIndex(COLUMN_POINT_Y));
        imageRecord.setPosition(new PointF(x, y));

        long moleGroupId = cursor.getLong(cursor.getColumnIndex(COLUMN_MOLE_GROUP));

        imageRecord.setMoleGroup(moleGroupRepository.getById(moleGroupId, db));
        imageRecord.setMoleGroupId(moleGroupId);

        String value = cursor.getString(cursor.getColumnIndex(COLUMN_LAST_UPDATE));
        if(value!=null){
            imageRecord.setLastUpdate(LocalDateTime.parse(value));
        }

        return imageRecord;
    }

    private long insert(ImageRecord imageRecord, SQLiteDatabase db) {
        if (imageRecord.getMoleGroup() != null) {
            moleGroupRepository.save(imageRecord.getMoleGroup(), db);
            imageRecord.setMoleGroupId(imageRecord.getMoleGroup().getId());
        }
        boolean needClose = false;
        if (db == null) {
            db = helper.getWritableDatabase();
            needClose = true;
        }

        try {
            String query = "select max(id) from " + TABLE_IMAGE_RECORD;
            Cursor cursor = db.rawQuery(query, null);

            long id = System.currentTimeMillis();
            return insert(imageRecord, db, id);
        } finally {
            if (needClose) {
                db.close();
            }
        }
    }

    public long insert(ImageRecord imageRecord, SQLiteDatabase db, long id) {
        if (imageRecord.getMoleGroup() != null) {
            moleGroupRepository.save(imageRecord.getMoleGroup(), db);
            imageRecord.setMoleGroupId(imageRecord.getMoleGroup().getId());
        }
        boolean needClose = false;
        if (db == null) {
            db = helper.getWritableDatabase();
            needClose = true;
        }

        try {
            imageRecord.setLastUpdate(LocalDateTime.now());
            ContentValues cv = buildContentValues(imageRecord);
            cv.put(COLUMN_ID, id);
            imageRecord.setId(id);
            db.insert(TABLE_IMAGE_RECORD, null, cv);
            return id;
        } finally {
            if (needClose) {
                db.close();
            }
        }
    }

    private int update(ImageRecord imageRecord, SQLiteDatabase db) {
        if (imageRecord.getMoleGroup() != null) {
            moleGroupRepository.save(imageRecord.getMoleGroup(), db);
            imageRecord.setMoleGroupId(imageRecord.getMoleGroup().getId());
        }

        boolean needClose = false;
        if (db == null) {
            db = helper.getWritableDatabase();
            needClose = true;
        }
        try {
            imageRecord.setLastUpdate(LocalDateTime.now());
            ContentValues cv = buildContentValues(imageRecord);

            int affectedRows = db.update(TABLE_IMAGE_RECORD, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(imageRecord
                    .getId())});
            return affectedRows;
        } finally {
            if (needClose) {
                db.close();
            }
        }
    }

    private ContentValues buildContentValues(ImageRecord imageRecord) {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_BODY_PART, imageRecord.getBodyPart().name());
        cv.put(COLUMN_POINT_X, imageRecord.getPosition().x);
        cv.put(COLUMN_POINT_Y, imageRecord.getPosition().y);
        cv.put(COLUMN_ANNOTATIONS, imageRecord.getAnnotations());
        cv.put(COLUMN_IMAGE_DATE, imageRecord.getImageDate().toString());
        cv.put(COLUMN_IMAGE_PATH, imageRecord.getImagePath());
        cv.put(COLUMN_IMAGE_TYPE, imageRecord.getImageType().name());
        cv.put(COLUMN_MOLE_GROUP, imageRecord.getMoleGroupId());
        cv.put(COLUMN_PATIENT, imageRecord.getPatientId());
        cv.put(COLUMN_LAST_UPDATE, imageRecord.getLastUpdate().toString());
        return cv;
    }


}
