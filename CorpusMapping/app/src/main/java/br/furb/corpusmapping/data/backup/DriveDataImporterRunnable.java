package br.furb.corpusmapping.data.backup;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;

import br.furb.corpusmapping.data.database.CorpusMappingSQLHelper;
import br.furb.corpusmapping.ui.data.ImportActivity;
import br.furb.corpusmapping.util.EventBus;
import br.furb.corpusmapping.util.IOUtils;
import br.furb.corpusmapping.util.errors.ImportError;

public class DriveDataImporterRunnable implements Runnable {
    public static final String TAG = DriveDataImporterRunnable.class.getSimpleName();
    private final GoogleApiClient googleApiClient;
    private final DriveId driveId;
    private final ImportActivity.ImportType importType;
    private final Context context;
    private final CorpusMappingSQLHelper dbHelper;
    private final EventBus eventBus;

    public DriveDataImporterRunnable(GoogleApiClient googleApiClient, DriveId driveId, ImportActivity.ImportType importType, Context context, EventBus eventBus) {
        this.googleApiClient = googleApiClient;
        this.driveId = driveId;
        this.importType = importType;
        this.context = context;
        this.dbHelper = CorpusMappingSQLHelper.getInstance(context);
        this.eventBus = eventBus;
    }

    @Override
    public void run() {
        DriveFolder folder = Drive.DriveApi.getFolder(googleApiClient, driveId);

        PendingResult<DriveApi.MetadataBufferResult> pendingResult = folder.listChildren(googleApiClient);
        DriveApi.MetadataBufferResult result = pendingResult.await();
        MetadataBuffer metadataBuffer = result.getMetadataBuffer();
        if (metadataBuffer.getCount() == 2) {

            try {

                Metadata jsonMetadata;
                Metadata zipMetadata;
                String firstMimeType = metadataBuffer.get(0).getMimeType();
                if (firstMimeType.equals("application/json")) {
                    jsonMetadata = metadataBuffer.get(0);
                    zipMetadata = metadataBuffer.get(1);
                } else {
                    zipMetadata = metadataBuffer.get(0);
                    jsonMetadata = metadataBuffer.get(1);
                }

                importJson(jsonMetadata);
                importZip(zipMetadata);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                if (e instanceof ImportError) {
                    this.eventBus.post(e);
                } else {
                    this.eventBus.post(new ImportError("Ocorreu um erro durante a importação dos dados. Tente novamente.", e));
                }
            }
        } else {
            this.eventBus.post(new ImportError("A pasta escolhida é inválida para importação dos dados"));
        }
    }

private void importJson(Metadata jsonMetadata) {
    DriveFile driveFile = Drive.DriveApi.getFile(googleApiClient, jsonMetadata.getDriveId());
    DriveApi.DriveContentsResult result = driveFile.open(googleApiClient, DriveFile.MODE_READ_ONLY, null).await();

    if (!result.getStatus().isSuccess()) {
        throw new ImportError("A importação falhou. Tente novamente. Status do Google Drive: " + result.getStatus().getStatusMessage());
    }

    DriveContents contents = result.getDriveContents();

    boolean isJson = true;
    BackupDataImporter importer = new BackupDataImporter(contents.getInputStream(), context, false, isJson);
    try {
        importer.importData();
        eventBus.post(importer);
    } catch (Exception e) {
        e.printStackTrace();
        ImportError error = new ImportError("A importação dos dados falhou. " + e.getMessage(), e);
        eventBus.post(error);
    } finally {
        IOUtils.closeQuietly(importer);
    }

}

    private void importZip(final Metadata zipMetadata) {
        Log.d(TAG, "import zip");
        final DriveFile driveFile = Drive.DriveApi.getFile(googleApiClient, zipMetadata.getDriveId());
        final DriveApi.DriveContentsResult result = driveFile.open(googleApiClient, DriveFile.MODE_READ_ONLY, null).await();
        Log.d(TAG, "result status: " + result.getStatus());
        Log.d(TAG, "result status isSuccess: " + result.getStatus().isSuccess());

        if (!result.getStatus().isSuccess()) {
            throw new ImportError("A importação falhou. Tente novamente. Status do Google Drive: " + result.getStatus().getStatusMessage());
        }

        final DriveContents contents = result.getDriveContents();
        final DataImporterRunnable dataImporterRunnable = new DataImporterRunnable(eventBus, importType.getDataImporter(contents.getInputStream(), context, false));
        dataImporterRunnable.run();
    }
}
