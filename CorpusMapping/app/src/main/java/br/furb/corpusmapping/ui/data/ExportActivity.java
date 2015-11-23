package br.furb.corpusmapping.ui.data;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;

import br.furb.corpusmapping.R;
import br.furb.corpusmapping.data.backup.DataExporter;
import br.furb.corpusmapping.data.backup.DriveDataExporterRunnable;
import br.furb.corpusmapping.util.LocalExecutor;
import br.furb.corpusmapping.util.errors.AppError;
import br.furb.corpusmapping.util.errors.ExportError;

public class ExportActivity extends BaseActivity {

    private static final int REQUEST_DRIVE_DIRECTORY = 2;

    private static final String FRAGMENT_GOOGLE_API = "FRAGMENT_GOOGLE_API";
    private static final String UNIQUE_GOOGLE_API_ID = ExportActivity.class.getName();

    private static final String STATE_IS_PROCESS_STARTED = "STATE_IS_PROCESS_STARTED";
    private static final String STATE_IS_DIRECTORY_REQUESTED = "STATE_IS_DIRECTORY_REQUESTED";

    private GoogleApiConnection connection;
    private ExecutorService localExecutor;

    private GoogleApiClient googleApiClient;
    private boolean isProcessStarted = false;
    private boolean isDirectoryRequested = false;

    public static void start(Context context) {
        final Intent intent = new Intent(context, ExportActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        connection = GoogleApiConnection.getInstance();
        localExecutor = LocalExecutor.getExecutor();

        if (savedInstanceState != null) {
            isProcessStarted = savedInstanceState.getBoolean(STATE_IS_PROCESS_STARTED, false);
            isDirectoryRequested = savedInstanceState.getBoolean(STATE_IS_DIRECTORY_REQUESTED, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getEventBus().register(this);
        if (!isProcessStarted) {
            isProcessStarted = true;
            isDirectoryRequested = startExportProcess(this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_IS_PROCESS_STARTED, isProcessStarted);
        outState.putBoolean(STATE_IS_DIRECTORY_REQUESTED, isDirectoryRequested);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK) {
            finish();
            return;
        }

        switch (requestCode) {

            case REQUEST_DRIVE_DIRECTORY:
                final DriveId driveId = data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                googleApiClient = connection.get(UNIQUE_GOOGLE_API_ID);
                onDriveDirectorySelected(driveId);
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
        if (error instanceof ExportError) {
            finish();
        }
    }

    @Subscribe
    public void onDataExporterFinished(DataExporter dataExporter) {
        Toast.makeText(this, R.string.done, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Subscribe
public void onGoogleApiClientConnected(GoogleApiConnection connection) {
    if (isDirectoryRequested || !connection.contains(UNIQUE_GOOGLE_API_ID)) {
        return;
    }

    googleApiClient = connection.get(UNIQUE_GOOGLE_API_ID);
    final IntentSender intentSender = Drive.DriveApi
            .newOpenFileActivityBuilder()
            .setMimeType(new String[]{"application/vnd.google-apps.folder"})
            .build(googleApiClient);

    try {
        startIntentSenderForResult(intentSender, REQUEST_DRIVE_DIRECTORY, null, 0, 0, 0);
        isDirectoryRequested = true;
    } catch (IntentSender.SendIntentException e) {
        throw new ExportError("Unable to show Google Drive.", e);
    }
}

    private void onDriveDirectorySelected(DriveId driveId) {
        exportData(new DriveDataExporterRunnable(googleApiClient, driveId, this, getEventBus(), getFileTitle()));
    }

    private String getFileTitle() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HHmmss");
        return getString(R.string.app_name) + " " + dateFormat.format(new Date());
    }

    private void exportData(Runnable exportRunnable) {
        localExecutor.execute(exportRunnable);
    }

    public boolean startExportProcess(FragmentActivity activity) {
        GoogleApiFragment googleApi_F = (GoogleApiFragment) activity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_GOOGLE_API);
        if (googleApi_F == null) {
            googleApi_F = GoogleApiFragment.with(UNIQUE_GOOGLE_API_ID).setUseDrive(true).build();
            activity.getSupportFragmentManager().beginTransaction().add(android.R.id.content, googleApi_F, FRAGMENT_GOOGLE_API).commit();
        }
        googleApi_F.connect();
        return false;
    }
}
