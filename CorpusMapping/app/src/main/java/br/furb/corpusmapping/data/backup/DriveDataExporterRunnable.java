package br.furb.corpusmapping.data.backup;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.internal.pe;

import br.furb.corpusmapping.ui.settings.data.ExportActivity;
import br.furb.corpusmapping.util.EventBus;
import br.furb.corpusmapping.util.LocalExecutor;
import br.furb.corpusmapping.util.errors.ExportError;

public class DriveDataExporterRunnable implements Runnable {
    private final GoogleApiClient googleApiClient;
    private final DriveId driveId;
    private final ExportActivity.ExportType exportType;
    private final ExportActivity.Destination destination;
    private final Context context;
    private final EventBus eventBus;
    private final String fileTitle;
    private ExportCallback callback;

    public DriveDataExporterRunnable(GoogleApiClient googleApiClient, DriveId driveId, ExportActivity.ExportType exportType, ExportActivity.Destination destination, Context context, EventBus eventBus, String fileTitle, ExportCallback callback) {
        this.googleApiClient = googleApiClient;
        this.driveId = driveId;
        this.exportType = exportType;
        this.destination = destination;
        this.context = context;
        this.eventBus = eventBus;
        this.fileTitle = fileTitle;
        this.callback = callback;
    }

    @Override
    public void run() {
        final DriveFolder driveFolder = Drive.DriveApi.getFolder(googleApiClient, driveId);

        // Cria um folder onde será criado um arquivo json com os dados e um arquivo zip com as imagens
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle(fileTitle).build();
        PendingResult<DriveFolder.DriveFolderResult> pendingResult = driveFolder.createFolder(googleApiClient, changeSet);
        DriveFolder.DriveFolderResult driveFolderResult = pendingResult.await();
        DriveFolder backupDriveFolder = driveFolderResult.getDriveFolder();

        exportJson(backupDriveFolder);
        exportImages(backupDriveFolder);

        callback.onExportFinished();
    }

    private void exportJson(DriveFolder backupDriveFolder) {
        // cria o json com os dados
        final DriveApi.DriveContentsResult result = Drive.DriveApi.newDriveContents(googleApiClient).await();

        if (!result.getStatus().isSuccess()) {
            throw new ExportError("Data export has failed.");
        }

        final DriveContents contents = result.getDriveContents();
        final DataExporterRunnable dataExporterRunnable = new DataExporterRunnable(eventBus, exportType.getDataExporter(contents.getOutputStream(), context, true));
        dataExporterRunnable.run();

        MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle(fileTitle + ".json").setMimeType("application/json").build();
        backupDriveFolder.createFile(googleApiClient, changeSet, contents).await();
    }

    private void exportImages(final DriveFolder backupDriveFolder) {
        // cria o zip com as imagens
        final DriveApi.DriveContentsResult result = Drive.DriveApi.newDriveContents(googleApiClient).await();

        if (!result.getStatus().isSuccess()) {
            throw new ExportError("Data export has failed.");
        }

        final DriveContents contents = result.getDriveContents();
        final DataExporterRunnable dataExporterRunnable = new DataExporterRunnable(eventBus, exportType.getDataExporter(contents.getOutputStream(), context, false));
        dataExporterRunnable.run();

        final MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle(fileTitle + ".zip").setMimeType("application/zip").build();

        if (!googleApiClient.isConnected() && !googleApiClient.isConnecting()) {

            googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle bundle) {
                    LocalExecutor.getExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            backupDriveFolder.createFile(googleApiClient, changeSet, contents).await();
                            googleApiClient.disconnect();
                        }
                    });
                }

                @Override
                public void onConnectionSuspended(int i) {

                }
            });

            googleApiClient.connect();

        }

    }
}
