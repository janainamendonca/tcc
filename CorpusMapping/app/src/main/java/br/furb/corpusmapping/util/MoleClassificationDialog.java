package br.furb.corpusmapping.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.RadioButton;

import br.furb.corpusmapping.R;
import br.furb.corpusmapping.data.MoleClassification;
import br.furb.corpusmapping.data.MoleGroup;
import br.furb.corpusmapping.data.MoleGroupRepository;

/**
 * Created by Janaina on 23/09/2015.
 */
public class MoleClassificationDialog {

    public static void show(final Activity activity, final MoleGroup moleGroup, final DialogInterface.OnClickListener okClickListner) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle("Classificação da pinta:");
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_mole_classification, null);
        builder.setView(view);

        final RadioButton rbNormal = (RadioButton) view.findViewById(R.id.rbNormal);
        final RadioButton rbAttention = (RadioButton) view.findViewById(R.id.rbAttention);
        final RadioButton rbDanger = (RadioButton) view.findViewById(R.id.rbDanger);
        MoleClassification classification = moleGroup.getClassification();
        if (classification != null) {
            switch (classification) {
                case NORMAL:
                    rbNormal.setChecked(true);
                    break;
                case ATTENTION:
                    rbAttention.setChecked(true);
                    break;
                case DANGER:
                    rbDanger.setChecked(true);
                    break;
            }
        }

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (rbNormal.isChecked()) {
                    moleGroup.setClassification(MoleClassification.NORMAL);
                } else if (rbAttention.isChecked()) {
                    moleGroup.setClassification(MoleClassification.ATTENTION);
                } else if (rbDanger.isChecked()) {
                    moleGroup.setClassification(MoleClassification.DANGER);
                }
                MoleGroupRepository.getInstance(activity).save(moleGroup);
                if (okClickListner != null) {
                    okClickListner.onClick(dialog, id);
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
