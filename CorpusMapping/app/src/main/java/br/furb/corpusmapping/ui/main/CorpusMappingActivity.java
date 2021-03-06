package br.furb.corpusmapping.ui.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import br.furb.corpusmapping.ui.data.DataActivity;
import br.furb.corpusmapping.ui.main.CorpusMappingApp;
import br.furb.corpusmapping.ui.main.DashboardFragment;
import br.furb.corpusmapping.ui.patient.PatientFormActivity;
import br.furb.corpusmapping.ui.patient.PatientsListFragment;
import br.furb.corpusmapping.ui.template.TemplateActivity;
import br.furb.corpusmapping.R;
import br.furb.corpusmapping.ui.settings.SettingsActivity;
import br.furb.corpusmapping.ui.view.ViewBodyDiagramActivity;
import br.furb.corpusmapping.ui.view.ViewImagesActivity;

/**
 * Activity que apresenta o painel (dashboard) com as principais funcionalidades do aplicativo.
 *
 * @author Janaina Carraro Mendonça Lima
 */
public class CorpusMappingActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    public static final String PARAM_PATIENT_ID = "patientId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corpus_mapping);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        configureDrawerMenu();

        long patientId = getIntent().getLongExtra(PARAM_PATIENT_ID, -1);

        Fragment fragment = DashboardFragment.newInstance(patientId);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    private void configureDrawerMenu() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.LEFT);


        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.app_name,
                R.string.app_name) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                supportInvalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.options));
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

        // menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
     /*   MenuItem item = menu.findItem(R.id.action_edit_patient);
        if(item!= null) {
            item.setVisible(!drawerOpen);
        }*/
        MenuItem item = menu.findItem(R.id.action_insert_patient);

        if (item != null) {
            item.setVisible(!drawerOpen);
        }

        //updateTitle(drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showItem(position);
    }

    private void showItem(int position) {
        String selecionado = mDrawerList.getItemAtPosition(position).toString();

        if (selecionado.equals("Configurações")) {
            startActivity(SettingsActivity.makeIntent(this));
        } else if (selecionado.equals("Gabarito")) {
            TemplateActivity.start(this);
        } else if (selecionado.equals("Dados do paciente")) {
            PatientFormActivity.start(this, CorpusMappingApp.getInstance().getSelectedPatientId());
        } else if (selecionado.equals("Dados (Backup)")) {
            DataActivity.start(this);
        } else if (selecionado.equals("Consultar mapeamento corporal")) {
            Intent intent = new Intent(this, ViewBodyDiagramActivity.class);
            startActivity(intent);
        } else if (selecionado.equals("Ver imagens")) {
            Intent intent = new Intent(this, ViewImagesActivity.class);
            startActivity(intent);
        }
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void updateTitle(boolean drawerIsOpen) {
        if (drawerIsOpen || mDrawerList.getCheckedItemPosition() == AdapterView.INVALID_POSITION) {
            mToolbar.setTitle(R.string.app_name);
        } else {
            int posicaoAtual = mDrawerList.getCheckedItemPosition();
            String opcaoAtual = mDrawerList.getItemAtPosition(posicaoAtual).toString();
            mToolbar.setTitle(opcaoAtual);
        }
    }
}
