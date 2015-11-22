package br.furb.corpusmapping.ui.common;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import br.furb.corpusmapping.R;
import br.furb.corpusmapping.data.model.MoleClassification;

public class SpinnerClassificationAdapter extends BaseAdapter {

    private Activity activity;
    public  SpinnerClassificationAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return MoleClassification.values().length;
    }

    @Override
    public Object getItem(int position) {
        return MoleClassification.values()[position];
    }

    @Override
    public long getItemId(int position) {
        return MoleClassification.values()[position].ordinal();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity)
                    .inflate(R.layout.item_classification, null);
            holder = new ViewHolder();
            holder.txtClassification = (TextView) convertView.findViewById(R.id.txtDescription);
            holder.imgClassification = (ImageView) convertView.findViewById(R.id.imgView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txtClassification.setText(MoleClassification.values()[position].getDescription());
        holder.imgClassification.setImageResource(MoleClassification.values()[position].getResource());
        return convertView;
    }

    class ViewHolder {
        ImageView imgClassification;
        TextView txtClassification;
    }
}