package br.furb.corpusmapping.patient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.furb.corpusmapping.R;
import br.furb.corpusmapping.data.Patient;
import br.furb.corpusmapping.data.PatientRepository;


public class PatientsActivity extends ActionBarActivity implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener, DialogInterface.OnClickListener {

    private ListView listPatients;
    private Long selectedItem;

    AlertDialog decisaoDialog;
    private PatientRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_activity);
        listPatients = (ListView) findViewById(R.id.listPatients);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbarPatients);
        setSupportActionBar(mToolbar);

        repository = PatientRepository.getInstance(this);

        listPatients.setOnItemClickListener(this);
        listPatients.setOnItemLongClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        List<Patient> patients = repository.getAll();

        ArrayAdapter<Patient> adapter = new ArrayAdapter<Patient>(
                this,
                android.R.layout.simple_list_item_1,
                patients);

        listPatients.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_patients_actitivity, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        // helper.close(); TODO
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_insert) {
            Intent i = new Intent(this, PatientFormActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        Patient item = (Patient) listPatients
                .getAdapter().getItem(position);
        selectedItem = item.getId();

        changePatient();
    }

    private void changePatient() {
        Intent intent = new Intent(this, PatientFormActivity.class);

        intent.putExtra("id", selectedItem);

        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {

        Map<String, Object> item = (Map<String, Object>) listPatients
                .getAdapter().getItem(position);
        selectedItem = (Long) item.get("_id");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        CharSequence[] itens = {getString(R.string.dialog_yes),
                getString(R.string.dialog_no)};
        builder.setTitle(getString(R.string.dialog_delete_title));
        builder.setItems(itens, this);

        //

        decisaoDialog = builder.create();

        return false;
    }

    @Override
    public void onClick(DialogInterface dialog, int item) {

        if (item == 0) {// sim
            removePatient();
        } else {// nao
            Toast.makeText(this, getString(R.string.dialog_delete_title), Toast.LENGTH_SHORT).show();
        }
    }

    private void removePatient() {
       /* SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("PRODUTOS", "_id = ?", new String[]{String.valueOf(selectedItem)});
        db.close(); TODO
*/
        Toast.makeText(this, "Exclusão realizada com sucesso", Toast.LENGTH_SHORT).show();
    }


    private List<? extends Map<String, ?>> loadPatients() {
        List<Map<String, Object>> dados = new ArrayList<Map<String, Object>>();

       /* SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT _id, codigo, nome from PRODUTOS ORDER BY nome", null);

        cursor.moveToFirst();

        while (cursor.moveToNext()) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("_id", cursor.getLong(0));
            map.put("codigo", cursor.getString(1));
            map.put("nome", cursor.getString(2));
            dados.add(map);
        }

        cursor.close();*/

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("_id", 1L);
        map.put("codigo", "1");
        map.put("nome", "Jubileu");
        dados.add(map);

        map = new HashMap<String, Object>();
        map.put("_id", 2L);
        map.put("codigo", "2");
        map.put("nome", "Nina");
        dados.add(map);

        map = new HashMap<String, Object>();
        map.put("_id", 3L);
        map.put("codigo", "3");
        map.put("nome", "Batatinha");
        dados.add(map);

        map = new HashMap<String, Object>();
        map.put("_id", 4L);
        map.put("codigo", "4");
        map.put("nome", "Lisa");
        dados.add(map);

        return dados;
    }

}
