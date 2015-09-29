package br.furb.corpusmapping.common;

import android.graphics.Matrix;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.Arrays;
import java.util.List;

import br.furb.corpusmapping.data.PointF;
import br.furb.corpusmapping.util.BoundingBox;

/**
 * Created by Janaina on 25/08/2015.
 */
public class ImageBoundingBoxTouchListener implements View.OnTouchListener {
    private static final String TAG = "Touch";

    // Matrizes utilizadas para mover e dar zoom na imagem
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    PointF startPoint = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    private List<BoundingBox> bbox;

    private boolean zoomEnabled;

    private boolean moveEnabled;

    public ImageBoundingBoxTouchListener(BoundingBox... boundingBoxes) {
        this.bbox = Arrays.asList(boundingBoxes);
        this.zoomEnabled = true;
        this.moveEnabled = true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        ImageView view = (ImageView) v;
        // define o tipo de escala da imagem como Matriz
        view.setScaleType(ImageView.ScaleType.MATRIX);

        float scale;

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN: //primeiro dedo
                savedMatrix.set(matrix);
                startPoint.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG");
                mode = DRAG;
                break;
            case MotionEvent.ACTION_UP: // primeiro dedo levantado
            case MotionEvent.ACTION_POINTER_UP: // segundo dedo levantado

                if (mode == DRAG) {
                    Log.d(TAG, "event = " + event.getX() + " - " + event.getY());
                    onSimpleTouch(event, view);
                }
                mode = NONE;
                Log.d(TAG, "mode=NONE");

                break;
            case MotionEvent.ACTION_POINTER_DOWN: //segundo dedo
                oldDist = spacing(event); // calcula a distância entre os dois pontos
                Log.d(TAG, "oldDist=" + oldDist);
                // distancia minima entre os dedos
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event); // define o centro dos pontos de toque
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) { //movimento do 1º dedo: translada a imagem
                    if (isMoveEnabled()) {
                        matrix.set(savedMatrix);
                        if (view.getLeft() >= -392) {
                            matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
                        }

                        // Aplica a transformação
                        view.setImageMatrix(matrix);
                    }
                } else if (mode == ZOOM) { //

                    if (isZoomEnabled()) {

                        float newDist = spacing(event);
                        Log.d(TAG, "newDist=" + newDist);
                        if (newDist > 5f) {
                            matrix.set(savedMatrix);
                            scale = newDist / oldDist;
                            Log.d(TAG, "scale=" + scale);
                            matrix.postScale(scale, scale, mid.x, mid.y);
                        }

                        // Aplica a transformação
                        view.setImageMatrix(matrix);
                    }
                }
                break;
        }


        return true;
    }

    private void onSimpleTouch(MotionEvent event, ImageView view) {
        // mapeamento do ponto de toque da ImageView para a imagem
        Matrix inverse = new Matrix();
        view.getImageMatrix().invert(inverse);
        float[] touchPoint = new float[]{event.getX(), event.getY()};
        inverse.mapPoints(touchPoint);
        Log.d(TAG, "inverse = " + touchPoint[0] + " - " + touchPoint[1]);

        float x = touchPoint[0];
        float y = touchPoint[1];

        boolean inner = bbox.isEmpty(); // se não tiver bouding box, considera sempre 'dentro'
        int bboxId = -1;
        for (BoundingBox box : bbox) {
            if (box.isInner(x, y)) {
                inner = true;
                bboxId = box.id;
                break;
            }
        }

        if (inner) {
            onClickInnerBoundingBox(view, new PointF(x, y), bboxId);
        }

        Log.d(TAG, inner ? "Dentro" : "Fora" + x + " - " + y);
    }

    public void onClickInnerBoundingBox(ImageView view, PointF touchPoint, int boundingBoxId) {
        // por padrão não faz nada
        // subclasses devem implementar
    }

    public void setZoomEnabled(boolean zoomEnabled) {
        this.zoomEnabled = zoomEnabled;
    }

    public boolean isZoomEnabled() {
        return zoomEnabled;
    }

    public void setMoveEnabled(boolean moveEnabled) {
        this.moveEnabled = moveEnabled;
    }

    public boolean isMoveEnabled() {
        return moveEnabled;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }


}
