package br.furb.corpusmapping.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import br.furb.corpusmapping.data.model.Patient;

/**
 * Created by Janaina on 13/09/2015.
 */
public class ImageUtils {

    private static final String TAG = "ImageUtils";
    public static final int EXTERNAL_SQUARE_SIZE = 256;
    public static final int INTERNAL_SQUARE_SIZE = 96;

    /**
     * Retorna o diretório das imagens do paciente informado.
     * Se o diretório ainda não existir, ele será criado.
     *
     * @param patient o paciente que se deseja obter o diretório
     * @return File apontando para o diretório das imagens do paciente.
     */
    public static File getPatientImagesDir(Patient patient) {
        File patientDir = getPatientImagesDir(patient.getId(), patient.getName());
        patientDir.mkdirs();
        return patientDir;
    }

    /**
     * Retorna o diretório das imagens do paciente informado. Pode ser que o diretório exista ou não.
     *
     * @param id   id do paciente
     * @param name nome do paciente
     * @return
     */
    public static File getPatientImagesDir(long id, String name) {
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
        File f = getImageFile(imageShortPath);
        if (f != null && f.exists()) {
            return Uri.fromFile(f);
        }
        return null;
    }

    public static File getImageFile(String imageShortPath) {
        if (imageShortPath != null) {
            if (imageShortPath.contains(":")) {
                imageShortPath = imageShortPath.replace(":", "/");
            }

            Log.d("I", imageShortPath);
            File f = new File(getAppRootDir(), imageShortPath);
            Log.d("I", f.getAbsolutePath());
            return f;
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

    public static Bitmap cropImage(Context context, Bitmap image) {
        int centerY = image.getHeight() / 2;
        int centerX = image.getWidth() / 2;

        Log.d(TAG, "Center x: " + centerX);
        Log.d(TAG, "Center y: " + centerY);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        float dpi = displayMetrics.densityDpi;
        float pixelForDp = dpi / 160f;

        float roiSizeOnScreen = EXTERNAL_SQUARE_SIZE * pixelForDp;
        int roiSizeOnImage = (int) (roiSizeOnScreen * image.getHeight()) / displayMetrics.heightPixels;
        int width = (int) (roiSizeOnImage + (roiSizeOnImage * 0.2));
        int height = (int) (roiSizeOnImage + (roiSizeOnImage * 0.1));
        Log.d(TAG, "Density Dpi: " + dpi);
        Log.d(TAG, "Height pixels: " + displayMetrics.heightPixels);
        Log.d(TAG, "Width pixels: " + displayMetrics.widthPixels);
        Log.d(TAG, "Size: " + roiSizeOnImage);

        int x = centerX - (width / 2);
        int y = centerY - (height / 2);
        Bitmap croppedImage = Bitmap.createBitmap(image, x, y, width, height);

        return croppedImage;
    }

    public static Bitmap rotate(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), matrix, false);
    }

    public static boolean containsTemplate(Context context, Uri uri) {
        Bitmap bitmap = decodeSampledBitmapFromResource(context, uri, 300, 300);
        return verifySquare(bitmap);
    }

    private static boolean verifySquare(Bitmap image) {
        image = toGrayscale(image);

        int height = image.getHeight();
        int width = image.getWidth();

        Log.d(TAG, "Height: " + height);
        Log.d(TAG, "Width: " + width);

        List<Point> points = new ArrayList<>();
        int[][] matrixBinary = new int[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int color = image.getPixel(x, y);
                int red = Color.red(color);
                int green = Color.green(color);
                int blue = Color.blue(color);
                if (red < 85 && green < 85 && blue < 85) {
                    Point point = new Point();
                    point.x = x;
                    point.y = y;
                    points.add(point);
                    matrixBinary[x][y] = 1;
                }
            }
        }

        int totalSize = width * height;
        int rectSize = points.size();
        Log.d(TAG, "Size: " + totalSize);
        Log.d(TAG, "Rect size: " + rectSize);
        Log.d(TAG, "%: " + (100 * rectSize) / totalSize);

        int templateSize = 5; // tamanho do gabarito
        int[][] template = new int[templateSize][templateSize];
        for (int i = 0; i < templateSize; i++) {
            for (int j = 0; j < templateSize; j++) {
                template[i][j] = 1;
            }
        }
        boolean found = false;
        mainLoop:
        for (int i = 0; i < matrixBinary.length - templateSize; i++) {
            for (int j = 0; j < matrixBinary[i].length - templateSize; j++) {
                found = true;
                for (int k = 0; k < templateSize; k++) {
                    for (int l = 0; l < templateSize; l++) {
                        if (matrixBinary[i + k][j + l] != template[k][l]) {
                            found = false;
                        }
                    }
                }

                if (found) {
                    break mainLoop;
                }
            }
        }

        Log.d(TAG, "Encontrou gabarito: " + found);
        return found;

    }

    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    private static int diff(int a, int b) {
        int result = a - b;
        return result >= 0 ? result : result * -1;
    }

    public static Bitmap decodeSampledBitmapFromResource(Context ctx, Uri res,
                                                         int reqWidth, int reqHeight) {

        InputStream stream = null;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            stream = ctx.getContentResolver().openInputStream(res);
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
