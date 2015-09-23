package br.furb.corpusmapping;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.RadioButton;
import android.widget.TextView;

import br.furb.corpusmapping.data.ImageRecord;
import br.furb.corpusmapping.data.MoleClassification;
import br.furb.corpusmapping.data.MoleGroup;
import br.furb.corpusmapping.util.ImageDrawer;
import br.furb.corpusmapping.util.ImageUtils;


public class ImageSliderActivity extends FragmentActivity implements View.OnClickListener {

    public static final String PARAM_IMAGES = "images";
    public static final String PARAM_SELECTED_IMAGE = "selected-image";
    ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    ViewPager viewPager;
    private ImageRecord[] images;
    private int numItems;
    private int selectedImage;
    private MoleGroup moleGroup;
    private ImageView imgClassification;


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

        TextView txtMole = (TextView) findViewById(R.id.txtMole);
        ImageRecord imageRecord = images[0];
        moleGroup = imageRecord.getMoleGroup();
        txtMole.setText(moleGroup.getGroupName());
        imgClassification = (ImageView) findViewById(R.id.imgClassification);

        if (moleGroup.getClassification() != null) {
            imgClassification.setImageResource(moleGroup.getClassification().getResource());
        }

        ImageView imgBodyPart = (ImageView) findViewById(R.id.imgBodyPart);
        SpecificBodyPart bodyPart = imageRecord.getBodyPart();
        imgBodyPart.setImageResource(bodyPart.getResource());
        ImageDrawer.drawPoint(imgBodyPart, bodyPart.getResource(), moleGroup.getPosition());

        imgClassification.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgClassification) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Classificação da pinta:");
            View view = this.getLayoutInflater().inflate(R.layout.dialog_mole_classification, null);
            builder.setView(view);

            final RadioButton rbNormal = (RadioButton) view.findViewById(R.id.rbNormal);
            final RadioButton rbAttention = (RadioButton) view.findViewById(R.id.rbAttention);
            final RadioButton rbDanger = (RadioButton) view.findViewById(R.id.rbDanger);
            MoleClassification classification = moleGroup.getClassification();
            if (classification != null) {
                switch (classification) {
                    case NORMAL:
                        rbNormal.setChecked(true);
                        break;
                    case ATTENTION:
                        rbAttention.setChecked(true);
                        break;
                    case DANGER:
                        rbDanger.setChecked(true);
                        break;
                }
            }

            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    if (rbNormal.isChecked()) {
                        moleGroup.setClassification(MoleClassification.NORMAL);
                    } else if (rbAttention.isChecked()) {
                        moleGroup.setClassification(MoleClassification.ATTENTION);
                    } else if (rbDanger.isChecked()) {
                        moleGroup.setClassification(MoleClassification.DANGER);
                    }

                    imgClassification.setImageResource(moleGroup.getClassification().getResource());
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
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
