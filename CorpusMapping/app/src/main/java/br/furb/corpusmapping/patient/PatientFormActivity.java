package br.furb.corpusmapping.patient;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import br.furb.corpusmapping.R;
import br.furb.corpusmapping.data.Gender;
import br.furb.corpusmapping.data.Patient;
import br.furb.corpusmapping.data.PatientRepository;

/**
 * Activity para o cadastro de pacientes
 *
 * @author Janaína Mendonça Lima
 */
public class PatientFormActivity extends ActionBarActivity implements View.OnClickListener {

    private PatientRepository repository;
    private long id;
    private EditText edtName;
    private EditText edtCpf;
    private EditText edtBirthDate;
    private RadioButton rbFemale;
    private RadioButton rbMale;
    private Button btnSavePatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_form);
        repository = PatientRepository.getInstance(this);
        edtName = (EditText) findViewById(R.id.edtName);
        edtCpf = (EditText) findViewById(R.id.edtCpf);
        edtBirthDate = (EditText) findViewById(R.id.edtBirthDate);
        rbFemale = (RadioButton) findViewById(R.id.rbFemale);
        rbMale = (RadioButton) findViewById(R.id.rbMale);
        btnSavePatient = (Button) findViewById(R.id.btnSavePatient);

        btnSavePatient.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            this.id = extras.getLong("id", -1);
        } else {
            this.id = -1;
        }

        if (this.id > -1) {
            loadRecord();
        }
    }

    private void loadRecord() {
        Patient patient = repository.getById(this.id);
        if (patient != null) {

            edtName.setText(patient.getName());
            edtCpf.setText(patient.getCpf());
            edtBirthDate.setText(patient.getBirthDateStr());
            rbFemale.setSelected(patient.getGender() == Gender.FEMALE);
            rbMale.setSelected(patient.getGender() == Gender.MALE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_patient_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        Patient patient = new Patient();
        if (this.id >= 0) {
            patient.setId(this.id);
        }

        patient.setName(edtName.getText().toString());
        patient.setCpf(edtCpf.getText().toString());
        patient.setGender(rbFemale.isSelected() ? Gender.FEMALE : Gender.MALE);

        patient.setBirthDate(edtBirthDate.getText().toString());
        long result;

        repository.save(patient);

        Toast.makeText(this, "Paciente salvo com sucesso!",
                Toast.LENGTH_SHORT).show();
        finish();
    }
}
