package com.example.nomad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/** @noinspection deprecation*/
public class SubCountriesActivity extends AppCompatActivity {
    String selectedDistrict,sub_area;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_countries);


        // making references to the widgets in the xml

        EditText sub_district1 = findViewById(R.id.sub_district1);
        Spinner district1 = findViewById(R.id.district1);
        Button btn13 = findViewById(R.id.create_btn13);
        // data source from where the spinner will pick
        String[] DistrictName ={"Kampala","Wakiso","Nakasero"};

        //configuring the array adapter for the spinner-acts the glue

        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(SubCountriesActivity.this,R.layout.spinner_item,R.id.TextView_spinner,DistrictName);
        // assign Array adapter to the spinner
        district1.setAdapter(districtAdapter);

        // selecting an Item
        district1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long id) {
                selectedDistrict=DistrictName[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedDistrict="not specified";

            }
        });

        // setting an onclick listener
        btn13.setOnClickListener(new View.OnClickListener() {
            /**
             */
            @Override
            public void onClick(View v) {
                // area =district1.getText().toString();
                sub_area = sub_district1.getText().toString();


                if (sub_area.isEmpty()) {
                    sub_district1.setError("select sub-district");
                } else if (selectedDistrict.equals("not specified")) {
                    Toast.makeText(SubCountriesActivity.this, "Select District", Toast.LENGTH_SHORT).show();

                }
                // Boolean haveNetworkConnection;

                if (haveNetworkConnection())
                    Toast.makeText(SubCountriesActivity.this, " Connected", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(SubCountriesActivity.this, "No internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
            //method to check if user connected to the internet
            private boolean haveNetworkConnection() {

                boolean haveConnectedWifi = false;
                boolean haveConnectedMobile = false;
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo[] netInfo = cm.getAllNetworkInfo();
                for (NetworkInfo ni : netInfo) {
                    if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                        if (ni.isConnectedOrConnecting())
                            haveConnectedWifi = true;
                    if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                        if (ni.isConnectedOrConnecting()) haveConnectedMobile = true;
                }
                return haveConnectedWifi || haveConnectedMobile;
            }

        });
    }
}




