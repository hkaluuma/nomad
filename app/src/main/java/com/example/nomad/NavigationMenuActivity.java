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
            case R.id.nav_countries:
                startActivity(new Intent(
                        NavigationMenuActivity.this,
                        MainActivity.class
                ));
                break;
            case R.id.nav_districts:
                startActivity(new Intent(
                        NavigationMenuActivity.this,
                        MainActivity.class
                ));
                break;
            case R.id.nav_subcounties:
                startActivity(new Intent(
                        NavigationMenuActivity.this,
                        MainActivity.class ));
                break;
            case R.id.nav_facilities:
                startActivity(new Intent(
                        NavigationMenuActivity.this,
                        MainActivity.class
                ));
                break;
            case R.id.nav_departments:
                startActivity(new Intent(
                        NavigationMenuActivity.this,
                        MainActivity.class
                ));
                break;
            case R.id.nav_sections:
                //makecall();
                startActivity(new Intent(
                        NavigationMenuActivity.this,
                        MainActivity.class
                ));
                break;
            case R.id.nav_authorities:
                startActivity(new Intent(NavigationMenuActivity.this,
                        MainActivity.class
                ));
                break;
            case R.id.nav_equipment:
                startActivity(new Intent(
                        NavigationMenuActivity.this,
                        LoginActivity.class
                ));
                break;
            case R.id.nav_equipment_class:
                startActivity(new Intent(NavigationMenuActivity.this, MainActivity.class));
                break;

            case R.id.nav_score_sheet:
                startActivity(new Intent(NavigationMenuActivity.this, MainActivity.class));
                break;
            case R.id.nav_manufacturer:
                startActivity(new Intent(NavigationMenuActivity.this, MainActivity.class));
                break;
            case R.id.nav_suppliers:
                startActivity(new Intent(NavigationMenuActivity.this, MainActivity.class));
                break;
            default:
                startActivity(new Intent(NavigationMenuActivity.this, MainActivity.class));

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}