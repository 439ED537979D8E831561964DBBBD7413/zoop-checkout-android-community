package com.zoop.checkout.app;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mainente on 27/04/15.
 */
public class testeActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
   // private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private LinearLayout menuL;
    private String[] mZoopOptions;
    private  ExpandableListView expandableListView;
    private List<String> listGroup;
    private HashMap<String, List<String>> listData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.menutest);
        expandableListView = (ExpandableListView) findViewById(R.id.left_drawer);
        mZoopOptions = getResources().getStringArray(R.array.ZoopOptions_array);

        OptionsMenu Menu=new OptionsMenu();
        Menu.MountOpitions(this,expandableListView,mZoopOptions);


        mTitle = mDrawerTitle = "Zoop";
        mZoopOptions = getResources().getStringArray(R.array.ZoopOptions_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
     //   mDrawerList = (ListView) findViewById(R.id.left_drawer);
     //   buildList();
        menuL=(LinearLayout) findViewById(R.id.LinearMenu);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
     //   mDrawerList.setAdapter(new ArrayAdapter<String>(this,
       //         R.layout.listoptions, mZoopOptions));
        //mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        )

        {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };


        mDrawerLayout.setDrawerListener(mDrawerToggle);


        if (savedInstanceState == null) {
           // selectItem(0);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(Gravity.START);
      menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons

                return super.onOptionsItemSelected(item);
        }


    /* The click listner for ListView in the navigation drawer *//*
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        //Fragment fragment = new PlanetFragment();
        Bundle args = new Bundle();
        //args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
        //fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        //fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mZoopOptions[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }*/

    public void buildList(){
        listGroup = new ArrayList<String>();
        listData = new HashMap<String, List<String>>();

        // GROUP
        listGroup.add("Grupo 1");
        listGroup.add("Grupo 2");
        listGroup.add("Grupo 3");
        listGroup.add("Grupo 4");

        // CHILDREN
        List<String> auxList = new ArrayList<String>();

        listData.put(listGroup.get(0), auxList);

        auxList = new ArrayList<String>();


        auxList.add("Item 5");
        auxList.add("Item 6");
        auxList.add("Item 7");
        auxList.add("Item 8");
        listData.put(listGroup.get(1), auxList);

        auxList = new ArrayList<String>();
        auxList.add("Item 9");
        auxList.add("Item 10");
        auxList.add("Item 11");
        auxList.add("Item 12");
        listData.put(listGroup.get(2), auxList);

        auxList = new ArrayList<String>();
        auxList.add("Item 13");
        auxList.add("Item 14");
        auxList.add("Item 15");
        auxList.add("Item 16");
        listData.put(listGroup.get(3), auxList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
