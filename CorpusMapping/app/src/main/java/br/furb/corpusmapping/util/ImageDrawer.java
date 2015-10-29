package br.furb.corpusmapping.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import br.furb.corpusmapping.CorpusMappingApp;
import br.furb.corpusmapping.SpecificBodyPart;
import br.furb.corpusmapping.data.ImageRecord;
import br.furb.corpusmapping.data.ImageRecordRepository;
import br.furb.corpusmapping.data.PointF;

/**
 * Created by Janaina on 25/08/2015.
 */
public class ImageDrawer {

    public static void drawPoint(ImageView view, int resourceId, PointF... point) {
        Bitmap bitmap = BitmapFactory.decodeResource(view.getResources(), resourceId);
        bitmap = drawPoint(bitmap, point);
        view.setImageBitmap(bitmap);
    }
    public static Bitmap drawPoint(Bitmap bitmap, PointF... point) {

        Bitmap workingBitmap = Bitmap.createBitmap(bitmap);
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(mutableBitmap);

        Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(Color.BLUE);
        paintText.setTextSize(12);
        paintText.setStyle(Paint.Style.FILL);
        paintText.setFakeBoldText(true);

        for (PointF p : point){
            canvas.drawText("@", p.x, p.y, paintText);
        }

        return mutableBitmap;
    }

    public static void drawPoints(ImageView view, SpecificBodyPart bodyPart, int resourceId){
        Bitmap bitmap = BitmapFactory.decodeResource(view.getResources(), resourceId);
        long patientId = CorpusMappingApp.getInstance().getSelectedPatientId();
        ImageRecordRepository imageRecordRepository = ImageRecordRepository.getInstance(view.getContext());
        List<ImageRecord> imageRecords = imageRecordRepository.getByBodyPartId(patientId, bodyPart);

        List<PointF> listPoints = new ArrayList<>();
        for (ImageRecord i : imageRecords) {
            listPoints.add(i.getMoleGroup().getPosition());
        }
        bitmap = ImageDrawer.drawPoint(bitmap, listPoints.toArray(new PointF[listPoints.size()]));
        view.setImageBitmap(bitmap);
    }

}
