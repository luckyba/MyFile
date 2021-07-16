package com.luckyba.myfile;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.luckyba.myfile.app.MyApplication;
import com.luckyba.myfile.audios.AudiosListFragment;
import com.luckyba.myfile.common.CommonListener;
import com.luckyba.myfile.helper.ArcProgress;
import com.luckyba.myfile.images.ImagesListFragment;
import com.luckyba.myfile.settings.SettingsFragment;
import com.luckyba.myfile.storage.StorageFragment;
import com.luckyba.myfile.utils.Constant;
import com.luckyba.myfile.utils.Utils;
import com.luckyba.myfile.videos.VideosListFragment;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG_INTERNAL_STORAGE = "INTERNAL STORAGE";
    private static final String TAG_EXTERNAL_STORAGE = "EXTERNAL STORAGE";
    private static final String TAG_IMAGES_LIST = "IMAGES";
    private static final String TAG_AUDIOS_LIST = "AUDIOS";
    private static final String TAG_VIDEOS_LIST = "VIDEOS";
    private static final String TAG_SETTINGS = "SETTINGS";
    public static String FG_TAG = TAG_INTERNAL_STORAGE;
    public static int navItemIndex = 3;
    NavigationView navigationView;
    private DrawerLayout drawer;
    private String[] activityTitles;
    private Handler mHandler;
    public static CommonListener.CommunicationActivity buttonBackPressListener;
    private static final String TAG = MainActivity.class.getSimpleName();
    private ArcProgress progressStorage;
    private TextView tvFreeStorage;
    private Handler handler;
    private Runnable runnable;
    private int i = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getColor(R.color.action_bar_back_ground));
        getWindow().setNavigationBarDividerColor(getColor(R.color.action_bar_back_ground));
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        mHandler = new Handler();
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                loadHomeFragment();
            }
        };
        drawer.addDrawerListener(toggle);

        drawer.setStatusBarBackgroundColor(getColor(R.color.my_file_back_ground));

        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);
        progressStorage = (ArcProgress) headerLayout.findViewById(R.id.progress_storage);
        tvFreeStorage = (TextView) headerLayout.findViewById(R.id.id_free_space);
        navigationView.setNavigationItemSelectedListener(this);
        if (savedInstanceState == null) {
            navItemIndex = 3;
            FG_TAG = TAG_INTERNAL_STORAGE;
            navigationView.setCheckedItem(R.id.nav_internal_storage);
            loadHomeFragment();
            setRamStorageDetails(navItemIndex);
            if (sharedPref.getBoolean("first_time", true)) {
                final Dialog homeGuideDialog = new Dialog(MainActivity.this, android.R.style.Theme_Translucent_NoTitleBar);

                homeGuideDialog.setContentView(R.layout.custom_guide_dialog);
                homeGuideDialog.show();
                RelativeLayout layout = (RelativeLayout) homeGuideDialog.findViewById(R.id.guide_layout);
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        homeGuideDialog.dismiss();
                    }
                });
            }

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("first_time", false);
            editor.apply();
        }
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                handler.postDelayed(this, 1000);
                if (i > -1) {
                    i--;
                } else {

                    handler.removeCallbacks(runnable);
                }
            }
        };
        runnable.run();
    }


    private void setActivityTitle() {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void loadHomeFragment() {
        setActivityTitle();
        invalidateOptionsMenu();
        if (getSupportFragmentManager().findFragmentByTag(FG_TAG) != null) {
            // getSupportFragmentManager().popBackStack(FG_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            drawer.closeDrawers();
            return;
        }
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main_internal_storage content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, FG_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 3:
                return StorageFragment.newInstance(Constant.INTERNAL_STORAGE, "internal");
            case 4:
                return StorageFragment.newInstance(Constant.EXTERNAL_STORAGE, "external");
            case 0:
                return new ImagesListFragment();
            case 1:
                return new AudiosListFragment();
            case 2:
                return new VideosListFragment();
            case 5:
                return new SettingsFragment();
            default:
                return StorageFragment.newInstance(Constant.INTERNAL_STORAGE, "internal");
        }
    }

    private void removeFragment() {
        if (getSupportFragmentManager().findFragmentByTag(FG_TAG) == null) {
            try {
                getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.frame)).commit();
            } catch (Exception e) {
                MyApplication.getInstance().trackException(e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.main_internal_storage, menu);
            return true;
        }
        if (navItemIndex == 4) {
            getMenuInflater().inflate(R.menu.main_external_storage, menu);
            return true;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_new_folder) {
            StorageFragment storageFragment = (StorageFragment) getSupportFragmentManager().findFragmentByTag(FG_TAG);
            if (storageFragment != null) {
                storageFragment.createNewFolder();
            }
            return true;
        } else if (id == R.id.action_new_file) {
            StorageFragment storageFragment = (StorageFragment) getSupportFragmentManager().findFragmentByTag(FG_TAG);
            if (storageFragment != null) {
                storageFragment.createNewFile();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_internal_storage:
                navItemIndex = 3;
                FG_TAG = TAG_INTERNAL_STORAGE;
                setRamStorageDetails(navItemIndex);
                break;
            case R.id.nav_external_storage:
                navItemIndex = 4;
                FG_TAG = TAG_EXTERNAL_STORAGE;
                setRamStorageDetails(navItemIndex);
                break;
            case R.id.nav_images:
                navItemIndex = 0;
                FG_TAG = TAG_IMAGES_LIST;
                break;
            case R.id.nav_audios:
                navItemIndex = 1;
                FG_TAG = TAG_AUDIOS_LIST;
                break;
            case R.id.nav_videos:
                navItemIndex = 2;
                FG_TAG = TAG_VIDEOS_LIST;
                break;

            case R.id.nav_settings:
                navItemIndex = 5;
                FG_TAG = TAG_SETTINGS;
                break;
        }
        navigationView.setCheckedItem(id);
        removeFragment();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setRamStorageDetails(int navItemIndex) {
        if (navItemIndex == 3) {
            tvFreeStorage.setText(Utils.getAvailableInternalMemorySize());
            progressStorage.setProgress(Utils.getAvailableInternalStoragePercentage());
        } else if (navItemIndex == 4) {
            tvFreeStorage.setText(Utils.getAvailableExternalMemorySize());
            progressStorage.setProgress(Utils.getAvailableExternalStoragePercentage());

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (navItemIndex != 3) {
                navItemIndex = 3;
                FG_TAG = TAG_INTERNAL_STORAGE;
                navigationView.setCheckedItem(R.id.nav_internal_storage);
                loadHomeFragment();
            } else {
                buttonBackPressListener.onBackPressed(navItemIndex);
            }
        }
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}
