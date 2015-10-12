package br.furb.corpusmapping.ui.common;

import android.support.v7.app.ActionBarActivity;
import android.view.ActionMode;
import android.widget.Toast;

import br.furb.corpusmapping.util.EventBus;
import br.furb.corpusmapping.util.errors.AppError;

/**
 * Created by Janaina on 11/10/2015.
 */
public class BaseActivity extends ActionBarActivity {


    protected EventBus getEventBus() {
        return EventBus.getInstance();
    }

    protected void onHandleError(AppError error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
