package com.example.nomad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.entity.UrlEncodedFormEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

/** @noinspection deprecation*/
public class FacilitiesActivity extends AppCompatActivity {
    //Instantiating the config class
    Config config = new Config();
    String facilities_url= "https://"+config.server_ip+"/nomad/facility.php";

    //Global Variables
    String facName,facLevel,facOwnership,facStatus,facCode,selectedsubRegion,selectedLevel, selectedOwner,selectedauth;

    static final String TAG ="FacilitiesActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facilties);

// making reference to the facilities widgets
        Toolbar toolbar11 = findViewById(R.id.toolbar11);
        EditText fName =  findViewById(R.id.fac_name);
        Spinner sub_districtSpinner =  findViewById(R.id.sub_district);
        Spinner flevelSpinner =findViewById(R.id.faclevelOptions);
        Spinner fownershipSpinner =findViewById(R.id.facownerOptions);
        Spinner fauthoritySpinner = findViewById(R.id.fAuthorityOptions);
        Spinner statusSpinner  = findViewById(R.id.facStatusOptions);
        EditText fcode =findViewById(R.id.code);
        Button btn3 = findViewById(R.id.create_btn3);
        setSupportActionBar(toolbar11);
        //Enabling the navigation
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // data source from where the spinner will pick
        String[] subDistrict={"Moroto","Gulu","Arua","Abim"};
        String[] facLevel ={"HCIV","HCIII","HCII","HCI","GH"};
        String[] fAuthority ={"MOH"};
        String[] facOwner ={"Private","Government"};
        String[] facStatus ={"Functional","Non-Functional"};

        //configuring the array adapter for the spinner-acts the glue

        ArrayAdapter<String> subAdapter = new ArrayAdapter<>(FacilitiesActivity.this,R.layout.spinner_item,R.id.sub_district);
        ArrayAdapter<String> levelAdapter= new ArrayAdapter<>(FacilitiesActivity.this,R.layout.spinner_item,R.id.faclevelOptions);
        ArrayAdapter<String> authAdapter = new ArrayAdapter<>(FacilitiesActivity.this,R.layout.spinner_item,R.id.facownerOptions);
        ArrayAdapter<String> ownershipAdapter = new ArrayAdapter<>(FacilitiesActivity.this,R.layout.spinner_item,R.id.fAuthorityOptions);
        ArrayAdapter<String> statAdapter = new ArrayAdapter<>(FacilitiesActivity.this,R.layout.spinner_item,R.id.facStatusOptions);

        // assign Array adapter to the spinner
        sub_districtSpinner.setAdapter(subAdapter );
        flevelSpinner.setAdapter(levelAdapter);
        fownershipSpinner .setAdapter(ownershipAdapter);
        fauthoritySpinner.setAdapter(authAdapter);
        statusSpinner.setAdapter(statAdapter);


        // selecting an Item from the spinner
        sub_districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int t, long id) {
                selectedsubRegion=subDistrict [t];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView){
                selectedsubRegion="not specified";
            }
        });
        flevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedLevel= facLevel [position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedLevel="not specified";
            }
        });
        fownershipSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int z, long id) {
                selectedOwner= facOwner [z];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedOwner="not specified";
            }
        });
        fauthoritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int q, long id) {
                selectedauth=  fAuthority [q];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedauth="not specified";
            }
        });

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int x, long id) {
                selectedauth=  facStatus [x];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedauth="not specified";
            }
        });

//set onclick listener
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facName = fName.getText().toString();

                facCode = fcode.getText().toString();

                if (facName.isEmpty()) {
                    fName.setError("facility Name is Required");
                } else if (facCode.isEmpty()) {
                    fcode.setError("code is Required");
                } else {

                    if (!haveNetworkConnection()) {
                        Toast.makeText(FacilitiesActivity.this, "No internet Connection", Toast.LENGTH_SHORT).show();

                    } else {
                        new facilityclass().execute();

                    }

                }
            }

        });
    }


    @SuppressLint("StaticFieldLeak")
    @SuppressWarnings("deprecation")
    class facilityclass extends AsyncTask<String, String, String> {
        String responsefromphp;
        //create progress dialog class
        ProgressDialog pdialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show dialog while registering a facility
            pdialog = new ProgressDialog(FacilitiesActivity.this);
            pdialog.setMessage("Please wait...");
            pdialog.setIndeterminate(false);//holds till process is done
            pdialog.setCancelable(false);// set screen in freez
            pdialog.show();
        }


        @Override
        protected String doInBackground(String... strings) {
            //upload data to the database
            try {
                DefaultHttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(facilities_url);
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<>(3);
                nameValuePairs.add(new BasicNameValuePair("facilityName",   facName));
                nameValuePairs.add(new BasicNameValuePair("facilityLevel", facLevel));
                nameValuePairs.add(new BasicNameValuePair("ownership",  facOwnership));
                nameValuePairs.add(new BasicNameValuePair("facilityStatus", facStatus));
                nameValuePairs.add(new BasicNameValuePair("facilityCode", facCode));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                InputStream inputStream = response.getEntity().getContent();
                BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream), 4096);
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
                responsefromphp = sb.toString();
                inputStream.close();
            } catch (Exception e) {
                Log.i(TAG, "doInBackground: *****" + responsefromphp);
            }
            return responsefromphp;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pdialog.dismiss();
            if(responsefromphp.equals("0")){
                Toast.makeText(FacilitiesActivity.this, "Facility details have not been not Submitted.", Toast.LENGTH_SHORT).show();
            } else if(responsefromphp.equals("1")){
                Toast.makeText(FacilitiesActivity.this, "Facility details have been  Submitted Successful.", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(FacilitiesActivity.this, "Technical Failure. Contact Admin.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @SuppressWarnings("deprecation")
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

}
