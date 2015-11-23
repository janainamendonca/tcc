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

import br.furb.corpusmapping.ui.data.ExportActivity;
import br.furb.corpusmapping.util.EventBus;
import br.furb.corpusmapping.util.LocalExecutor;
import br.furb.corpusmapping.util.errors.ExportError;

public class DriveDataExporterRunnable implements Runnable {
    private final GoogleApiClient googleApiClient;
    private final DriveId driveId;
    private final Context context;
    private final EventBus eventBus;
    private final String fileTitle;

    public DriveDataExporterRunnable(GoogleApiClient googleApiClient, DriveId driveId, Context context, EventBus eventBus, String fileTitle) {
        this.googleApiClient = googleApiClient;
        this.driveId = driveId;
        this.context = context;
        this.eventBus = eventBus;
        this.fileTitle = fileTitle;
    }

    @Override
    public void run() {
        DriveFolder driveFolder = Drive.DriveApi.getFolder(googleApiClient, driveId);
        /*
         * Cria um folder onde será criado o arquivo json com os dados
         * e um arquivo zip com as imagens
         */
        MetadataChangeSet.Builder builder = new MetadataChangeSet.Builder();
        MetadataChangeSet changeSet = builder.setTitle(fileTitle).build();
        PendingResult<DriveFolder.DriveFolderResult> pendingResult =
                driveFolder.createFolder(googleApiClient, changeSet);
        DriveFolder.DriveFolderResult driveFolderResult = pendingResult.await();
        DriveFolder backupDriveFolder = driveFolderResult.getDriveFolder();

        exportJson(backupDriveFolder);
        exportImages(backupDriveFolder);
    }

    private void exportJson(DriveFolder backupDriveFolder) {
        // cria o json com os dados
        DriveApi.DriveContentsResult result = Drive.DriveApi.newDriveContents(googleApiClient).await();

        if (!result.getStatus().isSuccess()) {
            throw new ExportError("Data export has failed.");
        }

        DriveContents contents = result.getDriveContents();
        boolean isJson = true;
        BackupDataExporter exporter = new BackupDataExporter(contents.getOutputStream(), context, isJson);
        try {
            exporter.exportData();
            eventBus.post(exporter);
        } catch (Exception e) {
            e.printStackTrace();
            ExportError error = new ExportError("Data export has failed. " + e.getMessage(), e);
            eventBus.post(error);
        }

        MetadataChangeSet.Builder builder = new MetadataChangeSet.Builder();
        builder.setTitle(fileTitle + ".json");
        builder.setMimeType("application/json");
        MetadataChangeSet changeSet = builder.build();
        backupDriveFolder.createFile(googleApiClient, changeSet, contents).await();
    }

    private void exportImages(final DriveFolder backupDriveFolder) {
        // cria o zip com as imagens
        final DriveApi.DriveContentsResult result = Drive.DriveApi.newDriveContents(googleApiClient).await();

        if (!result.getStatus().isSuccess()) {
            throw new ExportError("Data export has failed.");
        }

        final DriveContents contents = result.getDriveContents();
        BackupDataExporter exporter = new BackupDataExporter(contents.getOutputStream(), context, false);
        try {
            exporter.exportData();
            eventBus.post(exporter);
        } catch (Exception e) {
            e.printStackTrace();
            ExportError error = new ExportError("Data export has failed. " + e.getMessage(), e);
            eventBus.post(error);
        }

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
