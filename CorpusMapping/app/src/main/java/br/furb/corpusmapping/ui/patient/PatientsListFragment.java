package br.furb.corpusmapping.ui.patient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import br.furb.corpusmapping.ui.main.CorpusMappingActivity;
import br.furb.corpusmapping.ui.main.CorpusMappingApp;
import br.furb.corpusmapping.R;
import br.furb.corpusmapping.data.model.ImageRecord;
import br.furb.corpusmapping.data.database.ImageRecordRepository;
import br.furb.corpusmapping.data.database.MoleGroupRepository;
import br.furb.corpusmapping.data.model.Patient;
import br.furb.corpusmapping.data.database.PatientRepository;
import br.furb.corpusmapping.util.ImageUtils;

public class PatientsListFragment extends Fragment implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    private ListView listPatients;
    private Long selectedPatientId;

    private PatientRepository repository;

    public static PatientsListFragment newInstance() {
        PatientsListFragment fragment = new PatientsListFragment();
        return fragment;
    }

    public PatientsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        repository = PatientRepository.getInstance(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
        List<Patient> patients = repository.getAll();

        ArrayAdapter<Patient> adapter = new ArrayAdapter<Patient>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                patients);

        listPatients.setAdapter(adapter);
    }

    private void updatePatients() {
        List<Patient> patients = repository.getAll();

        ArrayAdapter<Patient> adapter = new ArrayAdapter<Patient>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                patients);

        listPatients.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_patients_fragment, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_patients_list, container, false);

        listPatients = (ListView) layout.findViewById(R.id.listPatients);

        listPatients.setOnItemClickListener(this);
        listPatients.setOnItemLongClickListener(this);

        return layout;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Patient patient = (Patient) listPatients.getAdapter().getItem(position);
        selectedPatientId = patient.getId();

        CorpusMappingApp.getInstance().setSelectedPatientId(selectedPatientId);

        Intent intent = new Intent(getActivity(), CorpusMappingActivity.class);
        intent.putExtra(CorpusMappingActivity.PARAM_PATIENT_ID, selectedPatientId);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_insert_patient) {
            Intent i = new Intent(getActivity(), PatientFormActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Deseja excluir o paciente e todos os seus dados?");
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Patient patient = (Patient) listPatients
                        .getAdapter().getItem(position);
                removePatient(patient);
                Toast.makeText(getActivity(), "Paciente excluído com sucesso!", Toast.LENGTH_SHORT);
                updatePatients();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

        return true;
    }

    private void removePatient(Patient patient) {
        PatientRepository patientRepository = PatientRepository.getInstance(getActivity());
        patientRepository.delete(patient);
        long patientId = patient.getId();

        ImageRecordRepository imageRecordRepository = ImageRecordRepository.getInstance(getActivity());
        List<ImageRecord> imageRecords = imageRecordRepository.getByPatientId(patientId);
        imageRecordRepository.deleteByPatient(patientId);
        MoleGroupRepository.getInstance(getActivity()).deleteByPatient(patientId);

        for (ImageRecord imageRecord : imageRecords) {
            File imageFile = ImageUtils.getImageFile(imageRecord.getImagePath());
            imageFile.delete();
        }

        File patientImagesDir = ImageUtils.getPatientImagesDir(patientId, patient.getName());
        patientImagesDir.delete();
    }

}
