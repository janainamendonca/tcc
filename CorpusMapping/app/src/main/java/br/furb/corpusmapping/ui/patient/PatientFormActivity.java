package br.furb.corpusmapping.ui.patient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import br.furb.corpusmapping.R;
import br.furb.corpusmapping.data.model.Gender;
import br.furb.corpusmapping.data.model.Patient;
import br.furb.corpusmapping.data.database.PatientRepository;

/**
 * Activity para o cadastro de pacientes
 *
 * @author Janaína Mendonça Lima
 */
public class PatientFormActivity extends Activity implements View.OnClickListener {

    private PatientRepository repository;
    private long id;
    private EditText edtName;
    private EditText edtCpf;
    private EditText edtBirthDate;
    private RadioButton rbFemale;
    private RadioButton rbMale;
    private Button btnSavePatient;

    public static void start(Context context, long patientId) {
        Intent intent = new Intent(context, PatientFormActivity.class);
        intent.putExtra("id", patientId);
        context.startActivity(intent);
    }

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
            if (patient.getGender() == Gender.FEMALE) {
                rbFemale.setChecked(true);
            } else {
                rbMale.setChecked(true);
            }
        }

    }

    @Override
    public void onClick(View v) {

        Patient patient = new Patient();
        if (this.id >= 0) {
            patient.setId(this.id);
        }

        patient.setName(edtName.getText().toString());
        patient.setCpf(edtCpf.getText().toString());
        patient.setGender(rbFemale.isChecked() ? Gender.FEMALE : Gender.MALE);
        try {
            patient.setBirthDate(edtBirthDate.getText().toString());
        } catch (Exception e) {
            Toast.makeText(this, "Valor inválido no campo Data de nascimento.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        repository.save(patient);
        Toast.makeText(this, "Paciente salvo com sucesso!",
                Toast.LENGTH_SHORT).show();

        finish();
    }
}
