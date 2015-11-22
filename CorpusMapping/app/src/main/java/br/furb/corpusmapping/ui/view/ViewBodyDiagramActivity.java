package br.furb.corpusmapping.ui.view;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import br.furb.corpusmapping.R;
import br.furb.corpusmapping.ui.common.tablayout.SlidingTabLayout;


public class ViewBodyDiagramActivity extends ActionBarActivity {

    ViewPager mViewPager;
    SlidingTabLayout mSlidingTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_body_diagram);

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));

        BodyDiagramTabsAdapter pagerAdapter = new BodyDiagramTabsAdapter(this, getSupportFragmentManager());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewPager = (ViewPager)findViewById(R.id.pager);
        mViewPager.setAdapter(pagerAdapter);

        mSlidingTabLayout = (SlidingTabLayout)findViewById(R.id.tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

}
