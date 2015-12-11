package br.furb.corpusmapping.ui.bodylink;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import org.joda.time.LocalDateTime;

import java.util.List;

import br.furb.corpusmapping.data.model.BodyPart;
import br.furb.corpusmapping.ui.common.AssociateBodyPartTouchListener;
import br.furb.corpusmapping.ui.common.ImageBoundingBoxTouchListener;
import br.furb.corpusmapping.data.model.ImageType;
import br.furb.corpusmapping.R;
import br.furb.corpusmapping.data.model.SpecificBodyPart;
import br.furb.corpusmapping.data.model.ImageRecord;
import br.furb.corpusmapping.data.database.ImageRecordRepository;
import br.furb.corpusmapping.data.model.MoleClassification;
import br.furb.corpusmapping.data.model.MoleGroup;
import br.furb.corpusmapping.data.model.PointF;
import br.furb.corpusmapping.util.BoundingBox;

/**
 * Activity que apresenta as regiões do corpo para o usuário escolher com qual parte do corpo ele quer associar a imagem que foi capturada.
 * @author Janaina  Carraro Mendonça Lima
 */
public class SaveImageActivity extends ActionBarActivity {

    public static final String IMAGE_SHORT_PATH = "imageShortPath";
    public static final String PATIENT_ID = "patientId";

    private ImageView imgBody;
    private String imagePath;
    private ImageRecordRepository imageRecordRepository;
    private long patientId;

    private static BoundingBox headBBox = new BoundingBox(1, 0, 0, 400, 125);
    private static BoundingBox shoulderBBox = new BoundingBox(2, 0, 124, 400, 200);
    private static BoundingBox leftArmBBox = new BoundingBox(3, 250, 199, 400, 390);
    private static BoundingBox rightArmBBox = new BoundingBox(4, 0, 199, 150, 390);
    private static BoundingBox bodyBBox = new BoundingBox(5, 151, 124, 249, 390);
    private static BoundingBox leftLegBBox = new BoundingBox(6, 200, 391, 400, 700);
    private static BoundingBox rightLegBBox = new BoundingBox(7, 0, 391, 200, 700);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_image);

        imgBody = (ImageView) findViewById(R.id.imgBody);
        imgBody.setOnTouchListener(new BodyTouchListener(this));

        imagePath = getIntent().getStringExtra(IMAGE_SHORT_PATH);
        patientId = getIntent().getLongExtra(PATIENT_ID, -1);
        imageRecordRepository = ImageRecordRepository.getInstance(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

private class BodyTouchListener extends ImageBoundingBoxTouchListener {


    private SaveImageActivity activity;

    private BodyTouchListener(SaveImageActivity activity) {
        super(headBBox, shoulderBBox, leftArmBBox, rightArmBBox, bodyBBox, leftLegBBox, rightLegBBox);
        this.activity = activity;
        setMoveEnabled(false);
        setZoomEnabled(false);
    }

    @Override
    public void onClickInnerBoundingBox(ImageView view, PointF touchPoint, int boundingBoxId) {
        BodyPart bodyPart = null;

        if (boundingBoxId == headBBox.id) {
            bodyPart = BodyPart.HEAD;
        } else if (boundingBoxId == shoulderBBox.id || boundingBoxId == bodyBBox.id) {
            bodyPart = BodyPart.BODY;
        } else if (boundingBoxId == leftArmBBox.id) {
            bodyPart = BodyPart.LEFT_ARM;
        } else if (boundingBoxId == rightArmBBox.id) {
            bodyPart = BodyPart.RIGHT_ARM;
        } else if (boundingBoxId == leftLegBBox.id) {
            bodyPart = BodyPart.LEFT_LEG;
        } else if (boundingBoxId == rightLegBBox.id) {
            bodyPart = BodyPart.RIGHT_LEG;
        }

        Log.d("Part", bodyPart != null ? bodyPart.name() : "");

        if (bodyPart != null) {
            Intent i = new Intent(activity, SelectBodyPartActivity.class);
            i.putExtra(SelectBodyPartActivity.PARAM_BODY_PART, bodyPart.name());
            activity.startActivityForResult(i, SelectBodyPartActivity.REQUEST_CODE);
        }
    }
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SelectBodyPartActivity.REQUEST_CODE) {

            if (resultCode == AssociateBodyPartTouchListener.RESULT_CODE_OK) {
                String groupName = data.getStringExtra("groupName");
                String annotation = data.getStringExtra("annotation");
                PointF position = (PointF) data.getSerializableExtra("position");
                SpecificBodyPart bodyPart = SpecificBodyPart.valueOf(data.getStringExtra("bodyPart"));
                MoleClassification classification = MoleClassification.valueOf(data.getStringExtra("classification"));

                ImageRecord imageRecord = new ImageRecord();
                imageRecord.setImageDate(LocalDateTime.now());
                imageRecord.setPosition(position);
                imageRecord.setImagePath(imagePath);
                imageRecord.setImageType(ImageType.LOCAL);
                imageRecord.setBodyPart(bodyPart);
                imageRecord.setAnnotations(annotation);

                List<ImageRecord> found = imageRecordRepository.getByBodyPartAndPosition(patientId, bodyPart, position);
                MoleGroup moleGroup = found.isEmpty() ? null : found.get(0).getMoleGroup();

                if (moleGroup == null) {
                    moleGroup = new MoleGroup();
                    moleGroup.setPosition(position);
                    moleGroup.setPatientId(patientId);
                }
                moleGroup.setGroupName(groupName);
                moleGroup.setClassification(classification);

                imageRecord.setMoleGroup(moleGroup);

                imageRecord.setPatientId(patientId);
                imageRecordRepository.save(imageRecord);

                finish();
            }

        }
    }
}
