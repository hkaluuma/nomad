package com.example.nomad;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class SuppliersActivity extends AppCompatActivity {
    Config config = new Config();
    String supplier_url = "https://" + config.server_ip + "/nomad/supplier.php";
    String  supply_name, supply_category;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suppliers);
    }
}