package br.furb.corpusmapping;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.LocalDateTime;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import br.furb.corpusmapping.data.ImageRecord;
import br.furb.corpusmapping.data.ImageRecordRepository;
import br.furb.corpusmapping.data.MoleGroup;
import br.furb.corpusmapping.data.Patient;
import br.furb.corpusmapping.data.PatientRepository;
import br.furb.corpusmapping.data.PointF;
import br.furb.corpusmapping.util.ImageDrawer;
import br.furb.corpusmapping.util.ImageUtils;

import static br.furb.corpusmapping.util.MoleClassificationDialog.show;


public class MoleImageSliderActivity extends ActionBarActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_IMAGE = 1;
    public static final String PARAM_IMAGES = "images";
    ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    ViewPager viewPager;
    private ImageRecord[] images;
    private int numItems;
    private MoleGroup moleGroup;
    private ImageView imgClassification;
    private String imageShortPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mole_image_slider);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
            show(this, moleGroup, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    imgClassification.setImageResource(moleGroup.getClassification().getResource());
                }
            });
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

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_images, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_take_picture) {
            long patientId = CorpusMappingApp.getInstance().getSelectedPatientId();
            Patient patient = PatientRepository.getInstance(this).getById(patientId);

            File sdImageFile = ImageUtils.getFileForNewImage(patient);
            Uri outputFileUri = Uri.fromFile(sdImageFile);

            imageShortPath = ImageUtils.getImageShortPath(sdImageFile);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(intent, REQUEST_CODE_IMAGE);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK
                && requestCode == REQUEST_CODE_IMAGE) {
            MoleGroup moleGroup = images[0].getMoleGroup();

            String annotation = "colocar depois";

            ImageRecord imageRecord = new ImageRecord();
            imageRecord.setImageDate(LocalDateTime.now());
            imageRecord.setPosition(moleGroup.getPosition());
            imageRecord.setImagePath(imageShortPath);
            imageRecord.setImageType(ImageType.LOCAL);
            imageRecord.setBodyPart(images[0].getBodyPart());
            imageRecord.setAnnotations(annotation);

            moleGroup.setGroupName(moleGroup.getGroupName());
            imageRecord.setMoleGroup(moleGroup);
            long patientId = CorpusMappingApp.getInstance().getSelectedPatientId();
            imageRecord.setPatientId(patientId);
            ImageRecordRepository.getInstance(this).save(imageRecord);

            images = Arrays.copyOf(images, images.length + 1);
            images[images.length - 1] = imageRecord;
            numItems = images.length;
            //imageFragmentPagerAdapter.notifyDataSetChanged();
            viewPager.setAdapter(imageFragmentPagerAdapter);
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

            if (position == images.length - 1 && images.length > 1) {
                image2 = images[0];
            } else {
                image2 = (position + 1 < images.length ? images[position + 1] : null);
            }

            View swipeView = inflater.inflate(R.layout.fragment_mole_image_slider, container, false);

            TextView txtDate1 = (TextView) swipeView.findViewById(R.id.txtDate1);
            TextView txtDate2 = (TextView) swipeView.findViewById(R.id.txtDate2);
            TextView txtAnnotations1 = (TextView) swipeView.findViewById(R.id.txtAnnotations1);
            TextView txtAnnotations2 = (TextView) swipeView.findViewById(R.id.txtAnnotations2);

            txtDate1.setText(image1.getImageDateAsString());
            txtAnnotations1.setText(image1.getAnnotations());
            if (image2 != null) {
                txtDate2.setText(image2.getImageDateAsString());
                txtAnnotations2.setText(image2.getAnnotations());
            } else {
                txtDate2.setText("");
                txtAnnotations2.setText("");
            }
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
            } else {
                rightImageView.setImageResource(R.drawable.ic_images25);
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

            Intent i = new Intent(getActivity(), MoleDetailedImageSliderActivity.class);
            i.putExtra(MoleDetailedImageSliderActivity.PARAM_IMAGES, images);
            i.putExtra(MoleDetailedImageSliderActivity.PARAM_SELECTED_IMAGE, position);
            startActivity(i);

        }
    }


}
