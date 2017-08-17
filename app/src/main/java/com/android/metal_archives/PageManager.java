package com.android.metal_archives;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.lang.reflect.Array;

/**
 * Author: James Berry
 * Description: Fragment page manager
 */

public class PageManager extends FragmentPagerAdapter {
    private final int TAB_COUNT = 4;
    private Context context;

    public PageManager(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        Log.i("PAGE MANAGER", "position: " + position);

        if (position == 0) {
            fragment =  HomeFragment.newInstance(position);
            Log.i("generating fragments", "fragment info: " + fragment.getArguments());
        } else if (position == 1){
            fragment = SearchFragment.newInstance(position);
            Log.i("generating fragments", "fragment info: " + fragment.getArguments());
        } else if (position == 2){
            fragment = NotificationFragment.newInstance(position);
            Log.i("generating fragments", "fragment info: " + fragment.getArguments());
        } else if (position == 3){
            fragment = SocialFragment.newInstance(position);
            Log.i("generating fragments", "fragment info: " + fragment.getArguments());
        } else {
            Log.e("PageManager.java", "error, position is not 0-3");
        }

        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null; // no tab titles, just icons
    }
}
