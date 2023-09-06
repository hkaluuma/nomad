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
public class MaintenanceRequestsActivity extends AppCompatActivity {
    //Instantiating the config class
    Config config = new Config();
    String maintenance_url= "https://"+config.server_ip+"/nomad/maintenance.php";
    // global variables
    String equipment, facility, department, condition, inventory, model, serial, description, selectedEquipment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_requests);
        //making references to the xml widgets
        //Toolbar toolbar4 = findViewById(R.id.toolbar7);
        Button btn8 = findViewById(R.id.create_btn8);
        Spinner edEquipment = findViewById(R.id.equipment);
        EditText edFacility = findViewById(R.id.facility1);
        EditText edDepartment = findViewById(R.id.department2);
        EditText edCondition = findViewById(R.id.condition1);
        EditText edInventory = findViewById(R.id.inventoryName);
        EditText edModel = findViewById(R.id.modelNumber);
        EditText edSerial = findViewById(R.id.Serial1);
        EditText edDescription = findViewById(R.id.description);

        //setSupportActionBar(toolbar4);
        //Enabling the navigation
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        // data source from where the spinner will pick
        String[] Equipment = {"****", "needle", "injection"};
        //configuring the array adapter for the spinner-acts the glue

        ArrayAdapter<String> eqAdapter = new ArrayAdapter<>(MaintenanceRequestsActivity.this, R.layout.spinner_item, R.id.TextView_spinner,Equipment );

        // assigning Array adapter to the spinner

        edEquipment.setAdapter(eqAdapter);

        // selecting an Item from the spinner
        edEquipment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int k, long id) {
                selectedEquipment= Equipment [k];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedEquipment="not specified";

                Toast.makeText(MaintenanceRequestsActivity.this, "Select Equipment", Toast.LENGTH_SHORT).show();
            }
        });
        //setting a button click mtd
        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // equipment = edEquipment.getText().toString();
                facility = edFacility.getText().toString();
                department = edDepartment.getText().toString();
                condition = edCondition.getText().toString();
                inventory =edInventory.getText().toString();
                model = edModel.getText().toString();
                serial =edSerial .getText().toString();
                description = edDescription.getText().toString();

                if (condition.isEmpty()) {
                    edCondition.setError("condition is required");
                }
                else if (serial.isEmpty()) {
                    edSerial.setError("serialNo is required");
                }
                else {
                    if (!haveNetworkConnection())
                        Toast.makeText(MaintenanceRequestsActivity.this, " No internet Connected", Toast.LENGTH_SHORT).show();
                    else {
                        new maintenanceclass().execute();

                    }
                }
            }


            @SuppressLint("StaticFieldLeak")
            @SuppressWarnings("deprecation")
            class maintenanceclass extends AsyncTask<String, String, String> {
                String responsefromphp;
                //create progress dialog class
                ProgressDialog pdialog;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();

                    //show dialog while processing the request

                    pdialog = new ProgressDialog(MaintenanceRequestsActivity.this);
                    pdialog.setMessage("Please wait...");
                    pdialog.setIndeterminate(false);//holds till process is done
                    pdialog.setCancelable(false);// set screen in freez mode
                    pdialog.show();
                }


                @Override
                protected String doInBackground(String... strings) {
                    //upload data to the database
                    try {
                        DefaultHttpClient httpclient = new DefaultHttpClient();
                        HttpPost httppost = new HttpPost(maintenance_url);
                        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>(3);
                        nameValuePairs.add(new BasicNameValuePair("select equipment", equipment));
                        nameValuePairs.add(new BasicNameValuePair(" Facility", facility));
                        nameValuePairs.add(new BasicNameValuePair("Department", department));
                        nameValuePairs.add(new BasicNameValuePair("Condition", condition));
                        nameValuePairs.add(new BasicNameValuePair("Inventory", inventory));
                        nameValuePairs.add(new BasicNameValuePair("Model", model));
                        nameValuePairs.add(new BasicNameValuePair("Serial", serial));
                        nameValuePairs.add(new BasicNameValuePair("Description", description));
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
                        Toast.makeText(getApplicationContext(), "Try Again", Toast.LENGTH_LONG).show();
                    }
                    return responsefromphp;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    pdialog.dismiss();
                    if (responsefromphp.equals("0")) {
                        Toast.makeText(MaintenanceRequestsActivity.this, "Request not  sent .", Toast.LENGTH_SHORT).show();
                    } else if (responsefromphp.equals("1")) {
                        Toast.makeText(MaintenanceRequestsActivity.this, "Request sent Successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MaintenanceRequestsActivity.this, "Technical Failure. Contact Admin.", Toast.LENGTH_SHORT).show();
                    }
                }

            }

        });
    }

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

}



