package br.furb.corpusmapping.ui.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.joda.time.LocalDateTime;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.furb.corpusmapping.ui.main.CorpusMappingApp;
import br.furb.corpusmapping.data.model.ImageType;
import br.furb.corpusmapping.R;
import br.furb.corpusmapping.data.model.SpecificBodyPart;
import br.furb.corpusmapping.ui.common.SpinnerClassificationAdapter;
import br.furb.corpusmapping.data.model.ImageRecord;
import br.furb.corpusmapping.data.database.ImageRecordRepository;
import br.furb.corpusmapping.data.model.MoleClassification;
import br.furb.corpusmapping.data.model.MoleGroup;
import br.furb.corpusmapping.data.database.MoleGroupRepository;
import br.furb.corpusmapping.data.model.Patient;
import br.furb.corpusmapping.data.database.PatientRepository;
import br.furb.corpusmapping.ui.capture.CameraActivity;
import br.furb.corpusmapping.util.ImageDrawer;
import br.furb.corpusmapping.util.ImageUtils;

import static br.furb.corpusmapping.ui.view.MoleClassificationDialog.show;

/**
 * Activity para visualização de duas imagens lado à lado.
 *
 * @author Janaina Carraro Mendonça Lima
 */
public class MoleImageSliderActivity extends ActionBarActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_IMAGE = 1;
    private static final int REQUEST_CODE_IMAGE_SLIDER = 2;
    public static final String PARAM_IMAGES = "images";
    ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    ViewPager viewPager;
    private ImageRecord[] images;
    private MoleGroup moleGroup;
    private ImageView imgClassification;
    private ImageView imgBodyPart;
    private String imageShortPath;
    private SpecificBodyPart bodyPart;
    private TextView txtMole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mole_image_slider);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        images = getImages(getIntent(), savedInstanceState);

        imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.pager);

        viewPager.setAdapter(imageFragmentPagerAdapter);

        txtMole = (TextView) findViewById(R.id.txtMole);
        imgClassification = (ImageView) findViewById(R.id.imgClassification);
        imgBodyPart = (ImageView) findViewById(R.id.imgBodyPart);
        imgClassification.setOnClickListener(this);
        imgBodyPart.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ImageRecord imageRecord = images[0];
        moleGroup = imageRecord.getMoleGroup();
        bodyPart = imageRecord.getBodyPart();
        imgBodyPart.setImageResource(bodyPart.getResource());
        ImageDrawer.drawPoint(imgBodyPart, bodyPart.getResource(), moleGroup.getPosition());
        updateValues();
    }

    private void updateValues() {
        if (moleGroup.getClassification() != null) {
            imgClassification.setImageResource(moleGroup.getClassification().getResource());
        }
        txtMole.setText(moleGroup.getGroupName());
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

    private class ImageFragmentPagerAdapter extends FragmentStatePagerAdapter {
        public ImageFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            int count = images.length / 2;
            if (images.length % 2 > 0) {
                count += 1;
            }
            return count;
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
            takePicture();
        } else if (item.getItemId() == R.id.action_edit) {
            editMoleGroup();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(PARAM_IMAGES, images);
    }

    private void takePicture() {
        long patientId = CorpusMappingApp.getInstance().getSelectedPatientId();
        Patient patient = PatientRepository.getInstance(this).getById(patientId);

        File sdImageFile = ImageUtils.getFileForNewImage(patient);
        imageShortPath = ImageUtils.getImageShortPath(sdImageFile);

        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra(CameraActivity.IMAGE_PATH, sdImageFile.getAbsolutePath());

        startActivityForResult(intent, REQUEST_CODE_IMAGE);
    }

    private void editMoleGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_mole_group, null);
        builder.setView(view);

        final EditText edtGroupName = (EditText) view.findViewById(R.id.edtGroupName);

        final Spinner spinnerClassification = (Spinner) view.findViewById(R.id.spinnerClassification);

        spinnerClassification.setAdapter(new SpinnerClassificationAdapter(this));

        final ImageRecord imageRecord = images[viewPager.getCurrentItem()];

        edtGroupName.setText(moleGroup.getGroupName());
        spinnerClassification.setSelection(moleGroup.getClassification().ordinal());

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                moleGroup.setGroupName(edtGroupName.getText().toString());
                moleGroup.setClassification(MoleClassification.values()[spinnerClassification.getSelectedItemPosition()]);
                MoleGroupRepository.getInstance(MoleImageSliderActivity.this).save(moleGroup);
                imageFragmentPagerAdapter.notifyDataSetChanged();
                updateValues();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK
                && requestCode == REQUEST_CODE_IMAGE) {
            final MoleGroup moleGroup = images[0].getMoleGroup();

            final ImageRecord imageRecord = new ImageRecord();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_annotations, null);
            builder.setView(view);
            final EditText edtAnnotation = (EditText) view.findViewById(R.id.edtAnnotation);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    imageRecord.setAnnotations(edtAnnotation.getText().toString());

                    imageRecord.setImageDate(LocalDateTime.now());
                    imageRecord.setPosition(moleGroup.getPosition());
                    imageRecord.setImagePath(imageShortPath);
                    imageRecord.setImageType(ImageType.LOCAL);
                    imageRecord.setBodyPart(images[0].getBodyPart());

                    moleGroup.setGroupName(moleGroup.getGroupName());
                    imageRecord.setMoleGroup(moleGroup);
                    long patientId = CorpusMappingApp.getInstance().getSelectedPatientId();
                    imageRecord.setPatientId(patientId);
                    ImageRecordRepository.getInstance(MoleImageSliderActivity.this).save(imageRecord);

                    // images = Arrays.copyOf(images, images.length + 1);
                    List<ImageRecord> list = new ArrayList<>(Arrays.asList(images));
                    list.add(0, imageRecord);
                    images = list.toArray(new ImageRecord[list.size()]);
                    //images[images.length - 1] = imageRecord;
                    imageFragmentPagerAdapter.notifyDataSetChanged();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (requestCode == REQUEST_CODE_IMAGE_SLIDER && resultCode == RESULT_OK) {
            if (data != null) {
                ImageRecord[] imageRecords = getImages(data);
                images = imageRecords.length > 0 ? imageRecords : images;
                imageFragmentPagerAdapter.notifyDataSetChanged();
            }
        }

    }

    private ImageRecord[] getImages(Intent intent) {
        Object[] records = (Object[]) intent.getSerializableExtra(PARAM_IMAGES);

        if (records != null) {
            ImageRecord[] images = new ImageRecord[records.length];

            for (int i = 0; i < records.length; i++) {
                images[i] = (ImageRecord) records[i];
            }
            return images;
        }
        return new ImageRecord[0];
    }

    private ImageRecord[] getImages(Intent intent, Bundle savedInstance) {
        Object[] records = (Object[]) intent.getSerializableExtra(PARAM_IMAGES);

        if (savedInstance != null && records == null) {
            records = (Object[]) savedInstance.getSerializable(PARAM_IMAGES);
        }

        if (records != null) {
            ImageRecord[] images = new ImageRecord[records.length];

            for (int i = 0; i < records.length; i++) {
                images[i] = (ImageRecord) records[i];
            }
            return images;
        }
        return new ImageRecord[0];
    }

    public static class SwipeFragment extends Fragment implements View.OnClickListener {


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
                image2 = null;
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
                leftImageView.setImageResource(R.drawable.ic_image_photo);
            } else {
                leftImageView.setImageBitmap(ImageUtils.decodeSampledBitmapFromResource(getActivity(), imageUri, 200, 200));
            }

            if (image2 != null) {
                imageUri = ImageUtils.getImageUri(image2.getImagePath());
                if (imageUri == null) {
                    rightImageView.setImageResource(R.drawable.ic_image_photo);
                } else {
                    rightImageView.setImageBitmap(ImageUtils.decodeSampledBitmapFromResource(getActivity(), imageUri, 200, 200));
                }
            } else {
                rightImageView.setImageResource(R.drawable.ic_image_photo);
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

            int p = position;

            if (v.getId() == R.id.rightImageView) {
                if (position == images.length - 1 && images.length > 1) {
                    p = 0;
                } else {
                    p = (position + 1 < images.length ? position + 1 : -1);
                }
            }

            if (p >= 0) {
                Intent i = new Intent(getActivity(), MoleDetailedImageSliderActivity.class);
                i.putExtra(MoleDetailedImageSliderActivity.PARAM_IMAGES, images);
                i.putExtra(MoleDetailedImageSliderActivity.PARAM_SELECTED_IMAGE, p);
                getActivity().startActivityForResult(i, REQUEST_CODE_IMAGE_SLIDER);
            }
        }
    }


}
