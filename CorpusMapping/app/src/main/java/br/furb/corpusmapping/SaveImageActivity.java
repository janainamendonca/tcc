package br.furb.corpusmapping;

import android.content.Intent;
import android.graphics.PointF;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import br.furb.corpusmapping.util.BoundingBox;

public class SaveImageActivity extends ActionBarActivity implements View.OnClickListener {

    private ImageView imgBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_image);

        imgBody = (ImageView) findViewById(R.id.imgBody);
        imgBody.setOnTouchListener(new BodyTouchListener(this));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBody:
                //TODO
                Intent i = new Intent(this, SelectBodyPartActivity.class);
                i.putExtra(SelectBodyPartActivity.PARAM_BODY_PART, BodyPart.HEAD.name());
                startActivity(i);

                break;
        }
    }

    private static class BodyTouchListener extends ImageBoundingBoxTouchListener {

        private static BoundingBox headBBox = new BoundingBox(1, 0, 0, 400, 125);
        private static BoundingBox shoulderBBox = new BoundingBox(2, 0, 124, 400, 200);
        private static BoundingBox leftArmBBox = new BoundingBox(3, 250, 199, 400, 390);
        private static BoundingBox rightArmBBox = new BoundingBox(4, 0, 199, 150, 390);
        private static BoundingBox bodyBBox = new BoundingBox(5, 151, 124, 249, 390);
        private static BoundingBox leftLegBBox = new BoundingBox(6, 200, 391, 400, 700);
        private static BoundingBox rightLegBBox = new BoundingBox(7, 0, 391, 200, 700);

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
                activity.startActivity(i);
            }
        }
    }
}
