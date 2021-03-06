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

import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import br.furb.corpusmapping.data.database.CorpusMappingSQLHelper;
import br.furb.corpusmapping.data.model.ImageRecord;
import br.furb.corpusmapping.data.database.ImageRecordRepository;
import br.furb.corpusmapping.data.model.MoleGroup;
import br.furb.corpusmapping.data.database.MoleGroupRepository;
import br.furb.corpusmapping.data.model.Patient;
import br.furb.corpusmapping.data.database.PatientRepository;
import br.furb.corpusmapping.util.ImageUtils;

public class BackupDataImporter extends DataImporter {
    private static final int MIN_VALID_VERSION = 7;
    public static final String TAG = BackupDataImporter.class.getSimpleName();

    private final Context context;
    private final CorpusMappingSQLHelper dbHelper;
    private final boolean merge;
    private boolean json;

    public BackupDataImporter(InputStream inputStream, Context context, boolean merge, boolean json) {
        super(inputStream);
        this.json = json;
        this.context = context.getApplicationContext();
        this.dbHelper = CorpusMappingSQLHelper.getInstance(context);
        this.merge = merge;
    }

    @Override
    protected void importData(InputStream inputStream) throws Exception {
        Log.d(TAG, "import data: " + (this.json ? "json" : "zip"));
        if (this.json) {
            JsonObject root = inputStreamToJson(inputStream);
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            try {
                database.beginTransaction();

                if (!merge) {
                    cleanDatabase(database);
                }
                if (root != null) {
                    GsonBuilder builder = new GsonBuilder()
                            .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
                    Gson gson = builder.create();

                    importPatients(database, gson, root);
                    importMoleGroups(database, gson, root);
                    importImageRecords(database, gson, root);

                }
                database.setTransactionSuccessful();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                throw e;
            } finally {
                database.endTransaction();
                database.close();
            }
        } else {
            ZipInputStream zin = new ZipInputStream(inputStream);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                Log.d(TAG, ze.getName());
                String name = ze.getName();
                if (name.contains("/")) {
                    String path = name.split("/")[0];
                    String fileName = name.split("/")[1];

                    long id = Long.parseLong(path.split("_")[0]);
                    String patientName = path.split("_")[1];

                    // copia as imagens para o  deretorio do paciente
                    File patientImagesDir = ImageUtils.getPatientImagesDir(id, patientName);
                    if (!patientImagesDir.exists()) {
                        patientImagesDir.mkdirs();
                    }
                    File imgFile = new File(patientImagesDir, fileName);

                    if (!imgFile.exists()) {
                        Log.d(TAG, "copying image: " + imgFile.getName());
                        // copia a imagem apenas se não existir
                        boolean created = imgFile.createNewFile();
                        if (created) {
                            FileOutputStream outputStream = new FileOutputStream(imgFile);
                            IOUtils.copy(zin, outputStream);
                            outputStream.close();
                        }
                    }
                }
                zin.closeEntry();
            }
            zin.close();
        }
    }


    private JsonObject inputStreamToJson(InputStream inputStream) {
        final JsonParser parser = new JsonParser();
        final JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));
        jsonReader.setLenient(true);
        final JsonElement jsonElement = parser.parse(jsonReader);
        return jsonElement.getAsJsonObject();
    }

    private void importPatients(SQLiteDatabase database, Gson gson, JsonObject root) {
        JsonArray jsonArray = root.get("patients").getAsJsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            Patient patient = gson.fromJson(jsonArray.get(i).getAsJsonObject(), Patient.class);
            PatientRepository patientRepository = PatientRepository.getInstance(context);

            Patient patientLocal = patientRepository.getById(patient.getId(), database);
            if (patientLocal != null) {
                //verifica a data da última alteração do registro antes de sobreescrever.

                if (patientLocal.getLastUpdate() != null && patient.getLastUpdate() != null &&
                        !patientLocal.getLastUpdate().isAfter(patient.getLastUpdate())) {
                    // só restaura com o valor do backup se a última alteração local foi anterior ao backup.
                    patientRepository.save(patient, database);
                }

            } else {
                patientRepository.insert(patient, database, patient.getId());
            }

        }
    }

    private void importMoleGroups(SQLiteDatabase database, Gson gson, JsonObject root) {
        JsonArray jsonArray = root.get("moleGroups").getAsJsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            MoleGroup moleGroup = gson.fromJson(jsonArray.get(i).getAsJsonObject(), MoleGroup.class);
            long id = moleGroup.getId();
            MoleGroupRepository repository = MoleGroupRepository.getInstance(context);
            MoleGroup moleGroupLocal = repository.getById(moleGroup.getId(), database);
            if (moleGroupLocal != null) {
                //verifica a data da última alteração do registro antes de sobreescrever.
                if (moleGroupLocal.getLastUpdate() != null && moleGroup.getLastUpdate() != null &&
                        !moleGroupLocal.getLastUpdate().isAfter(moleGroup.getLastUpdate())) {
                    // só restaura com o valor do backup se a última alteração local foi anterior ao backup.
                    repository.save(moleGroup, database);
                }
            } else {
                repository.insert(moleGroup, database, id);
            }
        }
    }

    private void importImageRecords(SQLiteDatabase database, Gson gson, JsonObject root) {
        JsonArray jsonArray = root.get("imageRecords").getAsJsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            ImageRecord imageRecord = gson.fromJson(jsonArray.get(i).getAsJsonObject(), ImageRecord.class);
            ImageRecordRepository repository = ImageRecordRepository.getInstance(context);
            ImageRecord imageRecordLocal = repository.getById(imageRecord.getId(), database);
            if (imageRecordLocal != null) {
                //verifica a data da última alteração do registro antes de sobreescrever.
                if (imageRecordLocal.getLastUpdate() != null && imageRecord.getLastUpdate() != null &&
                        !imageRecordLocal.getLastUpdate().isAfter(imageRecord.getLastUpdate())) {
                    // só restaura com o valor do backup se a última alteração local foi anterior ao backup.
                    repository.save(imageRecord, database);
                }
            } else {
                repository.insert(imageRecord, database, imageRecord.getId());
            }
        }
    }


    private void cleanDatabase(SQLiteDatabase db) {
        db.delete(ImageRecordRepository.TABLE_IMAGE_RECORD, null, null);
        db.delete(MoleGroupRepository.TABLE_MOLE_GROUP, null, null);
        db.delete(PatientRepository.TABLE_PATIENT, null, null);
    }

}
