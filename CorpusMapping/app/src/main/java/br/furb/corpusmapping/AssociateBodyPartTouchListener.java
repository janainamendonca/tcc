package br.furb.corpusmapping;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.widget.ImageView;

import br.furb.corpusmapping.util.BBox;
import br.furb.corpusmapping.util.ImageDrawer;

/**
 * Created by Janaina on 25/08/2015.
 */
public class AssociateBodyPartTouchListener extends ImageBoundingBoxTouchListener {

    private Bitmap bitmap;
    private int resourceId;

    public AssociateBodyPartTouchListener(int resourceId, BBox... boundingBoxes) {
        super(boundingBoxes);
        this.resourceId = resourceId;
    }

    @Override
    public void onClickInnerBoundingBox(ImageView view, PointF touchPoint) {
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(view.getResources(), resourceId);
        }
        bitmap = ImageDrawer.drawPoint(bitmap, touchPoint);

        view.setImageBitmap(bitmap);
        view.setAdjustViewBounds(false);
    }
}
