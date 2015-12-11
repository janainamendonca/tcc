package br.furb.corpusmapping.util;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Service que verifica a presença do gabarito preenchido.
 * Obs.: nao está sendo utilizado porque não é mais realizada a verificação.
 *
 * @author Janaina Carraro Mendonça Lima
 */
public class VerifyTemplateService extends IntentService {
    private static final String IMAGE_PATH = "image_path";
    private static final String TAG = "VerifyTemplateService";
    private static final String DPI = "dpi";
    private static final String SCREEN_HEIGHT = "screen_height";
    private static final String SCREEN_WIDTH = "screen_width";
    public static final int INTERNAL_SQUARE_SIZE = 96;

    public static final String RESULT_ACTION = VerifyTemplateService.class.getCanonicalName();
    public static final String RESULT_STATUS = VerifyTemplateService.class.getCanonicalName() + "_status";

    public static void start(Context context, String imagePath, float dpi, int screenHeight, int screenWidth) {
        Intent intent = new Intent(context, VerifyTemplateService.class);
        intent.putExtra(IMAGE_PATH, imagePath);
        intent.putExtra(DPI, dpi);
        intent.putExtra(SCREEN_HEIGHT, screenHeight);
        intent.putExtra(SCREEN_WIDTH, screenWidth);
        context.startService(intent);
    }

    public VerifyTemplateService() {
        super("VerifyTemplateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String imagePath = intent.getStringExtra(IMAGE_PATH);
            final float dpi = intent.getFloatExtra(DPI, 0);
            int screenHeight = intent.getIntExtra(SCREEN_HEIGHT, 0);
            int screenWidth = intent.getIntExtra(SCREEN_WIDTH, 0);
            boolean found = foundTemplate(imagePath, dpi, screenHeight, screenWidth);

            Intent localIntent =
                    new Intent(RESULT_ACTION)
                            .putExtra(RESULT_STATUS, found);
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

        }
    }

    private boolean foundTemplate(String imagePath, float dpi, int screenHeight, int screenWidth) {
        Bitmap image = ImageUtils.decodeSampledBitmapFromResource(this, Uri.fromFile(new File(imagePath)), 300, 300);
        image = ImageUtils.toGrayscale(image);

        int height = image.getHeight();
        int width = image.getWidth();

        Log.d(TAG, "Height: " + height);
        Log.d(TAG, "Width: " + width);

        List<Point> points = new ArrayList<>();
        int[][] matrixBinary = new int[width][height];

        //**********/
        int centerY = image.getHeight() / 2;
        int centerX = image.getWidth() / 2;
        float pixelForDp = dpi / 160f;
        float roiSizeOnScreen = INTERNAL_SQUARE_SIZE * pixelForDp;
        int roiSizeOnImage = (int) (roiSizeOnScreen * image.getHeight()) / screenHeight;
        int width1 = (int) (roiSizeOnImage + (roiSizeOnImage * 0.2));
        int height1 = (int) (roiSizeOnImage + (roiSizeOnImage * 0.1));
        Log.d(TAG, "Density Dpi: " + dpi);
        Log.d(TAG, "Height pixels: " + screenHeight);
        Log.d(TAG, "Width pixels: " + screenWidth);
        Log.d(TAG, "Size: " + roiSizeOnImage);

        int x1 = centerX - (width1 / 2);
        int y1 = centerY - (height1 / 2);

        //**********/

        BoundingBox bbox = new BoundingBox(x1, y1, x1 + width1, y1 + height1);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!bbox.isInner(x, y)) {
                    int color = image.getPixel(x, y);
                    int red = Color.red(color);
                    int green = Color.green(color);
                    int blue = Color.blue(color);
                    if (red < 90 && green < 90 && blue < 90) {
                        Point point = new Point();
                        point.x = x;
                        point.y = y;
                        points.add(point);
                        matrixBinary[x][y] = 1;
                        //
                    }
                }
            }
        }


        int totalSize = width * height;
        int rectSize = points.size();
        Log.d(TAG, "Size: " + totalSize);
        Log.d(TAG, "Rect size: " + rectSize);
        Log.d(TAG, "%: " + (100 * rectSize) / totalSize);

        int templateSize = 50; // tamanho do gabarito
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
                found = false;
                int blackCount1 = 0;
                int blackCount2 = 0;
                int blackCount3 = 0;
                int blackCount4 = 0;

                int s = templateSize / 2;
                // divide o template em quato partes
                for (int k = 0; k < s; k++) {
                    for (int l = 0; l < s; l++) {
                        if (matrixBinary[i + k][j + l] == template[k][l]) {
                            blackCount1 += 1;
                        }
                    }
                }

                for (int k = 0; k < s; k++) {
                    for (int l = s - 1; l < templateSize; l++) {
                        if (matrixBinary[i + k][j + l] == template[k][l]) {
                            blackCount2 += 1;
                        }
                    }
                }

                for (int k = s - 1; k < templateSize; k++) {
                    for (int l = s - 1; l < templateSize; l++) {
                        if (matrixBinary[i + k][j + l] == template[k][l]) {
                            blackCount3 += 1;
                        }
                    }
                }

                for (int k = s - 1; k < templateSize; k++) {
                    for (int l = 0; l < s; l++) {
                        if (matrixBinary[i + k][j + l] == template[k][l]) {
                            blackCount4 += 1;
                        }
                    }
                }

                //  Log.d(TAG, "BlackCount 1: " + blackCount1 + " BlackCount 2: " + blackCount2 + " BlackCount 3: " + blackCount3 + " BlackCount 4: " + blackCount4);
                if (blackCount1 > 150 && blackCount2 > 150 && blackCount3 > 150 && blackCount4 > 150) {
                    found = true;
                    break mainLoop;
                }
            }
        }

        Log.d(TAG, "Encontrou gabarito: " + found);
        return found;
    }

}
