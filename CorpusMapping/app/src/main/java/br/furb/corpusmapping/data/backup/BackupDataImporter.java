package br.furb.corpusmapping.data.backup;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import br.furb.corpusmapping.data.CorpusMappingSQLHelper;
import br.furb.corpusmapping.data.ImageRecord;
import br.furb.corpusmapping.data.ImageRecordRepository;
import br.furb.corpusmapping.data.MoleGroup;
import br.furb.corpusmapping.data.MoleGroupRepository;
import br.furb.corpusmapping.data.Patient;
import br.furb.corpusmapping.data.PatientRepository;

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

    @Override
    protected void importData(InputStream inputStream) throws Exception {
        //   final JsonObject json = inputStreamToJson(inputStream);
        //   final int version = validate(json);

        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            database.beginTransaction();

            if (!merge) {
                cleanDatabase(database);
            }

            String json = null;
            try {
                ZipInputStream zin = new ZipInputStream(inputStream);
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {
                    //create dir if required while unzipping
                    if (ze.isDirectory()) {
                        // TODO pasta de imagens
                    } else {
                        // Arquivo json com os dados

                        StringWriter writer = new StringWriter();

                        for (int c = zin.read(); c != -1; c = zin.read()) {
                            writer.write(c);
                        }
                        zin.closeEntry();
                        json = writer.toString();

                    }
                }
                zin.close();
            } catch (Exception e) {
                System.out.println(e);
            }

            if (json != null) {
                GsonBuilder builder = new GsonBuilder()
                        .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
                Gson gson = builder.create();

                JsonParser jsonParser = new JsonParser();
                JsonObject root = jsonParser.parse(json).getAsJsonObject();

                Map<Long, Long> moleGroupIds = new HashMap<>();
                Map<Long, Long> patientIds = new HashMap<>();

                importPatients(database, gson, root, patientIds);
                importMoleGroups(database, gson, root, moleGroupIds, patientIds);
                importImageRecords(database, gson, root, moleGroupIds, patientIds);
            }

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
            database.close();
        }
    }

    private void importPatients(SQLiteDatabase database, Gson gson, JsonObject root, Map<Long, Long> patientIds) {
        JsonArray jsonArray = root.get("patients").getAsJsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            Patient patient = gson.fromJson(jsonArray.get(i).getAsJsonObject(), Patient.class);
            long oldId = patient.getId();
            patient.setId(-1);
            PatientRepository.getInstance(context).save(patient, database);
            patientIds.put(oldId, patient.getId());
        }
    }

    private void importMoleGroups(SQLiteDatabase database, Gson gson, JsonObject root, Map<Long, Long> moleGroupIds, Map<Long, Long> patientIds) {
        JsonArray jsonArray = root.get("moleGroups").getAsJsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            MoleGroup moleGroup = gson.fromJson(jsonArray.get(i).getAsJsonObject(), MoleGroup.class);
            long id = moleGroup.getId();
            moleGroup.setId(-1);

            if (patientIds.containsKey(moleGroup.getPatientId())) {
                moleGroup.setPatientId(patientIds.get(moleGroup.getPatientId()));
            }

            MoleGroupRepository.getInstance(context).save(moleGroup, database);
            moleGroupIds.put(id, moleGroup.getId());
        }
    }

    private void importImageRecords(SQLiteDatabase database, Gson gson, JsonObject root, Map<Long, Long> moleGroupIds, Map<Long, Long> patientIds) {
        JsonArray jsonArray = root.get("imageRecords").getAsJsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            ImageRecord imageRecord = gson.fromJson(jsonArray.get(i).getAsJsonObject(), ImageRecord.class);
            imageRecord.setId(-1);
            if (imageRecord.getMoleGroup() != null) {
                long oldId = imageRecord.getMoleGroup().getId();
                Long newId = moleGroupIds.get(oldId);
                imageRecord.setMoleGroupId(newId);
                imageRecord.setMoleGroup(null);
            }

            if (patientIds.containsKey(imageRecord.getPatientId())) {
                imageRecord.setPatientId(patientIds.get(imageRecord.getPatientId()));
            }

            ImageRecordRepository.getInstance(context).save(imageRecord, database);
        }
    }


    private void cleanDatabase(SQLiteDatabase db) {
        db.delete(ImageRecordRepository.TABLE_IMAGE_RECORD, null, null);
        db.delete(MoleGroupRepository.TABLE_MOLE_GROUP, null, null);
        db.delete(PatientRepository.TABLE_PATIENT, null, null);
    }

}
