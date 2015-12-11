package br.furb.corpusmapping.ui.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import br.furb.corpusmapping.data.model.Patient;
import br.furb.corpusmapping.data.database.PatientRepository;
import br.furb.corpusmapping.ui.capture.CameraActivity;
import br.furb.corpusmapping.util.ImageDrawer;
import br.furb.corpusmapping.util.ImageUtils;

import static br.furb.corpusmapping.ui.view.MoleClassificationDialog.show;

/**
 * Activity para visualização das imagens de forma ampliada.
 *
 * @author Janaina Carraro Mendonça Lima
 */
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
    private TextView txtMole;
    private ImageView imgBodyPart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mole_image_slider);
        Object[] records = (Object[]) getIntent().getSerializableExtra(PARAM_IMAGES);
        images = new ImageRecord[records.length];

        for(int i = 0; i < records.length;i++){
            images[i] = (ImageRecord) records[i];
        }
        int selectedImage = getIntent().getIntExtra(PARAM_SELECTED_IMAGE, 0);
        numItems = images.length;
        imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(imageFragmentPagerAdapter);
        viewPager.setCurrentItem(selectedImage);

        imgBodyPart = (ImageView) findViewById(R.id.imgBodyPart);
        txtMole = (TextView) findViewById(R.id.txtMole);
        imgClassification = (ImageView) findViewById(R.id.imgClassification);

        imgClassification.setOnClickListener(this);
        imgBodyPart.setOnClickListener(this);

    }


    @Override
    protected void onStart() {
        super.onStart();
        ImageRecord imageRecord = images[0];
        moleGroup = imageRecord.getMoleGroup();
        updateValues();

        bodyPart = imageRecord.getBodyPart();
        imgBodyPart.setImageResource(bodyPart.getResource());
        ImageDrawer.drawPoint(imgBodyPart, bodyPart.getResource(), moleGroup.getPosition());
    }

    private void updateValues() {
        txtMole.setText(moleGroup.getGroupName());

        if (moleGroup.getClassification() != null) {
            imgClassification.setImageResource(moleGroup.getClassification().getResource());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_images_detailed, menu);
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
            takePicture();
        } else if (item.getItemId() == R.id.action_edit) {
            editImageRecord();
        } else if (item.getItemId() == R.id.action_remove) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Deseja excluir a imagem?");
            builder.setCancelable(true);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    removeImageRecord();
                }
            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void removeImageRecord() {
        ImageRecord imageRecord = getImageRecord();
        // Remove o registro da tabela.
        ImageRecordRepository.getInstance(this).delete(imageRecord);

        // Remove o arquivo da imagem do cartão de memória
        File file = ImageUtils.getImageFile(imageRecord.getImagePath());
        file.delete();

        // atualiza a view pager
        numItems = numItems - 1;
        int currentItem = viewPager.getCurrentItem();
        images[currentItem] = null;

        ImageRecord[] newImages = new ImageRecord[numItems];
        for (int i = 0, j = 0; i <= numItems; i++) {
            if (images[i] != null) {
                newImages[j] = images[i];
                j++;
            }
        }
        images = newImages;
        imageFragmentPagerAdapter.notifyDataSetChanged();
        Intent data = new Intent();
        data.putExtra(PARAM_IMAGES, images);
        setResult(RESULT_OK, data);
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

    private void editImageRecord() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_save_mole, null);
        builder.setView(view);

        final EditText edtGroupName = (EditText) view.findViewById(R.id.edtGroupName);
        final EditText edtAnnotation = (EditText) view.findViewById(R.id.edtAnnotation);

        final Spinner spinnerClassification = (Spinner) view.findViewById(R.id.spinnerClassification);

        spinnerClassification.setAdapter(new SpinnerClassificationAdapter(this));

        final ImageRecord imageRecord = getImageRecord();

        edtGroupName.setText(moleGroup.getGroupName());
        edtAnnotation.setText(imageRecord.getAnnotations());
        spinnerClassification.setSelection(moleGroup.getClassification().ordinal());

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                imageRecord.setAnnotations(edtAnnotation.getText().toString());
                imageRecord.getMoleGroup().setGroupName(edtGroupName.getText().toString());
                imageRecord.getMoleGroup().setClassification(MoleClassification.values()[spinnerClassification.getSelectedItemPosition()]);

                ImageRecordRepository.getInstance(MoleDetailedImageSliderActivity.this).save(imageRecord);
                imageFragmentPagerAdapter.notifyDataSetChanged();
                updateValues();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private ImageRecord getImageRecord() {
        return images[viewPager.getCurrentItem()];
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

                    List<ImageRecord> list = new ArrayList<>(Arrays.asList(images));
                    list.add(0, imageRecord);
                    images = list.toArray(new ImageRecord[list.size()]);
                    numItems = images.length;
                    imageFragmentPagerAdapter.notifyDataSetChanged();
                    Intent data = new Intent();
                    data.putExtra(PARAM_IMAGES, images);
                    setResult(RESULT_OK, data);
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }


    private class ImageFragmentPagerAdapter extends FragmentStatePagerAdapter {
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
                imageView.setImageResource(R.drawable.ic_image_photo);
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
