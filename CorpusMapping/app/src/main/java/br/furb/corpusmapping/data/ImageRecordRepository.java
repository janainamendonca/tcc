package br.furb.corpusmapping.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PointF;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.furb.corpusmapping.ImageType;
import br.furb.corpusmapping.SpecificBodyPart;

/**
 * Created by Janaina on 16/08/2015.
 */
public class ImageRecordRepository {

    public static final String TABLE_IMAGE_RECORD = "IMAGE_RECORD";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_BODY_PART = "body_part";
    public static final String COLUMN_IMAGE_DATE = "image_date";
    public static final String COLUMN_POINT_X = "point_x";
    public static final String COLUMN_POINT_Y = "point_y";
    public static final String COLUMN_PATIENT = "patient";
    public static final String COLUMN_ANNOTATIONS = "annotations";
    public static final String COLUMN_MOLE_GROUP = "mole_group";
    public static final String COLUMN_IMAGE_TYPE = "image_type";
    public static final String COLUMN_IMAGE_PATH = "image_path";
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

    public void save(ImageRecord imageRecord) {
        if (imageRecord.getId() <= 0) {
            insert(imageRecord);
        } else {
            update(imageRecord);
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

    public List<ImageRecord> getAll() {
        SQLiteDatabase db = helper.getReadableDatabase();

        try {

            String sql = "SELECT * FROM " + TABLE_IMAGE_RECORD;
            sql += " ORDER BY " + COLUMN_BODY_PART;

            Cursor cursor = db.rawQuery(sql, null);
            List<ImageRecord> imageRecords = new ArrayList<ImageRecord>();
            while (cursor.moveToNext()) {
                ImageRecord imageRecord = createImageRecord(cursor);
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
            String sql = "SELECT * FROM " + TABLE_IMAGE_RECORD + " WHERE _id = ?";

            Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});

            ImageRecord imageRecord = null;

            if (cursor.moveToFirst()) {
                imageRecord = createImageRecord(cursor);
            }
            cursor.close();
            return imageRecord;
        } finally {
            db.close();
        }

    }

    public ImageRecord getByBodyPartAndPosition(long patientId, SpecificBodyPart bodyPart, PointF position) {
        SQLiteDatabase db = helper.getReadableDatabase();

        try {
            String sql = "SELECT * FROM " + TABLE_IMAGE_RECORD + " WHERE patient = ? and body_part = ? and point_x <= ? and point_x >= ? and point_y <= ? and point_y >= ?";

            Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(patientId), bodyPart.name(),//
                    String.valueOf(position.x + CLICK_MARGIN), String.valueOf(position.x - CLICK_MARGIN), String.valueOf(position.y + CLICK_MARGIN), String.valueOf(position.y - CLICK_MARGIN)});

            ImageRecord imageRecord = null;

            if (cursor.moveToFirst()) {
                imageRecord = createImageRecord(cursor);
            }
            cursor.close();
            return imageRecord;
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
                ImageRecord imageRecord = createImageRecord(cursor);
                imageRecords.add(imageRecord);
            }
            cursor.close();

            return imageRecords;

        } finally {
            db.close();
        }
    }

    private ImageRecord createImageRecord(Cursor cursor) {
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

        imageRecord.setMoleGroup(moleGroupRepository.getById(moleGroupId));
        imageRecord.setMoleGroupId(moleGroupId);

        return imageRecord;
    }

    private long insert(ImageRecord imageRecord) {
        if (imageRecord.getMoleGroup() != null) {
            moleGroupRepository.save(imageRecord.getMoleGroup());
            imageRecord.setMoleGroupId(imageRecord.getMoleGroup().getId());
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        try {


            ContentValues cv = buildContentValues(imageRecord);

            long id = db.insert(TABLE_IMAGE_RECORD, null, cv);
            if (id != -1) {
                imageRecord.setId(id);
            }
            return id;
        } finally {
            db.close();
        }
    }

    private int update(ImageRecord imageRecord) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues cv = buildContentValues(imageRecord);

            int affectedRows = db.update(TABLE_IMAGE_RECORD, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(imageRecord
                    .getId())});
            return affectedRows;
        } finally {
            db.close();
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
        return cv;
    }


}
