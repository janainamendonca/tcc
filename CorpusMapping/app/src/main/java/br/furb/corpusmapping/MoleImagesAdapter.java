package br.furb.corpusmapping;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import br.furb.corpusmapping.data.ImageRecord;
import br.furb.corpusmapping.util.ImageUtils;

public class MoleImagesAdapter extends BaseAdapter {

    Context ctx;
    List<ImageRecord> images;

    public MoleImagesAdapter(Context ctx, List<ImageRecord> images) {
        this.ctx = ctx;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return images.get(position).getMoleGroupId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1 passo
        ImageRecord image = images.get(position);
        // 2 passo
        ViewHolder holder = null;
        if (convertView == null) {
            Log.d("NGVL", "View Nova => position: " + position);
            convertView = LayoutInflater.from(ctx)
                    .inflate(R.layout.item_mole, null);

            holder = new ViewHolder();
            holder.imgMole = (ImageView) convertView.findViewById(R.id.imgMole);
            holder.txtMole = (TextView) convertView.findViewById(R.id.txtMole);
            holder.txtDateTime = (TextView) convertView.findViewById(R.id.txtDateTime);
            holder.txtClassification = (TextView) convertView.findViewById(R.id.txtClassification);
            convertView.setTag(holder);
        } else {
            Log.d("NGVL", "View existente => position: " + position);
            holder = (ViewHolder) convertView.getTag();
        }
        // 3 passo
        Uri imageUri = ImageUtils.getImageUri(image.getImagePath());
        if (imageUri == null) {
            holder.imgMole.setImageResource(R.drawable.ic_images25);
        } else {
            holder.imgMole.setImageBitmap(ImageUtils.decodeSampledBitmapFromResource(ctx, imageUri, 100, 100));
        }
        holder.txtMole.setText(image.getMoleGroup().getGroupName());
        holder.txtDateTime.setText(image.getImageDate().toString("dd/MM/yyyy HH:mm"));

        // 4 passo
        return convertView;
    }

    static class ViewHolder {
        ImageView imgMole;
        TextView txtMole;
        TextView txtDateTime;
        TextView txtClassification;
    }

}
