package br.furb.corpusmapping;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.furb.corpusmapping.data.Patient;
import br.furb.corpusmapping.data.PatientRepository;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DashboardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    public static final String PARAM_PATIENT_ID = "patientId";
    private static final int REQUEST_CODE_IMAGE = 1;

    private long patientId;
    private PatientRepository repository;
    private TextView txtPatient;
    private Patient patient;
    private String imageShortPath;

    public static DashboardFragment newInstance(long patientId) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putLong(PARAM_PATIENT_ID, patientId);
        fragment.setArguments(args);
        return fragment;
    }

    public DashboardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            patientId = getArguments().getLong(PARAM_PATIENT_ID);
        }
        repository = PatientRepository.getInstance(getActivity());

        patient = repository.getById(patientId);

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        txtPatient = (TextView) view.findViewById(R.id.txtPatient);

        txtPatient.setText(patient.getName());

        Button button = (Button) view.findViewById(R.id.btnAddImage);
        button.setOnClickListener(this);
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

        if (!externalStorageAvailable()) {
            Toast.makeText(this.getActivity(), "O cartão de memória não está disponível para salvar a imagem.", Toast.LENGTH_LONG);
        } else {
            File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "CorpusMapping");
            root.mkdirs();

            File patientDir = new File(root, patient.getId() + "_" +patient.getName());
            patientDir.mkdirs();

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = timeStamp + ".jpg";
            File sdImageFile = new File(patientDir, fileName);
            Uri outputFileUri = Uri.fromFile(sdImageFile);

            Log.d("C", "Uri: " + outputFileUri.getPath());

            imageShortPath = patientDir.getName() + File.pathSeparator +fileName;

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(intent, REQUEST_CODE_IMAGE);
        }
    }

    private boolean externalStorageAvailable() {
        return
                Environment.MEDIA_MOUNTED
                        .equals(Environment.getExternalStorageState());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK
                && requestCode == REQUEST_CODE_IMAGE) {
            Intent intent = new Intent(getActivity(), SaveImageActivity.class);
            intent.putExtra(SaveImageActivity.IMAGE_SHORT_PATH, imageShortPath);
            intent.putExtra(SaveImageActivity.PATIENT_ID, patientId);
            startActivity(intent);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
