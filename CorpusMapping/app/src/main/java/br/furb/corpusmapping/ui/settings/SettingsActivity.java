package br.furb.corpusmapping.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import br.furb.corpusmapping.R;
import br.furb.corpusmapping.adapters.SettingsAdapter;
import br.furb.corpusmapping.ui.common.NavigationScreen;
import br.furb.corpusmapping.ui.settings.data.DataActivity;

public class SettingsActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private SettingsAdapter adapter;
    private boolean isResumed = false;
    private boolean requestLock = false;

    public static Intent makeIntent(Context context){
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Get views
        final ListView list_V = (ListView) findViewById(R.id.listView);

        // Setup
        adapter = new SettingsAdapter(this);
        list_V.setAdapter(adapter);
        list_V.setOnItemClickListener(this);
    }

    @Override protected void onResume() {
        super.onResume();
        isResumed = true;
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.clear();
        return true;
    }

    @Override protected void onPause() {
        super.onPause();
        isResumed = false;
    }

    protected NavigationScreen getNavigationScreen() {
        return NavigationScreen.Settings;
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
         if (id == SettingsAdapter.ID_DATA) {
           DataActivity.start(this);
        } else if (id == SettingsAdapter.ID_ABOUT) {
          // TODO  AboutActivity.start(this);
        }
    }

}
