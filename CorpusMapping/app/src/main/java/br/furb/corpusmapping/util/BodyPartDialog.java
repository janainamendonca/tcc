package br.furb.corpusmapping.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;

import br.furb.corpusmapping.R;
import br.furb.corpusmapping.SpecificBodyPart;
import br.furb.corpusmapping.data.MoleGroup;

/**
 * Created by Janaina on 28/09/2015.
 */
public class BodyPartDialog {

    public static void show(Activity activity, SpecificBodyPart bodyPart, MoleGroup moleGroup) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_body_part_image, null);

        builder.setView(view);
        ImageView img = (ImageView) view.findViewById(R.id.imgBodyPart);
        ImageDrawer.drawPoint(img, bodyPart.getResource(), moleGroup.getPosition());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
