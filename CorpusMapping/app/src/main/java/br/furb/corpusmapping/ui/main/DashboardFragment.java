package br.furb.corpusmapping.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.LocalDateTime;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import br.furb.corpusmapping.R;
import br.furb.corpusmapping.data.database.ImageRecordRepository;
import br.furb.corpusmapping.data.database.MoleGroupRepository;
import br.furb.corpusmapping.data.model.ImageRecord;
import br.furb.corpusmapping.data.model.MoleClassification;
import br.furb.corpusmapping.data.model.MoleGroup;
import br.furb.corpusmapping.ui.bodylink.SaveImageActivity;
import br.furb.corpusmapping.data.model.Patient;
import br.furb.corpusmapping.data.database.PatientRepository;
import br.furb.corpusmapping.ui.capture.CameraActivity;
import br.furb.corpusmapping.util.ImageUtils;
import br.furb.corpusmapping.ui.view.ViewBodyDiagramActivity;
import br.furb.corpusmapping.ui.view.ViewImagesActivity;

public class DashboardFragment extends Fragment implements View.OnClickListener {

    public static final String PARAM_PATIENT_ID = "patientId";
    private static final int REQUEST_CODE_IMAGE = 1;
    private static final String TAG = DashboardFragment.class.getName();
    private long patientId;
    private PatientRepository repository;
    private TextView txtPatient;
    private Patient patient;
    private String imageShortPath;
    private TextView txtNormal;
    private TextView txtWarning;
    private TextView txtDanger;
    private TextView txtNone;
    private TextView txtLastUpdate;

    public DashboardFragment() {
    }

    public static DashboardFragment newInstance(long patientId) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putLong(PARAM_PATIENT_ID, patientId);
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        txtPatient = (TextView) view.findViewById(R.id.txtPatient);
        patientId = CorpusMappingApp.getInstance().getSelectedPatientId();
        patient = repository.getById(patientId);
        txtPatient.setText(patient.getName());

        Button button = (Button) view.findViewById(R.id.btnAddImage);
        Button btnViewImages = (Button) view.findViewById(R.id.btnViewImages);
        Button btnViewDiagram = (Button) view.findViewById(R.id.btnViewDiagram);
        button.setOnClickListener(this);
        btnViewImages.setOnClickListener(this);
        btnViewDiagram.setOnClickListener(this);

        txtNormal = (TextView) view.findViewById(R.id.txtNormal);
        txtWarning = (TextView) view.findViewById(R.id.txtWarning);
        txtDanger = (TextView) view.findViewById(R.id.txtDanger);
        txtNone = (TextView) view.findViewById(R.id.txtNone);
        txtLastUpdate = (TextView) view.findViewById(R.id.txtLastUpdate);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        List<MoleGroup> moleGroups = MoleGroupRepository.getInstance(this.getActivity()).getByPatient(patientId);

        int normalCount = 0;
        int warningCount = 0;
        int dangerCount = 0;
        int noneCount = 0;

        for (MoleGroup moleGroup : moleGroups) {
            switch (moleGroup.getClassification()) {
                case NORMAL:
                    normalCount++;
                    break;
                case ATTENTION:
                    warningCount++;
                    break;
                case DANGER:
                    dangerCount++;
                    break;
                case NONE:
                    noneCount++;
            }
        }

        txtNormal.setText(getTextRisk(normalCount, "normal"));
        txtWarning.setText(getTextRisk(warningCount, "médio"));
        txtDanger.setText(getTextRisk(dangerCount, "alto"));
        txtNone.setText(getTextRisk(noneCount, ""));

        Collection<List<ImageRecord>> values = ImageRecordRepository.getInstance(getActivity()).getLastMolesByPatientId(patientId).values();

        LocalDateTime lastDate = null;

        for (List<ImageRecord> images : values) {
            if (images.size() > 0) {
                LocalDateTime date = images.get(0).getImageDate();

                if (lastDate == null || date.isAfter(lastDate)) {
                    lastDate = date;
                }
            }
        }
        if (lastDate != null) {
            txtLastUpdate.setText("Última imagem capturada no dia " + lastDate.toString("dd/MM/yyyy") + ".");
        } else {
            txtLastUpdate.setText("Nenhuma imagem capturada até o momento.");
        }

    }

    private String getTextRisk(int count, String risk) {
        if (risk.isEmpty()) {
            if (count == 0) {
                return "Nenhuma pinta não classificada";
            }

            if (count == 1) {
                return count + " pinta não classificada";
            }

            return count + " pintas não classificadas";
        }

        if (count == 0) {
            return "Nenhuma pinta com risco " + risk;
        }

        if (count == 1) {
            return count + " pinta com risco " + risk;
        }

        return count + " pintas com risco " + risk;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnAddImage:
                if (!externalStorageAvailable()) {
                    Toast.makeText(this.getActivity(), "O cartão de memória não está disponível para salvar a imagem.", Toast.LENGTH_LONG);
                } else {
                    File sdImageFile = ImageUtils.getFileForNewImage(patient);
                    imageShortPath = ImageUtils.getImageShortPath(sdImageFile);

                    Intent intent = new Intent(getActivity(), CameraActivity.class);
                    intent.putExtra(CameraActivity.IMAGE_PATH, sdImageFile.getAbsolutePath());
                    startActivityForResult(intent, REQUEST_CODE_IMAGE);
                }
                break;
            case R.id.btnViewImages:
                Intent intent = new Intent(getActivity(), ViewImagesActivity.class);
                startActivity(intent);
                break;
            case R.id.btnViewDiagram:
                intent = new Intent(getActivity(), ViewBodyDiagramActivity.class);
                startActivity(intent);
                break;
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

}
