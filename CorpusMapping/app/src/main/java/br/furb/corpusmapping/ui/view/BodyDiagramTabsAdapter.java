package br.furb.corpusmapping.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

import br.furb.corpusmapping.R;

/**
 * Created by Janaina on 26/09/2015.
 */
public class BodyDiagramTabsAdapter extends FragmentPagerAdapter {
   private String[] titulosAbas;

    public BodyDiagramTabsAdapter(Context ctx, FragmentManager fm) {
        super(fm);
        titulosAbas = ctx.getResources().getStringArray(R.array.bodyParts);
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
