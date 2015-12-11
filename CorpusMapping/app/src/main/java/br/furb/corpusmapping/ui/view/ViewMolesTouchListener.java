package br.furb.corpusmapping.ui.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.ImageView;

import java.util.List;

import br.furb.corpusmapping.ui.main.CorpusMappingApp;
import br.furb.corpusmapping.ui.common.ImageBoundingBoxTouchListener;
import br.furb.corpusmapping.data.model.SpecificBodyPart;
import br.furb.corpusmapping.data.model.ImageRecord;
import br.furb.corpusmapping.data.database.ImageRecordRepository;
import br.furb.corpusmapping.data.model.PointF;
import br.furb.corpusmapping.util.BoundingBox;

import static br.furb.corpusmapping.ui.view.MoleImageSliderActivity.PARAM_IMAGES;

/**
 * Listener para quando uma pinta é selecionada na imagem da parte do corpo.
 * Apresenta uma activity com todas as imagens associadas a pinta selecionada.
 *
 * @author Janaina Carraro Mendonça Lima
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
CorpusMappingApp app = CorpusMappingApp.getInstance();
long patientId = app.getSelectedPatientId();
ImageRecordRepository repository =
                ImageRecordRepository.getInstance(activity);

List<ImageRecord> imageRecords =
            repository.getByBodyPartAndPosition(patientId, bodyPart, touchPoint);

if (!imageRecords.isEmpty()) {
    Intent i = new Intent(activity, MoleImageSliderActivity.class);
    i.putExtra(PARAM_IMAGES,
            imageRecords.toArray(new ImageRecord[imageRecords.size()]));
    activity.startActivity(i);
}
    }
}
