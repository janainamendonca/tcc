package br.furb.corpusmapping;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

/**
 * Created by Janaina on 26/09/2015.
 */
public class BodyDiagramTabsAdapter extends FragmentPagerAdapter {
    String[] titulosAbas;
    TypedArray bgColors;
    TypedArray textColors;

    public BodyDiagramTabsAdapter(Context ctx, FragmentManager fm) {
        super(fm);
        titulosAbas = ctx.getResources().getStringArray(R.array.bodyParts);
        // bgColors = ctx.getResources().obtainTypedArray(R.array.cores_bg);
        //textColors = ctx.getResources().obtainTypedArray(R.array.cores_texto);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = ViewHeadMolesFragment.newInstance();
                break;
            case 1:
                fragment = ViewBodyMolesFragment.newInstance();
                break;
            case 2:
                fragment = ViewArmMolesFragment.newInstance();
                break;
            case 3:
                fragment = ViewLegMolesFragment.newInstance();
                break;


        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        return titulosAbas[position].toUpperCase(l);
    }
}
