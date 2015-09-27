package br.furb.corpusmapping;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.List;

import br.furb.corpusmapping.data.ImageRecord;
import br.furb.corpusmapping.data.ImageRecordRepository;
import br.furb.corpusmapping.data.MoleGroup;
import br.furb.corpusmapping.data.PointF;
import br.furb.corpusmapping.util.BoundingBox;
import br.furb.corpusmapping.util.ImageDrawer;

/**
 * Created by Janaina on 25/08/2015.
 */
public class AssociateBodyPartTouchListener extends ImageBoundingBoxTouchListener {

    private Bitmap bitmap;
    private Activity activity;
    private int resourceId;
    private final SpecificBodyPart bodyPart;

    public static final int RESULT_CODE_OK = 1;

    public AssociateBodyPartTouchListener(Activity activity, int resourceId, SpecificBodyPart bodyPart, Bitmap bitmap, BoundingBox... boundingBoxes) {
        super(boundingBoxes);
        this.activity = activity;
        this.resourceId = resourceId;
        this.bodyPart = bodyPart;
        this.bitmap = bitmap;
    }

    @Override
    public void onClickInnerBoundingBox(final ImageView view, final PointF touchPoint, int boundingBoxId) {
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(view.getResources(), resourceId);
        }
        List<ImageRecord> imageRecord = ImageRecordRepository.getInstance(activity).getByBodyPartAndPosition(CorpusMappingApp.getInstance().getSelectedPatientId(), bodyPart, touchPoint);
        MoleGroup moleGroup = imageRecord.isEmpty() ? null : imageRecord.get(0).getMoleGroup();

        if (moleGroup == null) {
            Bitmap newBitmap = ImageDrawer.drawPoint(bitmap, touchPoint);
            view.setImageBitmap(newBitmap);
            view.setAdjustViewBounds(false);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle("Confirma a localização da pinta?");
        LayoutInflater inflater = activity.getLayoutInflater();
        View view1 = inflater.inflate(R.layout.dialog_save_mole, null);
        builder.setView(view1);

        final EditText edtGroupName = (EditText) view1.findViewById(R.id.edtGroupName);
        final EditText edtAnnotation = (EditText) view1.findViewById(R.id.edtAnnotation);


        if (moleGroup != null) {
            edtGroupName.setText(moleGroup.getGroupName());
            edtAnnotation.setText(moleGroup.getAnnotations());
        } else {
            //sugerir um nome para o grupo
            edtGroupName.setText(bodyPart.getBodyPartName());
        }

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent data = new Intent();
                data.putExtra("groupName", edtGroupName.getText().toString());
                data.putExtra("annotation", edtAnnotation.getText().toString());
                data.putExtra("position", touchPoint);
                data.putExtra("bodyPart", bodyPart.toString());
                activity.setResult(RESULT_CODE_OK, data);
                activity.finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                view.setImageBitmap(bitmap);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
