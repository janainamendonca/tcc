package br.furb.corpusmapping.ui.data;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;

import br.furb.corpusmapping.R;
import br.furb.corpusmapping.data.backup.BackupDataImporter;
import br.furb.corpusmapping.data.backup.DataImporter;
import br.furb.corpusmapping.data.backup.DriveDataImporterRunnable;
import br.furb.corpusmapping.util.LocalExecutor;
import br.furb.corpusmapping.util.errors.AppError;
import br.furb.corpusmapping.util.errors.ImportError;

public class ImportActivity extends BaseActivity {
    private static final String EXTRA_IMPORT_TYPE = "EXTRA_IMPORT_TYPE";
    private static final String EXTRA_SOURCE = "EXTRA_SOURCE";

    private static final int REQUEST_LOCAL_FILE = 1;
    private static final int REQUEST_DRIVE_FILE = 2;

    private static final String FRAGMENT_GOOGLE_API = "FRAGMENT_GOOGLE_API";
    private static final String UNIQUE_GOOGLE_API_ID = ImportActivity.class.getName();

    private static final String STATE_IS_PROCESS_STARTED = "STATE_IS_PROCESS_STARTED";
    private static final String STATE_IS_FILE_REQUESTED = "STATE_IS_FILE_REQUESTED";

    GoogleApiConnection connection;
    ExecutorService localExecutor;

    private ImportType importType;
    private GoogleApiClient googleApiClient;
    private boolean isProcessStarted = false;
    private boolean isFileRequested = false;

    public static void start(Context context, ImportType importType) {
        final Intent intent = new Intent(context, ImportActivity.class);
        intent.putExtra(EXTRA_IMPORT_TYPE, importType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        connection = GoogleApiConnection.getInstance();
        localExecutor = LocalExecutor.getExecutor();

        // Get extras
        importType = (ImportType) getIntent().getSerializableExtra(EXTRA_IMPORT_TYPE);

        // Restore state
        if (savedInstanceState != null) {
            isProcessStarted = savedInstanceState.getBoolean(STATE_IS_PROCESS_STARTED, false);
            isFileRequested = savedInstanceState.getBoolean(STATE_IS_FILE_REQUESTED, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getEventBus().register(this);
        if (!isProcessStarted) {
            isProcessStarted = true;
            isFileRequested = startImportProcess(this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_IS_PROCESS_STARTED, isProcessStarted);
        outState.putBoolean(STATE_IS_FILE_REQUESTED, isFileRequested);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            finish();
            return;
        }

        switch (requestCode) {
            case REQUEST_DRIVE_FILE:
                final DriveId driveId = data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                googleApiClient = connection.get(UNIQUE_GOOGLE_API_ID);
                onDriveFileSelected(driveId);
                break;
        }

        final GoogleApiFragment googleApi_F = (GoogleApiFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_GOOGLE_API);
        if (googleApi_F != null) {
            googleApi_F.handleOnActivityResult(requestCode, resultCode);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getEventBus().unregister(this);
    }

    @Override
    protected void onHandleError(AppError error) {
        super.onHandleError(error);
        if (error instanceof ImportError) {
            finish();
        }
    }

    @Subscribe
    public void onDataImporterFinished(DataImporter dataImporter) {
        Toast.makeText(this, R.string.done, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Subscribe
    public void onGoogleApiClientConnected(GoogleApiConnection connection) {
        if (isFileRequested || !connection.contains(UNIQUE_GOOGLE_API_ID)) {
            return;
        }

        googleApiClient = connection.get(UNIQUE_GOOGLE_API_ID);
        final IntentSender intentSender = Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[]{"application/vnd.google-apps.folder"})
                .build(googleApiClient);

        try {
            startIntentSenderForResult(intentSender, REQUEST_DRIVE_FILE, null, 0, 0, 0);
            isFileRequested = true;
        } catch (IntentSender.SendIntentException e) {
            throw new ImportError("Unable to show Google Drive.", e);
        }
    }

    private void onDriveFileSelected(DriveId driveId) {
        importData(new DriveDataImporterRunnable(googleApiClient, driveId, importType, this, getEventBus()));
    }

    private void importData(Runnable importRunnable) {
        localExecutor.execute(importRunnable);
    }

    public boolean startImportProcess(BaseActivity activity) {
        GoogleApiFragment googleApi_F = (GoogleApiFragment) activity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_GOOGLE_API);
        if (googleApi_F == null) {
            googleApi_F = GoogleApiFragment.with(UNIQUE_GOOGLE_API_ID).setUseDrive(true).build();
            activity.getSupportFragmentManager().beginTransaction().add(android.R.id.content, googleApi_F, FRAGMENT_GOOGLE_API).commit();
        }
        googleApi_F.connect();
        return false;
    }

    public static enum ImportType {
        Backup {
            @Override
            public DataImporter getDataImporter(InputStream inputStream, Context context, boolean json) {
                return new BackupDataImporter(inputStream, context, false, json);
            }
        },

        MergeBackup {
            @Override
            public DataImporter getDataImporter(InputStream inputStream, Context context, boolean json) {
                return new BackupDataImporter(inputStream, context, true, json);
            }
        };

        public abstract DataImporter getDataImporter(InputStream inputStream, Context context, boolean json);
    }

}
