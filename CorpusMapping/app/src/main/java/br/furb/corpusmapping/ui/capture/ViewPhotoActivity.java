package br.furb.corpusmapping.ui.capture;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import br.furb.corpusmapping.R;
import br.furb.corpusmapping.util.ImageUtils;


public class ViewPhotoActivity extends Activity implements View.OnClickListener {
    public static final String IMAGE_PATH = "IMAGE_PATH";
    private static final String TAG = ViewPhotoActivity.class.getSimpleName();
    private String path;
    private Bitmap croppedImage;
    private Uri uri;
    private Bitmap.CompressFormat outputFormat =
            Bitmap.CompressFormat.JPEG; // utilizado para salvar a imagem cortada.
    private int outputQuality = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);

        path = getIntent().getStringExtra(IMAGE_PATH);
        uri = Uri.fromFile(new File(path));

        Bitmap originalImage = BitmapFactory.decodeFile(path, null);
        originalImage = adjustRotation(originalImage);
        croppedImage = ImageUtils.cropImage(this, originalImage);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(croppedImage);

        //CÓDIGO

        findViewById(R.id.btnCancel).setOnClickListener(this);
        findViewById(R.id.btnOk).setOnClickListener(this);



       /* if (!ImageUtils.containsTemplate(this, uri)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Não foi possível detectar a presença do gabarito na imagem. Favor realizar a captura novamente.");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new File(path).delete();
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                }
            });
            builder.show();
        }*/

        if (ImageUtils.containsTemplate(this, uri)) {
            Toast.makeText(this, "Gabarito detectado!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Gabarito não detectado!", Toast.LENGTH_LONG).show();
        }
    }

    private Bitmap adjustRotation(Bitmap originalImage) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap result = originalImage;
        Log.d("EXIF value",
                exif.getAttribute(ExifInterface.TAG_ORIENTATION));
        if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                .equalsIgnoreCase("1")) {
            result = ImageUtils.rotate(originalImage, 90);
        } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                .equalsIgnoreCase("8")) {
            result = ImageUtils.rotate(originalImage, 90);
        } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                .equalsIgnoreCase("3")) {
            result = ImageUtils.rotate(originalImage, 90);
        } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                .equalsIgnoreCase("0")) {
            result = ImageUtils.rotate(originalImage, 90);
        }

        return result;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnOk) {
       /*     new File(path).delete();

            OutputStream outputStream = null;
            try {
                outputStream = getContentResolver().openOutputStream(uri);
                if (outputStream != null) {
                    croppedImage.compress(outputFormat, outputQuality, outputStream);
                }
            } catch (IOException ex) {
                // TODO: report error to caller
                Log.e(TAG, "Cannot open file: " + uri, ex);
            } finally {
                try {
                    if (outputStream != null) outputStream.close();
                } catch (IOException e) {
                }
            }*/
/*
            new File(path).delete();

            ByteBuffer byteBuffer = ByteBuffer.allocate(croppedImage.getByteCount());
            croppedImage.copyPixelsToBuffer(byteBuffer);

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
            //apaga a imagem original
            new File(path).delete();

            // salva a imagem recortada
            OutputStream outputStream = null;
            try {
                outputStream = getContentResolver().openOutputStream(uri);
                if (outputStream != null) {
                    croppedImage.compress(outputFormat, outputQuality, outputStream);
                }
            } catch (IOException ex) {
                Log.e(TAG, "Cannot open file: " + uri, ex);
            } finally {
                try {
                    if (outputStream != null) outputStream.close();
                } catch (IOException e) {
                }
            }
            setResult(Activity.RESULT_OK);
            finish();
        } else {
            //apaga a imagem
            new File(path).delete();
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }

}
