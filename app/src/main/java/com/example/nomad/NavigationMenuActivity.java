package com.example.nomad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

public class NavigationMenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_menu);
        //new codes
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_home:
                startActivity(new Intent(
                        NavigationMenuActivity.this,
                        MainActivity.class
                ));
                break;
            case R.id.nav_checkup:
                startActivity(new Intent(
                        NavigationMenuActivity.this,
                        MainActivity.class
                ));
                break;
            case R.id.nav_maps:
                String uri = "https://www.google.com/maps/d/u/0/viewer?mid=1dJjQ4jCUPR89umU3AIBPn59R9Pqp77Ms&ll=0.40092934615056425%2C32.57792117264799&z=12\n";
                Intent locIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(locIntent);
                break;
            case R.id.nav_patients:
                startActivity(new Intent(
                        NavigationMenuActivity.this,
                        MainActivity.class
                ));
                break;
            case R.id.nav_addpatient:
                startActivity(new Intent(
                        NavigationMenuActivity.this,
                        MainActivity.class
                ));
                break;
            case R.id.nav_call:
                //makecall();
                startActivity(new Intent(
                        NavigationMenuActivity.this,
                        MainActivity.class
                ));
                break;
            case R.id.nav_discharge:
                //makecall();
                startActivity(new Intent(
                        NavigationMenuActivity.this,
                        MainActivity.class
                ));
                break;
           /* case R.id.nav_photo:
                //startActivity(new Intent(MainActivity.this, UploadImageActivity.class));
                startActivity(new Intent(MainActivity.this, ReportActivity.class));
                break; */
            case R.id.nav_logout:
                //Toast.makeText(MainActivity.this, "Logging out ... ", Toast.LENGTH_SHORT).show();
                //StyleableToast.makeText(MainActivity.this, "Logging out ...", R.style.exampleToast).show();
                startActivity(new Intent(
                        NavigationMenuActivity.this,
                        LoginActivity.class
                ));
                break;
            /*case R.id.nav_addfacility:
                //Toast.makeText(MainActivity.this, "Logging out ... ", Toast.LENGTH_SHORT).show();
                //StyleableToast.makeText(MainActivity.this, "Logging out ...", R.style.exampleToast).show();
                startActivity(new Intent(MainActivity.this, AddFacilityActivity.class));
                break; */

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}