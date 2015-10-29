package br.furb.corpusmapping.ui.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import br.furb.corpusmapping.R;


public class ViewPhotoActivity extends Activity implements View.OnClickListener {
    public static final String IMAGE_PATH = "IMAGE_PATH";
    public static final String IMAGE_URI = "IMAGE_URI";
    public static final String IMAGE_DATA = "image_data";
    private static final String TAG = ViewPhotoActivity.class.getSimpleName();
    private String path;
    private byte[] data;
    private Bitmap realImage;
    private Bitmap croppedBitMap;
    private Uri uri;
    private Bitmap.CompressFormat outputFormat =
            Bitmap.CompressFormat.JPEG; // utilizado para salvar a imagem cortada.
    private int outputQuality = 100;

    public static void start(Context context, String path) {
        final Intent intent = new Intent(context, ViewPhotoActivity.class);
        intent.putExtra(IMAGE_PATH, path);
        //  intent.putExtra(IMAGE_DATA, data);
        context.startActivity(intent);
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);

        path = getIntent().getStringExtra(IMAGE_PATH);
        uri = getIntent().getParcelableExtra(IMAGE_URI);
        // data = getIntent().getByteArrayExtra(IMAGE_DATA);


        final BitmapFactory.Options options = new BitmapFactory.Options();
        // options.inSampleSize = 2;
        //realImage = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        realImage = BitmapFactory.decodeFile(path, options);
        //
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Log.d("EXIF value",
                    exif.getAttribute(ExifInterface.TAG_ORIENTATION));
            if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                    .equalsIgnoreCase("1")) {
                realImage = rotate(realImage, 90);
            } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                    .equalsIgnoreCase("8")) {
                realImage = rotate(realImage, 90);
            } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                    .equalsIgnoreCase("3")) {
                realImage = rotate(realImage, 90);
            } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                    .equalsIgnoreCase("0")) {
                realImage = rotate(realImage, 90);
            }
        } catch (Exception e) {

        }
        //
        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        int centerY = realImage.getHeight() / 2;
        int centerX = realImage.getWidth() / 2;

        Log.d(getClass().getSimpleName(), "Center x: " + centerX);
        Log.d(getClass().getSimpleName(), "Center y: " + centerY);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        float dpi = displayMetrics.densityDpi;
        float dp = dpi / 160f;
        int size = (int) ((128 * dp) * realImage.getHeight()) / displayMetrics.heightPixels;
        int width = (int) (size + (size * 0.2));
        int height = (int) (size + (size * 0.1));
        Log.d(TAG, "Density Dpi: " + dpi);
        Log.d(TAG, "Height pixels: " + displayMetrics.heightPixels);
        Log.d(TAG, "Width pixels: " + displayMetrics.widthPixels);
        Log.d(TAG, "Size: " + size);

        //croppedBitMap = Bitmap.createBitmap(realImage, centerX - (centerX / 2), centerY - (centerY / 2), centerX, centerY);
        croppedBitMap = Bitmap.createBitmap(realImage, centerX - (width / 2), centerY - (height / 2), width, height);
        imageView.setImageBitmap(croppedBitMap);

        //  imageView.setImageBitmap(realImage);

        findViewById(R.id.btnCancel).setOnClickListener(this);
        findViewById(R.id.btnOk).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnOk) {
            new File(path).delete();

            OutputStream outputStream = null;
            try {
                outputStream = getContentResolver().openOutputStream(uri);
                if (outputStream != null) {
                    croppedBitMap.compress(outputFormat, outputQuality, outputStream);
                }
            } catch (IOException ex) {
                // TODO: report error to caller
                Log.e(TAG, "Cannot open file: " + uri, ex);
            } finally {
                try {
                    if (outputStream != null) outputStream.close();
                } catch (IOException e) {
                }
            }
/*
            new File(path).delete();

            ByteBuffer byteBuffer = ByteBuffer.allocate(croppedBitMap.getByteCount());
            croppedBitMap.copyPixelsToBuffer(byteBuffer);

            byte[] bytes = byteBuffer.array();
            //salva a imagem
            FileOutputStream outStream = null;
            try {
                // Write to SD Card
                outStream = new FileOutputStream(path);
                // outStream.write(data);
                IOUtils.write(bytes, outStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
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
*/
            setResult(Activity.RESULT_OK);
            finish();
        } else {
            new File(path).delete();
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }


    public static Bitmap rotate(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), matrix, false);
    }
}
