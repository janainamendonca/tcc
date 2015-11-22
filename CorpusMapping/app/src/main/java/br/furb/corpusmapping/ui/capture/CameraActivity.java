package br.furb.corpusmapping.ui.capture;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.PictureCallback;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import java.io.FileOutputStream;
import java.io.IOException;

import br.furb.corpusmapping.R;

public class CameraActivity extends Activity {

    public static final String IMAGE_PATH = "IMAGE_PATH";
    private Activity context;
    private CameraPreview cameraPreview;
    private Camera camera;
    private String path;
    private FrameLayout container;
    private SurfaceView surfaceView;
    private static final int REQUEST_CODE_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        context = this;
        container = (FrameLayout) findViewById(R.id.container);

        surfaceView = (SurfaceView) findViewById(R.id.cameraView);
        cameraPreview = new CameraPreview(this, surfaceView);
        FrameLayout frame = (FrameLayout) findViewById(R.id.preview);
        frame.addView(cameraPreview);
        cameraPreview.setKeepScreenOn(true);
        surfaceView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    takeFocusedPicture();
                } catch (Exception e) {

                }
                surfaceView.setClickable(false);
            }
        });

        path = getIntent().getStringExtra(IMAGE_PATH);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (camera == null) {
            camera = Camera.open();
            camera.startPreview();
            camera.setErrorCallback(new ErrorCallback() {
                public void onError(int error, Camera mcamera) {

                    camera.release();
                    camera = Camera.open();
                    Log.d("Camera died", "error camera");

                }
            });
        }
        if (camera != null) {
            if (Build.VERSION.SDK_INT >= 14)
                setCameraDisplayOrientation(context,
                        CameraInfo.CAMERA_FACING_BACK, camera);
            cameraPreview.setCamera(camera);
        }
    }

    private void setCameraDisplayOrientation(Activity activity, int cameraId,
                                             Camera camera) {
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }


    public void takeFocusedPicture() {
        camera.autoFocus(autoFocusCallback);

    }

    Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            camera.takePicture(new Camera.ShutterCallback() {
                @Override
                public void onShutter() {

                }
            }, null, jpegCallback);
        }
    };

    PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            //salva a imagem
            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream(path);
                outStream.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (outStream != null) {
                    try {
                        outStream.close();
                    } catch (IOException e) {
                    }
                }
            }

            Intent intent = new Intent(context, ViewPhotoActivity.class);
            intent.putExtra(IMAGE_PATH, path);
            startActivityForResult(intent, REQUEST_CODE_IMAGE);
            surfaceView.setClickable(true);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_IMAGE) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void finish() {
        camera.stopPreview();
        camera.release();
        super.finish();
    }
}
