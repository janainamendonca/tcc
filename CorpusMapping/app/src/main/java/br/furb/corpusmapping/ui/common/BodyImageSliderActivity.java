package br.furb.corpusmapping.ui.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import br.furb.corpusmapping.ui.main.CorpusMappingApp;
import br.furb.corpusmapping.R;
import br.furb.corpusmapping.data.model.SpecificBodyPart;
import br.furb.corpusmapping.data.model.ImageRecord;
import br.furb.corpusmapping.data.database.ImageRecordRepository;
import br.furb.corpusmapping.data.model.PointF;
import br.furb.corpusmapping.util.BodyImagesUtil;
import br.furb.corpusmapping.util.ImageDrawer;
import br.furb.corpusmapping.ui.view.ViewMolesTouchListener;

/**
 * Activity para navegação pelas partes do corpo.
 *
 * @author Janaina Carraro Mendonça Lima
 */
public class BodyImageSliderActivity extends FragmentActivity {

    public static final String PARAM_IMAGES = "images";
    public static final String PARAM_CURRENT = "current";
    public static final String PARAM_BODY_PARTS = "bodyParts";
    public static final String PARAM_IS_TO_ASSOCIATE = "isToAssociate";
    ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    ViewPager viewPager;
    private int[] images;
    private String[] bodyParts;
    private int numItems;
    private boolean isToAssociate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);
        images = getIntent().getIntArrayExtra(PARAM_IMAGES);
        bodyParts = getIntent().getStringArrayExtra(PARAM_BODY_PARTS);
        isToAssociate = getIntent().getBooleanExtra(PARAM_IS_TO_ASSOCIATE, true);
        int current = getIntent().getIntExtra(PARAM_CURRENT, 0);
        numItems = images.length;
        imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(imageFragmentPagerAdapter);
        viewPager.setCurrentItem(current);
    }

    private class ImageFragmentPagerAdapter extends FragmentPagerAdapter {
        public ImageFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return numItems;
        }

        @Override
        public Fragment getItem(int position) {
            return SwipeFragment.newInstance(position, images, bodyParts, isToAssociate);
        }
    }

    public static class SwipeFragment extends Fragment {

        private Bitmap bitmap;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Bundle bundle = getArguments();
            int position = bundle.getInt("position");
            int[] images = bundle.getIntArray(PARAM_IMAGES);
            String[] parts = bundle.getStringArray(PARAM_BODY_PARTS);
            boolean isToAssociate = bundle.getBoolean(PARAM_IS_TO_ASSOCIATE, true);
            SpecificBodyPart[] bodyParts = convertBodyParts(parts);
            View swipeView = inflater.inflate(R.layout.fragment_image_slider, container, false);
            ImageView imageView = (ImageView) swipeView.findViewById(R.id.imageView);

            int resource = images[position];

            bitmap = BitmapFactory.decodeResource(swipeView.getResources(), resource);
            List<ImageRecord> imageRecords = ImageRecordRepository.getInstance(getActivity()).getByBodyPartId(CorpusMappingApp.getInstance().getSelectedPatientId(), bodyParts[position]);

            List<PointF> listPoints = new ArrayList<>();
            for (ImageRecord i : imageRecords) {
                listPoints.add(i.getMoleGroup().getPosition());
            }
            bitmap = ImageDrawer.drawPoint(bitmap, listPoints.toArray(new PointF[listPoints.size()]));
            imageView.setImageBitmap(bitmap);
            /**/

            if (isToAssociate) {
                AssociateBodyPartTouchListener listener = new AssociateBodyPartTouchListener(getActivity(), resource, bodyParts[position], bitmap, BodyImagesUtil.getBoundingBoxForImage(resource));
                listener.setMoveEnabled(false);
                listener.setZoomEnabled(false);
                imageView.setOnTouchListener(listener);
            } else {
                ViewMolesTouchListener listener = new ViewMolesTouchListener(getActivity(), resource, bodyParts[position], bitmap, BodyImagesUtil.getBoundingBoxForImage(resource));
                listener.setMoveEnabled(false);
                listener.setZoomEnabled(false);
                imageView.setOnTouchListener(listener);
            }

            return swipeView;
        }

        static SwipeFragment newInstance(int position, int[] images, String[] bodyParts, boolean isToAssociate) {
            SwipeFragment swipeFragment = new SwipeFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            bundle.putIntArray(PARAM_IMAGES, images);
            bundle.putStringArray(PARAM_BODY_PARTS, bodyParts);
            bundle.putBoolean(PARAM_IS_TO_ASSOCIATE, isToAssociate);
            swipeFragment.setArguments(bundle);
            return swipeFragment;
        }
    }

    private static SpecificBodyPart[] convertBodyParts(String[] parts) {
        SpecificBodyPart[] bodyParts = new SpecificBodyPart[parts.length];

        for (int i = 0; i < parts.length; i++) {
            bodyParts[i] = SpecificBodyPart.valueOf(parts[i]);
        }
        return bodyParts;
    }


}
