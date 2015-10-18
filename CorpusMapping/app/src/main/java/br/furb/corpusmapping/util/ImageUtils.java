package br.furb.corpusmapping.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.furb.corpusmapping.data.Patient;

/**
 * Created by Janaina on 13/09/2015.
 */
public class ImageUtils {

    public static File getPatientImagesDir(Patient patient) {
        File patientDir = getPatientImagesDir(patient.getId(), patient.getName());
        patientDir.mkdirs();
        return patientDir;
    }

    public static File getPatientImagesDir(long id, String name){
        File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "CorpusMapping");
        root.mkdirs();

        File[] files = root.listFiles();
        File patientDir = null;

        for (File f : files) {
            // faz isso porque pode ser que o nome do paciente tenha sido alterado no cadastro dele
            if (f.getName().startsWith(id + "_")) {
                patientDir = f;
            }
        }

        if (patientDir == null) {
            patientDir = new File(root, id + "_" + name);
        }

        return patientDir;
    }

    public static File getFileForNewImage(Patient patient) {
        File patientDir = ImageUtils.getPatientImagesDir(patient);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = timeStamp + ".jpg";
        File sdImageFile = new File(patientDir, fileName);
        return sdImageFile;
    }

    public static String getImageShortPath(File imageFile) {
        String[] paths = imageFile.getAbsolutePath().split("/");
        return paths[paths.length - 2] + "/" + paths[paths.length - 1];
    }

    public static File getAppRootDir() {
        File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "CorpusMapping");
        root.mkdirs();
        return root;
    }

    public static Uri getImageUri(String imageShortPath) {
        if (imageShortPath != null) {
            if (imageShortPath.contains(":")) {
                imageShortPath = imageShortPath.replace(":", "/");
            }

            Log.d("I", imageShortPath);
            File f = new File(getAppRootDir(), imageShortPath);
            Log.d("I", f.getAbsolutePath());
            if (f.exists()) {
                return Uri.fromFile(f);
            }
        }
        return null;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Context ctx, Uri res,
                                                  int reqWidth, int reqHeight) {

        InputStream stream = null;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            stream = ctx.getContentResolver().openInputStream(res);
            // d = Drawable.createFromStream(stream, null);

            // First decode with inJustDecodeBounds=true to check dimensions
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(stream, null, options);
        } catch (Exception e) {
            Log.w("ImageView", "Unable to open content: " + res, e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    Log.w("ImageView", "Unable to close content: " + res, e);
                }
            }
        }

        try {
            stream = ctx.getContentResolver().openInputStream(res);
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(stream, null, options);
        } catch (Exception e) {
            Log.w("ImageView", "Unable to open content: " + res, e);
            return null;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    Log.w("ImageView", "Unable to close content: " + res, e);
                }
            }
        }


    }
}
