package br.furb.imagezoom;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements OnTouchListener{
    private static final String TAG = "Touch";

    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    private List<BBox> bbox;

    private Bitmap bitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView view = (ImageView) findViewById(R.id.imageView);
       // view.setScaleType(ImageView.ScaleType.FIT_CENTER); // make the image fit to the center.
        view.setOnTouchListener(this);

        bbox = new ArrayList<>();

        bbox.add(new BBox(50,250,230,370));// orelha p/ baixo
        bbox.add(new BBox(10,160,275,250)); // orelhas
        bbox.add(new BBox(20,15,260,250)); // testa
    }

    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        // make the image scalable as a matrix
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN: //first finger down only
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG" );
                mode = DRAG;
                break;
            case MotionEvent.ACTION_UP: //first finger lifted
            case MotionEvent.ACTION_POINTER_UP: //second finger lifted

                if(mode == DRAG){
                    Log.d(TAG, "event = " + event.getX() + " - " + event.getY());
                   // Log.d(TAG, "view = " + view.getX() + " - " + view.getY());
                    //Log.d(TAG, "" + m);


                    // Jana
                    Matrix inverse = new Matrix();
                    view.getImageMatrix().invert(inverse);

                    // map touch point from ImageView to image
                    float[] touchPoint = new float[] {event.getX(), event.getY()};
                    inverse.mapPoints(touchPoint);
                    Log.d(TAG, "inverse = " + touchPoint[0] + " - " + touchPoint[1]);

                    float x = touchPoint[0];
                    float y = touchPoint[1];

                    boolean inner = false;
                    for(BBox box : bbox){
                        if(box.isInner(x, y)){
                            inner = true;
                            break;
                        }
                    }

                    if(inner){
                        drawPoint(view, new PointF(x,y));
                    }

                  //  Toast.makeText(this, inner ? "Dentro" : "Fora", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, inner ? "Dentro" : "Fora" + touchPoint[0] + " - " + touchPoint[1]);

                    // ----
                }
                mode = NONE;
                Log.d(TAG, "mode=NONE");

                break;
            case MotionEvent.ACTION_POINTER_DOWN: //second finger down
                oldDist = spacing(event); // calculates the distance between two points where user touched.
                Log.d(TAG, "oldDist=" + oldDist);
                // minimal distance between both the fingers
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event); // sets the mid-point of the straight line between two points where user touched.
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM" );
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG)
                { //movement of first finger
                    matrix.set(savedMatrix);
                    if (view.getLeft() >= -392)
                    {
                        matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                    }
                }
                else if (mode == ZOOM) { //pinch zooming
                    float newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 5f ) {
                        matrix.set(savedMatrix);
                        scale = newDist/oldDist; //thinking I need to play around with this value to limit it**
                        Log.d(TAG, "scale=" + scale);
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        // Perform the transformation
        view.setImageMatrix(matrix);

        return true; // indicate event was handled
    }

    private void drawPoint(ImageView display, PointF point){

        if(bitmap == null){
            bitmap = BitmapFactory.decodeResource(display.getResources(), R.drawable.cabeca_frente);
        }

        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setTextSize(10);
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        Bitmap workingBitmap = Bitmap.createBitmap(bitmap);
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(mutableBitmap);

        Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(Color.BLUE);
        paintText.setTextSize(12);
        paintText.setStyle(Paint.Style.FILL);
        paintText.setFakeBoldText(true);

        paintText.setShadowLayer(30f, 10f, 10f, Color.BLACK);

       /* selImagens = new ExListViewController();
        String imgpath = new MelanomaPicsSaveControl().setImagePath();
        // replica os pontos do bitmap

        File[] list = new File[selImagens.devolveQtdTotal(CreateListFiles())];
        list = selImagens.devolveListaSelecionados(imgpath, CreateListFiles());
*/
        //for (int y = 0; y < selImagens.devolveQtd(imgpath, CreateListFiles()); y++) {
            Rect rectText = new Rect();
            paintText.getTextBounds("#", 0, 1, rectText);
           // int ajustePosx = (int) (selImagens.getXArquivo(list[y].getName()) / 4.65);
           // int ajustePosy = (int) (selImagens.getYArquivo(list[y].getName()) / 4.4);

            canvas.drawText("@", point.x, point.y, paintText);
       // }
        display.setAdjustViewBounds(false);
        display.setImageBitmap(mutableBitmap);

        bitmap = mutableBitmap;
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
