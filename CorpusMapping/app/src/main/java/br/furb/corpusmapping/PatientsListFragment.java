package br.furb.corpusmapping;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.util.SparseBooleanArray;
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

import br.furb.corpusmapping.data.Patient;
import br.furb.corpusmapping.data.PatientRepository;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PatientsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PatientsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatientsListFragment extends Fragment implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView listPatients;
    private Long selectedItem;

    private PatientRepository repository;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PatientsListFragment.
     */
    public static PatientsListFragment newInstance() {
        PatientsListFragment fragment = new PatientsListFragment();
        Bundle args = new Bundle();
        // args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PatientsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_patients_list, container, false);

        listPatients = (ListView) layout.findViewById(R.id.listPatients);

        listPatients.setOnItemClickListener(this);
        listPatients.setOnItemLongClickListener(this);

        return layout;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
       /* try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Patient item = (Patient) listPatients
                .getAdapter().getItem(position);
        selectedItem = item.getId();

        listPatients.setItemChecked(position, true);

        CorpusMappingApp.getInstance().setSelectedPatientId(selectedItem);

        //
        Intent intent = new Intent(getActivity(), PatientSelectedActivity.class);
        intent.putExtra(PatientSelectedActivity.PARAM_PATIENT_ID, selectedItem);
        startActivity(intent);
        //

        // changePatient();
    }

    private void changePatient() {
        Intent intent = new Intent(getActivity(), PatientFormActivity.class);

        intent.putExtra("id", selectedItem);

        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_insert_patient) {
            Intent i = new Intent(getActivity(), PatientFormActivity.class);
            startActivity(i);
            return true;
        }
        /*else if(id == R.id.action_edit_patient){
            changePatient();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
