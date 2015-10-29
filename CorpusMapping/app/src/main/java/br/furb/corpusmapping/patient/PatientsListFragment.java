package br.furb.corpusmapping.patient;

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

import java.util.List;

import br.furb.corpusmapping.CorpusMappingApp;
import br.furb.corpusmapping.R;
import br.furb.corpusmapping.data.Patient;
import br.furb.corpusmapping.data.PatientRepository;

public class PatientsListFragment extends Fragment implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    private ListView listPatients;
    private Long selectedItem;

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

        Patient item = (Patient) listPatients
                .getAdapter().getItem(position);
        selectedItem = item.getId();

        listPatients.setItemChecked(position, true);

        CorpusMappingApp.getInstance().setSelectedPatientId(selectedItem);

        Intent intent = new Intent(getActivity(), PatientSelectedActivity.class);
        intent.putExtra(PatientSelectedActivity.PARAM_PATIENT_ID, selectedItem);
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
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

}
