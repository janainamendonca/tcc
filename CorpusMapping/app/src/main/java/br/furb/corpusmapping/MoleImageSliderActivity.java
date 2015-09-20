package br.furb.corpusmapping;

import android.content.Intent;
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


public class MoleImageSliderActivity extends FragmentActivity {

    public static final String PARAM_IMAGES = "images";
    ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    ViewPager viewPager;
    //public static final String[] IMAGE_NAME = {"eagle", "horse", "bonobo", "wolf", "owl", "bear",};
    private ImageRecord[] images;
    private int numItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mole_image_slider);
        images = (ImageRecord[]) getIntent().getSerializableExtra(PARAM_IMAGES);

        numItems = images.length;

        imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.pager);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            boolean alteradoListener = false;

            public void onPageSelected(int position) {
                int pageCount = numItems;
                if (position == 0 && !alteradoListener) {
                    viewPager.setCurrentItem(pageCount - 1, false);
                } else if (position == pageCount - 1) {
                    // viewPager.setCurrentItem(1, false);
                    alteradoListener = true;
                    viewPager.setCurrentItem(0, false);
                    alteradoListener = false;
                }
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageScrollStateChanged(int state) {
            }
        });

        viewPager.setAdapter(imageFragmentPagerAdapter);

        TextView txtMole = (TextView) findViewById(R.id.txtMole);
        txtMole.setText(images[0].getMoleGroup().getGroupName());
        ImageView imgCla = (ImageView) findViewById(R.id.imgClassification);
        imgCla.setImageResource(R.drawable.ic_class_red);

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

    public static class SwipeFragment extends Fragment implements View.OnClickListener {

        private Bitmap bitmap;

        private ImageRecord[] images;

        private int position;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Bundle bundle = getArguments();
             position = bundle.getInt("position");
            images = (ImageRecord[]) bundle.getSerializable(PARAM_IMAGES);

            ImageRecord image1 = images[position];
            ImageRecord image2;

            if(position == images.length - 1){
                image2 = images[0];
            }else{
                image2 = (position + 1 < images.length ? images[position + 1] : null);
            }


            View swipeView = inflater.inflate(R.layout.fragment_mole_image_slider, container, false);

            TextView txtDate1 = (TextView)swipeView.findViewById(R.id.txtDate1);
            TextView txtDate2 = (TextView)swipeView.findViewById(R.id.txtDate2);
            TextView txtAnnotations1 = (TextView)swipeView.findViewById(R.id.txtAnnotations1);
            TextView txtAnnotations2 = (TextView)swipeView.findViewById(R.id.txtAnnotations2);

            txtDate1.setText(image1.getImageDateAsString());
            txtAnnotations1.setText(image1.getAnnotations());
            txtDate2.setText(image2.getImageDateAsString());
            txtAnnotations2.setText(image2.getAnnotations());

            ImageView leftImageView = (ImageView) swipeView.findViewById(R.id.leftImageView);
            ImageView rightImageView = (ImageView) swipeView.findViewById(R.id.rightImageView);

            Uri imageUri = ImageUtils.getImageUri(image1.getImagePath());
            if (imageUri == null) {
                leftImageView.setImageResource(R.drawable.ic_images25);
            } else {
                leftImageView.setImageBitmap(ImageUtils.decodeSampledBitmapFromResource(getActivity(), imageUri, 200, 200));
            }

            if (image2 != null) {
                imageUri = ImageUtils.getImageUri(image2.getImagePath());
                if (imageUri == null) {
                    rightImageView.setImageResource(R.drawable.ic_images25);
                } else {
                    rightImageView.setImageBitmap(ImageUtils.decodeSampledBitmapFromResource(getActivity(), imageUri, 200, 200));
                }
            }

            leftImageView.setOnClickListener(this);
            rightImageView.setOnClickListener(this);
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

        @Override
        public void onClick(View v) {

            Intent i = new Intent(getActivity(), ImageSliderActivity.class);
            i.putExtra(ImageSliderActivity.PARAM_IMAGES, images);
            startActivity(i);

        }
    }


}
