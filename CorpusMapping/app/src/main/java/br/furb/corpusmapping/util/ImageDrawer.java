package br.furb.corpusmapping.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import br.furb.corpusmapping.ui.main.CorpusMappingApp;
import br.furb.corpusmapping.data.model.SpecificBodyPart;
import br.furb.corpusmapping.data.model.ImageRecord;
import br.furb.corpusmapping.data.database.ImageRecordRepository;
import br.furb.corpusmapping.data.model.PointF;

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
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(mutableBitmap);

        Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(Color.BLUE);
        paintText.setTextSize(12);
        paintText.setStyle(Paint.Style.FILL);
        paintText.setFakeBoldText(true);

        for (PointF p : point) {
            canvas.drawText("@", p.x, p.y, paintText);
        }

        return mutableBitmap;
    }

    public static void drawPointsOfBodyPart(ImageView view, SpecificBodyPart bodyPart, int resourceId) {
        Bitmap bitmap = BitmapFactory.decodeResource(view.getResources(), resourceId);
        CorpusMappingApp app = CorpusMappingApp.getInstance();
        long patientId = app.getSelectedPatientId();
        ImageRecordRepository repository = getImageRecordRepository(view);
        List<ImageRecord> imageRecords = repository.getByBodyPartId(patientId, bodyPart);

        List<PointF> listPoints = new ArrayList<>();
        for (ImageRecord i : imageRecords) {
            PointF position = i.getMoleGroup().getPosition();
            if (!listPoints.contains(position)) {
                listPoints.add(position);
            }
        }
        if (listPoints.size() > 0) {
            PointF[] points = listPoints.toArray(new PointF[listPoints.size()]);
            bitmap = ImageDrawer.drawPoint(bitmap, points);
        }
        view.setImageBitmap(bitmap);
    }

    private static ImageRecordRepository getImageRecordRepository(ImageView view) {
        return ImageRecordRepository.getInstance(view.getContext());
    }

}
