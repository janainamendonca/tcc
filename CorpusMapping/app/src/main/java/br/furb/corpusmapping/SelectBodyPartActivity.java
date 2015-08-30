package br.furb.corpusmapping;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class SelectBodyPartActivity extends ActionBarActivity {

    public static final String PARAM_BODY_PART = "BODY_PART";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_body_part);

        BodyPart bodyPart = BodyPart.valueOf(getIntent().getStringExtra(PARAM_BODY_PART));

        Fragment fragment = null;
        switch (bodyPart) {
            case HEAD:
                fragment = SelectionHeadFragment.newInstance();
                break;
            case BODY:
                fragment = SelectionBodyFragment.newInstance();
                break;
            case LEFT_ARM:
                fragment = SelectionArmLeftFragment.newInstance();
                break;
            case RIGHT_ARM:
                fragment = SelectionArmRightFragment.newInstance();
                break;
            case LEFT_LEG:
                fragment = SelectionLegLeftFragment.newInstance();
                break;
            case RIGHT_LEG:
                fragment = SelectionLegRightFragment.newInstance();
                break;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.body_frame, fragment)
                .commit();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_body_part, menu);
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
}
