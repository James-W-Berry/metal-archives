package com.android.metal_archives;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

/**
 * Author: James Berry
 * Description: point of entry containing basic functions
 * such as search, navigation drawer, and updates
 */

public class Home extends AppCompatActivity {

    private DrawerLayout drawer_layout;
    public Toolbar toolbar;
    public Toolbar search_toolbar;
    public EditText search_text;
    private NavigationView nav_drawer;
    private ActionBarDrawerToggle drawerToggle;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context = this;

        // toolbar to replace default toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // TODO: instead of a searchview, create an editText that looks like a searchview to launch the searchable activity
        //search_toolbar = (Toolbar) findViewById(R.id.searchable_toolbar);
        search_text = (EditText) findViewById(R.id.search_text);
        search_text.setOnClickListener(new View.OnClickListener() {
            Intent intent;
            @Override
            public void onClick(View v) {
                //launch search activity
                intent = new Intent(context, SearchableActivity.class);
                startActivity(intent);
            }
        });

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // drawer set up
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        drawer_layout.addDrawerListener(drawerToggle);
        nav_drawer = (NavigationView) findViewById(R.id.nav_view);
        setupDrawerContent(nav_drawer);

        // main view and tab set up
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new PageManager(getSupportFragmentManager(), Home.this));
        viewPager.setOffscreenPageLimit(3);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.mipmap.ic_home_black_48dp);
        tabLayout.getTabAt(1).setIcon(R.mipmap.ic_search_black_48dp);
        tabLayout.getTabAt(2).setIcon(R.mipmap.ic_notifications_none_black_48dp);
        tabLayout.getTabAt(3).setIcon(R.mipmap.ic_group_black_48dp);

    }

    // search toolbar on click listener
//    @Override
//    public void setOnSearchClickListener(View.OnClickListener listener){
//
//    }

    public void setTitle(String title) {
        toolbar.setTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new activity for each selectable item
        Intent intent;

        switch(menuItem.getTitle().toString()){
            case "Profile":
                intent = new Intent(this, ProfileActivity.class);
                break;
            case "Settings":
                intent = new Intent(this, SettingsActivity.class);
                break;
            case "Info":
                intent = new Intent(this, InfoActivity.class);
                break;
            case "Log In" :
                intent = new Intent(this, UserLogActivity.class);
                break;
            default:
                intent = new Intent(this, Home.class);
                break;
        }

        menuItem.setChecked(true);
        drawer_layout.closeDrawers();
        startActivity(intent);
    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE 1: Make sure to override the method with only a single `Bundle` argument
    // Note 2: Make sure you implement the correct `onPostCreate(Bundle savedInstanceState)` method.
    // There are 2 signatures and only `onPostCreate(Bundle state)` shows the hamburger icon.
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
        setTitle("Home");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }


}
