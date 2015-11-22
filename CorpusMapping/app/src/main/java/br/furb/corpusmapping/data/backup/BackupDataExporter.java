package br.furb.corpusmapping.data.backup;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import br.furb.corpusmapping.data.model.ImageRecord;
import br.furb.corpusmapping.data.database.ImageRecordRepository;
import br.furb.corpusmapping.data.model.MoleGroup;
import br.furb.corpusmapping.data.database.MoleGroupRepository;
import br.furb.corpusmapping.data.model.Patient;
import br.furb.corpusmapping.data.database.PatientRepository;
import br.furb.corpusmapping.util.ZipManager;

public class BackupDataExporter extends DataExporter {

    private static final String CHARSET_NAME = "UTF-8";

    private final Context context;

    private final boolean json;

    public BackupDataExporter(OutputStream outputStream, Context context, boolean json) {
        super(outputStream);
        this.context = context;
        this.json = json;
    }

    @Override
    public void exportData(OutputStream outputStream) throws Exception {
        if (json) {

            GsonBuilder builder = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
            Gson gson = builder.create();
            JsonObject root = new JsonObject();

            addPatients(gson, root);
            addMoleGroups(gson, root);
            addImageRecords(gson, root);

            outputStream.write(root.toString().getBytes(CHARSET_NAME));
            outputStream.flush();
            outputStream.close();
        } else {
            ImageRecordRepository repository = ImageRecordRepository.getInstance(context);
            List<ImageRecord> imageRecords = repository.getAll();
            // obtem as imagens
            List<String> images = new ArrayList<>();

            for (ImageRecord record : imageRecords) {
                if (record.getImagePath() != null) {
                    images.add(record.getImagePath());
                }
            }
            // manda compactar as imagens em um arquivo zip
            ZipManager.zip(context, images, outputStream);
        }
    }

    private void addPatients(Gson gson, JsonObject root) {
        PatientRepository patientRepository = PatientRepository.getInstance(context);
        List<Patient> patients = patientRepository.getAll();
        JsonElement patientsJson = gson.toJsonTree(patients);

        root.add("patients", patientsJson);
    }

    private void addMoleGroups(Gson gson, JsonObject root) {
        MoleGroupRepository repository = MoleGroupRepository.getInstance(context);
        List<MoleGroup> moleGroups = repository.getAll();
        JsonElement json = gson.toJsonTree(moleGroups);
        root.add("moleGroups", json);
    }

    private List<ImageRecord> addImageRecords(Gson gson, JsonObject root) {
        ImageRecordRepository repository = ImageRecordRepository.getInstance(context);
        List<ImageRecord> records = repository.getAll();
        JsonElement json = gson.toJsonTree(records);
        root.add("imageRecords", json);
        return records;
    }

}
