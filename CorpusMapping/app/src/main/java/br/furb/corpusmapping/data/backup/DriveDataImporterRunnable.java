package br.furb.corpusmapping.data.backup;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
import com.google.android.gms.drive.query.Query;

import br.furb.corpusmapping.data.CorpusMappingSQLHelper;
import br.furb.corpusmapping.ui.settings.data.ImportActivity;
import br.furb.corpusmapping.util.EventBus;
import br.furb.corpusmapping.util.LocalExecutor;
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
        DriveApi.MetadataBufferResult metadataBufferResult = pendingResult.await();
        MetadataBuffer metadataBuffer = metadataBufferResult.getMetadataBuffer();
        if (metadataBuffer.getCount() == 2) {

            try {

                Metadata jsonMetadata;
                Metadata zipMetadata;
                if (metadataBuffer.get(0).getMimeType().equals("application/json")) {
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
            //throw new ImportError("A pasta escolhida é inválida para importação dos dados");
            this.eventBus.post(new ImportError("A pasta escolhida é inválida para importação dos dados"));
        }
    }

    private void importJson(Metadata jsonMetadata) {
        final DriveFile driveFile = Drive.DriveApi.getFile(googleApiClient, jsonMetadata.getDriveId());
        final DriveApi.DriveContentsResult result = driveFile.open(googleApiClient, DriveFile.MODE_READ_ONLY, null).await();

        if (!result.getStatus().isSuccess()) {
            throw new ImportError("A importação falhou. Tente novamente. Status do Google Drive: " + result.getStatus().getStatusMessage());
        }

        final DriveContents contents = result.getDriveContents();
        final DataImporterRunnable dataImporterRunnable = new DataImporterRunnable(eventBus, importType.getDataImporter(contents.getInputStream(), context, true));
        dataImporterRunnable.run();
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
