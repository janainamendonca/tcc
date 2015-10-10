package br.furb.corpusmapping.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.joda.time.LocalDateTime;

import java.io.File;
import java.util.Arrays;

import br.furb.corpusmapping.CorpusMappingApp;
import br.furb.corpusmapping.ImageType;
import br.furb.corpusmapping.R;
import br.furb.corpusmapping.SpecificBodyPart;
import br.furb.corpusmapping.common.SpinnerClassificationAdapter;
import br.furb.corpusmapping.data.ImageRecord;
import br.furb.corpusmapping.data.ImageRecordRepository;
import br.furb.corpusmapping.data.MoleClassification;
import br.furb.corpusmapping.data.MoleGroup;
import br.furb.corpusmapping.data.Patient;
import br.furb.corpusmapping.data.PatientRepository;
import br.furb.corpusmapping.util.BodyPartDialog;
import br.furb.corpusmapping.util.ImageDrawer;
import br.furb.corpusmapping.util.ImageUtils;

import static br.furb.corpusmapping.util.MoleClassificationDialog.show;


public class MoleDetailedImageSliderActivity extends ActionBarActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_IMAGE = 1;
    public static final String PARAM_IMAGES = "images";
    public static final String PARAM_SELECTED_IMAGE = "selected-image";
    ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    ViewPager viewPager;
    private ImageRecord[] images;
    private int numItems;
    private MoleGroup moleGroup;
    private ImageView imgClassification;
    private SpecificBodyPart bodyPart;
    private String imageShortPath;


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
        } else if (item.getItemId() == R.id.action_edit) {
            editImageRecord();
        }
        return super.onOptionsItemSelected(item);
    }

    private void editImageRecord() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_save_mole, null);
        builder.setView(view);

        final EditText edtGroupName = (EditText) view.findViewById(R.id.edtGroupName);
        final EditText edtAnnotation = (EditText) view.findViewById(R.id.edtAnnotation);

        final Spinner spinnerClassification = (Spinner) view.findViewById(R.id.spinnerClassification);

        spinnerClassification.setAdapter(new SpinnerClassificationAdapter(this));

        final ImageRecord imageRecord = images[viewPager.getCurrentItem()];

        edtGroupName.setText(moleGroup.getGroupName());
        edtAnnotation.setText(imageRecord.getAnnotations());
        spinnerClassification.setSelection(moleGroup.getClassification().ordinal());

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                imageRecord.setAnnotations(edtAnnotation.getText().toString());
                imageRecord.getMoleGroup().setGroupName(edtGroupName.getText().toString());
                imageRecord.getMoleGroup().setClassification(MoleClassification.values()[spinnerClassification.getSelectedItemPosition()]);

                ImageRecordRepository.getInstance(MoleDetailedImageSliderActivity.this).save(imageRecord);
                //TODO propagar a alteração para atualizar os componentes da tela
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
                    ImageRecordRepository.getInstance(MoleDetailedImageSliderActivity.this).save(imageRecord);

                    images = Arrays.copyOf(images, images.length + 1);
                    images[images.length - 1] = imageRecord;
                    numItems = images.length;
                    //imageFragmentPagerAdapter.notifyDataSetChanged(); TODO verificar como atualizar a viewPager
                    viewPager.setAdapter(imageFragmentPagerAdapter);
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
