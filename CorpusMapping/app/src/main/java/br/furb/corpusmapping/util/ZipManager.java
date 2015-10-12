package br.furb.corpusmapping.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by Janaina on 12/10/2015.
 */
public class ZipManager {

    private static final int BUFFER = 80000;

    public static void zip(Context context, List<String> _files, String json, OutputStream dest) {
        try {
            BufferedInputStream origin = null;
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[BUFFER];

            for (int i = 0; i < _files.size(); i++) {
                Log.v("Compress", "Adding: " + _files.get(i));

                InputStream stream = context.getContentResolver().openInputStream(ImageUtils.getImageUri( _files.get(i)));

                origin = new BufferedInputStream(stream, BUFFER);

                ZipEntry entry = new ZipEntry(_files.get(i));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            ZipEntry entry = new ZipEntry("CorpusMapping.json");
            out.putNextEntry(entry);
            out.write(json.getBytes("UTF-8"));

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unzip(String _zipFile, String _targetLocation) {

        //create target location folder if not exist
        dirChecker(_targetLocation);

        try {
            FileInputStream fin = new FileInputStream(_zipFile);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                //create dir if required while unzipping
                if (ze.isDirectory()) {
                    dirChecker(ze.getName());
                } else {
                    FileOutputStream fout = new FileOutputStream(_targetLocation + ze.getName());
                    for (int c = zin.read(); c != -1; c = zin.read()) {
                        fout.write(c);
                    }

                    zin.closeEntry();
                    fout.close();
                }
            }
            zin.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void dirChecker(String dir) {
        File f = new File(dir);
        if (!f.isDirectory()) {
            f.mkdirs();
        }
    }

}
