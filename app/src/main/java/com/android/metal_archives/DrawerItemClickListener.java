package com.android.metal_archives;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;



/**
 * Created by bej2ply on 8/10/2017.
 */

public class DrawerItemClickListener implements ListView.OnItemClickListener {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //selectItem(position);
    }
}

//
///** Swaps fragments in the main content view */
//private void selectItem(int position) {
//    // Create a new fragment and specify the planet to show based on position
//    Fragment fragment = new MenuFragment();
//    Bundle args = new Bundle();
//    args.putInt(MenuFragment.ARG_PLANET_NUMBER, position);
//    fragment.setArguments(args);
//
//    // Insert the fragment by replacing any existing fragment
//    FragmentManager fragmentManager = getFragmentManager();
//    fragmentManager.beginTransaction()
//            .replace(R.id.content_frame, fragment)
//            .commit();
//
//    // Highlight the selected item, update the title, and close the drawer
//    drawer_list.setItemChecked(position, true);
//    setTitle(menu_options[position]);
//    drawer_layout.closeDrawer(drawer_list);
//}
//
//@Override
//public void setTitle(CharSequence title) {
//    mTitle = title;
//    getActionBar().setTitle(mTitle);
//}