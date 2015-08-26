package br.furb.corpusmapping.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.widget.ImageView;

/**
 * Created by Janaina on 25/08/2015.
 */
public class ImageDrawer {

    public static Bitmap drawPoint(Bitmap bitmap, PointF point) {

        Bitmap workingBitmap = Bitmap.createBitmap(bitmap);
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(mutableBitmap);

        Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(Color.BLUE);
        paintText.setTextSize(12);
        paintText.setStyle(Paint.Style.FILL);
        paintText.setFakeBoldText(true);

        paintText.setShadowLayer(30f, 10f, 10f, Color.BLACK);

        canvas.drawText("@", point.x, point.y, paintText);

        return mutableBitmap;
    }

}