package br.furb.corpusmapping.ui.data;

import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import br.furb.corpusmapping.util.EventBus;
import br.furb.corpusmapping.util.errors.AppError;

/**
 * Created by Janaina on 11/10/2015.
 */
public class BaseActivity extends ActionBarActivity {
    private final Object eventHandler = new Object() {
        @Subscribe
        public void onAppError(AppError error) {
            onHandleError(error);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        getEventBus().register(eventHandler);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getEventBus().unregister(eventHandler);
    }

    protected EventBus getEventBus() {
        return EventBus.getInstance();
    }

    protected void onHandleError(AppError error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
    }
}
