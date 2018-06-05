package com.acc.study_control.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.acc.study_control.fragments.CodeFragment;
import com.acc.study_control.fragments.ItemListDialogFragment;
import com.acc.study_control.fragments.MainFragment;
import com.acc.study_control.fragments.MemberFragment;
import com.acc.study_control.models.Code;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.acc.study_control.R;
import com.acc.study_control.fragments.MainMapFragment;
import com.acc.study_control.models.User;
import com.acc.study_control.utils.RequestPermissionsUtil;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MemberFragment.OnFragmentInteractionListener,
        CodeFragment.OnFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Navigation and Toolbar
        setNavigationAndToolbar();

        // Request Permissions
        RequestPermissionsUtil.INSTANCE.requestPermissions(this);

        Code code = Code.first(Code.class);
        if (savedInstanceState == null && code == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.activity_container,
                    CodeFragment.newInstance("test_1", "test_2"), CodeFragment.class.getSimpleName()).commit();
        } else {
            changeFragment(code);
        }
        // Load Map
        //loadDashboardMap();
    }

//    private void loadDashboardMap() {
//        MainMapFragment mainMapFragment = MainMapFragment.Companion.getInstance();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction ft = fragmentManager.beginTransaction();
//        ft.replace(R.id.mapContainer, mainMapFragment);
//        ft.commit();
//    }

    private void setNavigationAndToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            finalizarActivity_Dialog();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {
            // LogOut Account
            logoutAccount();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutAccount() {
        // Clean Firebase User
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // TODO: Delete from User database
                        Log.d(TAG, "Logout Successful!");

                        // Clean User
                        User.cleanUser();
                        redirectLogin();
                    }
                });
    }

    private void redirectLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void changeFragment(Code code) {
        MainFragment mainFragment = new MainFragment();
        mainFragment.getCode(code);
//        getSupportFragmentManager().beginTransaction().add(R.id.activity_container,
//                mainFragment, "main_fragment").commit();
        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.activity_container, mainFragment);
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();

    }

    public void changeFragmentMember() {
        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.activity_container, MemberFragment.newInstance());
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();
    }

    //------------------------------------------------------------------------------------------------------------------
    public void finalizarActivity_Dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_Dialog_Salir);
        builder.setMessage("¿Desea salir De Control de Estudio?");
        builder.setCancelable(true);
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
