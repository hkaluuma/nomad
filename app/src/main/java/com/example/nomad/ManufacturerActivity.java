package com.example.nomad;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

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

public class ManufacturerActivity extends AppCompatActivity {
    Config config = new Config();
    String manufacture_url = "https://" + config.server_ip + "/nomad/manufacturer.php";
    String manufacture, selectedcat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manufacturer);
        // making references to the xml

        EditText manufacturer = findViewById(R.id.manufacturer);
        Spinner category1 = findViewById(R.id.category);
        Button btn12 = findViewById(R.id.create_btn12);

        // data source from where the spinner will pick
        String[] category = {"8888", "****", "??????"};

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(ManufacturerActivity.this, R.layout.spinner_item, R.id.TextView_spinner, category);
        // assigning Array adapter to the spinner
        category1.setAdapter(catAdapter);
        // selecting an Item from the spinner
        category1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int k, long id) {
                selectedcat = category[k];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedcat = "not specified";

                Toast.makeText(ManufacturerActivity.this, "Select category", Toast.LENGTH_SHORT).show();
            }
        });

        // setting the Onclick listener

        btn12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manufacture = manufacturer.getText().toString();

                if (manufacture.isEmpty()) {
                    manufacturer.setError("manufacturer is required");
                } else if (manufacture.equals("not specified")) {

                    Toast.makeText(ManufacturerActivity.this, "Select Category", Toast.LENGTH_SHORT).show();
                }
                // else if (!haveNetworkConnection())
                Toast.makeText(ManufacturerActivity.this, " No internet Connected", Toast.LENGTH_SHORT).show();
                // else
                {
                    //    new Manufactureclass().execute ();
                }

            }
        });

        //inner class
        @SuppressLint("StaticFieldLeak")
        @SuppressWarnings("deprecation")
        class Manufacturerclass extends AsyncTask<String, String, String> {
            String responsefromphp;
            //create progress dialog class
            ProgressDialog pdialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //show dialog while accessing the inventory
                pdialog = new ProgressDialog(ManufacturerActivity.this);
                pdialog.setMessage("Please wait...");
                pdialog.setIndeterminate(false);//holds till process is done
                pdialog.setCancelable(false);// set screen in freez
                pdialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                //uploading data to the database
                try {
                    DefaultHttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(manufacture_url);
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<>(3);
                    nameValuePairs.add(new BasicNameValuePair("Manufacturer", manufacture));
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
                if (responsefromphp.equals("0")) {
                    Toast.makeText(ManufacturerActivity.this, "Inventory not updated.", Toast.LENGTH_SHORT).show();
                } else if (responsefromphp.equals("1")) {
                    Toast.makeText(ManufacturerActivity.this, "Inventory updated Successful.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ManufacturerActivity.this, "Technical Failure. Contact Admin.", Toast.LENGTH_SHORT).show();
                }


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
    }
}


