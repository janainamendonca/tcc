package br.furb.corpusmapping;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.furb.corpusmapping.data.Gender;
import br.furb.corpusmapping.data.Patient;
import br.furb.corpusmapping.data.PatientRepository;


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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_patient_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
