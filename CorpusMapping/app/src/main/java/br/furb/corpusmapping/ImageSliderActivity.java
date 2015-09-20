package br.furb.corpusmapping;

import android.graphics.Bitmap;
import android.net.Uri;
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
import android.widget.TextView;

import br.furb.corpusmapping.data.ImageRecord;
import br.furb.corpusmapping.util.ImageUtils;


public class ImageSliderActivity extends FragmentActivity {

    public static final String PARAM_IMAGES = "images";
    public static final String PARAM_SELECTED_IMAGE = "selected-image";
    ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    ViewPager viewPager;
    //public static final String[] IMAGE_NAME = {"eagle", "horse", "bonobo", "wolf", "owl", "bear",};
    private ImageRecord[] images;
    private int numItems;
    private int selectedImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mole_image_slider);
        images = (ImageRecord[]) getIntent().getSerializableExtra(PARAM_IMAGES);
        selectedImage = getIntent().getIntExtra(PARAM_SELECTED_IMAGE, 0);


        numItems = images.length;

        imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(imageFragmentPagerAdapter);
        viewPager.setCurrentItem(selectedImage);

        /*
        TextView txtMole = (TextView) findViewById(R.id.txtMole);
        txtMole.setText(images[0].getMoleGroup().getGroupName());
        ImageView imgCla = (ImageView) findViewById(R.id.imgClassification);
        imgCla.setImageResource(R.drawable.ic_class_red); //TODO*/

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
            SwipeFragment fragment = new SwipeFragment();
            return SwipeFragment.newInstance(position, images);
        }
    }

    public static class SwipeFragment extends Fragment {

        private Bitmap bitmap;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Bundle bundle = getArguments();
            int position = bundle.getInt("position");
            ImageRecord[] images = (ImageRecord[]) bundle.getSerializable(PARAM_IMAGES);

            ImageRecord imageRecord = images[position];

            View swipeView = inflater.inflate(R.layout.fragment_detail_mole_image_slider, container, false);
            ImageView imageView = (ImageView) swipeView.findViewById(R.id.imageView);

            TextView txtDate = (TextView)swipeView.findViewById(R.id.txtDate);
            TextView txtAnnotations = (TextView)swipeView.findViewById(R.id.txtAnnotations);

            txtDate.setText(imageRecord.getImageDateAsString());
            txtAnnotations.setText(imageRecord.getAnnotations());

            Uri imageUri = ImageUtils.getImageUri(imageRecord.getImagePath());
            if (imageUri == null) {
                imageView.setImageResource(R.drawable.ic_images25);
            } else {
                imageView.setImageBitmap(ImageUtils.decodeSampledBitmapFromResource(getActivity(), imageUri, 200, 200));
            }

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
