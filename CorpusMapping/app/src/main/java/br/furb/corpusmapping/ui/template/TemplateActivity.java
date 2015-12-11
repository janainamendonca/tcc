package br.furb.corpusmapping.ui.template;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import br.furb.corpusmapping.R;
import br.furb.corpusmapping.util.ImageDrawer;

/**
 * Activity para orientar o usuário na criação do gabarito.
 *
 * @author Janaina Carraro Mendonça Lima
 */
public class TemplateActivity extends ActionBarActivity implements View.OnClickListener {

    public static void start(Context context) {
        Intent intent = new Intent(context, TemplateActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template);
        findViewById(R.id.imgViewTemplate).setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_template, menu);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = this.getLayoutInflater().inflate(R.layout.dialog_template, null);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
