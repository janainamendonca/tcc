package br.furb.corpusmapping.ui.capture;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.furb.corpusmapping.R;
import br.furb.corpusmapping.util.ImageUtils;
import br.furb.corpusmapping.util.VerifyTemplateService;

/**
 * Activity para o usuário visualizar a imagem após a captura.
 * @author Janaina Carraro Mendonça Lima
 */
public class ViewPhotoActivity extends Activity implements View.OnClickListener {
    public static final String IMAGE_PATH = "IMAGE_PATH";
    private static final String TAG = ViewPhotoActivity.class.getSimpleName();
    private String path;
    private Bitmap croppedImage;
    private Uri uri;
    private Bitmap.CompressFormat outputFormat =
            Bitmap.CompressFormat.JPEG; // utilizado para salvar a imagem cortada.
    private int outputQuality = 100;

    private Button btOk;
    private Button btCancel;
    private TextView txtMsgWait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);

        path = getIntent().getStringExtra(IMAGE_PATH);
        uri = Uri.fromFile(new File(path));

        Bitmap originalImage = BitmapFactory.decodeFile(path, null);
        originalImage = adjustRotation(originalImage);
        croppedImage = ImageUtils.cropImage(this, originalImage);
        /////////
        saveCroppedImage();
        /////////

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(croppedImage);

        btCancel = (Button) findViewById(R.id.btnCancel);
        btCancel.setOnClickListener(this);
        btOk = (Button) findViewById(R.id.btnOk);
        btOk.setOnClickListener(this);

        txtMsgWait = (TextView) findViewById(R.id.txtMsgWait);

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
/*
        if (ImageUtils.containsTemplate(this, uri)) {
            Toast.makeText(this, "Gabarito detectado!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Gabarito não detectado!", Toast.LENGTH_LONG).show();
        }*/
        txtMsgWait.setVisibility(View.INVISIBLE);
        // btOk.setVisibility(View.INVISIBLE);
        //  btCancel.setVisibility(View.INVISIBLE);
        //   verifyTemplate();
    }

    private void verifyTemplate() {
        IntentFilter mStatusIntentFilter = new IntentFilter(
                VerifyTemplateService.RESULT_ACTION);

        // Instantiates a new DownloadStateReceiver
        ResponseReceiver mResponseReceiver =
                new ResponseReceiver();
        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mResponseReceiver,
                mStatusIntentFilter);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpi = displayMetrics.densityDpi;

        VerifyTemplateService.start(this, path, dpi, displayMetrics.heightPixels, displayMetrics.widthPixels);
    }

    // Broadcast receiver for receiving status updates from the IntentService
    private class ResponseReceiver extends BroadcastReceiver {
        // Prevents instantiation
        private ResponseReceiver() {
        }

        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean found = intent.getBooleanExtra(VerifyTemplateService.RESULT_STATUS, false);

            txtMsgWait.setVisibility(View.INVISIBLE);
            btOk.setVisibility(View.VISIBLE);
            btCancel.setVisibility(View.VISIBLE);

           /* if (found) {
                Toast.makeText(ViewPhotoActivity.this, "Gabarito detectado!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ViewPhotoActivity.this, "Gabarito não detectado!", Toast.LENGTH_LONG).show();
            }*/

            if (!found) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewPhotoActivity.this);
                builder.setMessage("Não foi possível detectar a presença do gabarito na imagem. Favor realizar a captura novamente.");
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new File(path).delete();
                        ViewPhotoActivity.this.setResult(Activity.RESULT_CANCELED);
                        finish();
                    }
                });
                if (!(ViewPhotoActivity.this.isFinishing())) {
                    builder.show();
                }
            }
        }
    }

    private void saveCroppedImage() {
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
            //apaga a imagem original
            //  saveCroppedImage();
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
