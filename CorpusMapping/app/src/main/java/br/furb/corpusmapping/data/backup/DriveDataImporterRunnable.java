package br.furb.corpusmapping.data.backup;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;

import br.furb.corpusmapping.data.CorpusMappingSQLHelper;
import br.furb.corpusmapping.ui.settings.data.ImportActivity;
import br.furb.corpusmapping.util.EventBus;
import br.furb.corpusmapping.util.errors.ImportError;

public class DriveDataImporterRunnable implements Runnable {
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

    @Override public void run() {
        final DriveFile driveFile = Drive.DriveApi.getFile(googleApiClient, driveId);
        final DriveApi.DriveContentsResult result = driveFile.open(googleApiClient, DriveFile.MODE_READ_ONLY, null).await();

        if (!result.getStatus().isSuccess()) {
            throw new ImportError("Import failed. Result status from Google Drive " + result.getStatus().getStatusMessage());
        }

        final DriveContents contents = result.getDriveContents();
        final DataImporterRunnable dataImporterRunnable = new DataImporterRunnable(eventBus, importType.getDataImporter(contents.getInputStream(), context));
        dataImporterRunnable.run();
    }
}
