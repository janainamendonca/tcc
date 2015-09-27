package br.furb.corpusmapping;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.ImageView;

import java.util.List;

import br.furb.corpusmapping.data.ImageRecord;
import br.furb.corpusmapping.data.ImageRecordRepository;
import br.furb.corpusmapping.data.PointF;
import br.furb.corpusmapping.util.BoundingBox;

/**
 * Created by Janaina on 25/08/2015.
 */
public class ViewMolesTouchListener extends ImageBoundingBoxTouchListener {

    private Activity activity;
    private final SpecificBodyPart bodyPart;

    public static final int RESULT_CODE_OK = 1;

    public ViewMolesTouchListener(Activity activity, int resourceId, SpecificBodyPart bodyPart, Bitmap bitmap, BoundingBox... boundingBoxes) {
        super(boundingBoxes);
        this.activity = activity;
        this.bodyPart = bodyPart;
    }

    @Override
    public void onClickInnerBoundingBox(final ImageView view, final PointF touchPoint, int boundingBoxId) {
        long patientId = CorpusMappingApp.getInstance().getSelectedPatientId();
        List<ImageRecord> imageRecords = ImageRecordRepository.getInstance(activity).getByBodyPartAndPosition(patientId, bodyPart, touchPoint);

        if (!imageRecords.isEmpty()) {

            Intent i = new Intent(activity, MoleImageSliderActivity.class);
            i.putExtra(MoleImageSliderActivity.PARAM_IMAGES, imageRecords.toArray(new ImageRecord[imageRecords.size()]));
            activity.startActivity(i);
        }
    }
}
