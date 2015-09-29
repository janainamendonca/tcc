package br.furb.corpusmapping.view;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import br.furb.corpusmapping.R;
import br.furb.corpusmapping.SpecificBodyPart;
import br.furb.corpusmapping.data.ImageRecord;
import br.furb.corpusmapping.data.MoleGroup;
import br.furb.corpusmapping.util.BodyPartDialog;
import br.furb.corpusmapping.util.ImageDrawer;
import br.furb.corpusmapping.util.ImageUtils;

import static br.furb.corpusmapping.util.MoleClassificationDialog.show;


public class MoleDetailedImageSliderActivity extends ActionBarActivity implements View.OnClickListener {

    public static final String PARAM_IMAGES = "images";
    public static final String PARAM_SELECTED_IMAGE = "selected-image";
    ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    ViewPager viewPager;
    private ImageRecord[] images;
    private int numItems;
    private MoleGroup moleGroup;
    private ImageView imgClassification;
    private SpecificBodyPart bodyPart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mole_image_slider);
        images = (ImageRecord[]) getIntent().getSerializableExtra(PARAM_IMAGES);
        int selectedImage = getIntent().getIntExtra(PARAM_SELECTED_IMAGE, 0);
        numItems = images.length;
        imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(imageFragmentPagerAdapter);
        viewPager.setCurrentItem(selectedImage);

        TextView txtMole = (TextView) findViewById(R.id.txtMole);
        ImageRecord imageRecord = images[0];
        moleGroup = imageRecord.getMoleGroup();
        txtMole.setText(moleGroup.getGroupName());
        imgClassification = (ImageView) findViewById(R.id.imgClassification);

        if (moleGroup.getClassification() != null) {
            imgClassification.setImageResource(moleGroup.getClassification().getResource());
        }

        ImageView imgBodyPart = (ImageView) findViewById(R.id.imgBodyPart);
        bodyPart = imageRecord.getBodyPart();
        imgBodyPart.setImageResource(bodyPart.getResource());
        ImageDrawer.drawPoint(imgBodyPart, bodyPart.getResource(), moleGroup.getPosition());

        imgClassification.setOnClickListener(this);
        imgBodyPart.setOnClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_images, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgClassification) {
            show(this, moleGroup, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    imgClassification.setImageResource(moleGroup.getClassification().getResource());
                }
            });
        } else if (v.getId() == R.id.imgBodyPart) {
            BodyPartDialog.show(this, bodyPart, moleGroup);
        }
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
            return SwipeFragment.newInstance(position, images);
        }
    }

    public static class SwipeFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Bundle bundle = getArguments();
            int position = bundle.getInt("position");
            ImageRecord[] images = (ImageRecord[]) bundle.getSerializable(PARAM_IMAGES);

            ImageRecord imageRecord = images[position];

            View swipeView = inflater.inflate(R.layout.fragment_detail_mole_image_slider, container, false);
            ImageView imageView = (ImageView) swipeView.findViewById(R.id.imageView);

            TextView txtDate = (TextView) swipeView.findViewById(R.id.txtDate);
            TextView txtAnnotations = (TextView) swipeView.findViewById(R.id.txtAnnotations);

            txtDate.setText(imageRecord.getImageDateAsString());
            txtAnnotations.setText(imageRecord.getAnnotations());

            Uri imageUri = ImageUtils.getImageUri(imageRecord.getImagePath());
            if (imageUri == null) {
                imageView.setImageResource(R.drawable.ic_images25);
            } else {
                imageView.setImageBitmap(ImageUtils.decodeSampledBitmapFromResource(getActivity(), imageUri, 200, 200));
            }

            // ImageBoundingBoxTouchListener touchListener = new ImageBoundingBoxTouchListener();
            //  imageView.setOnTouchListener(touchListener);

            return swipeView;
        }

        static SwipeFragment newInstance(int position, ImageRecord[] images) {
            SwipeFragment swipeFragment = new SwipeFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            bundle.putSerializable(PARAM_IMAGES, images);
            swipeFragment.setArguments(bundle);
            return swipeFragment;
        }
    }


}
