package com.example.nomad;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

/** @noinspection deprecation*/
public class SuppliersActivity extends AppCompatActivity {
    Config config = new Config();
    String supplier_url = "https://" + config.server_ip + "/nomad/supplier.php";
    String  supply_name, supply_category;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suppliers);

        // Referencing to the xml widgets
        EditText supplyName= findViewById(R.id.supplier2);
       Spinner supplyCategory = findViewById(R.id.category);
        Button btn8 = findViewById(R.id.create_btn8);

        //setting an onclick method to trigger the action

        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supply_name = supplyName.getText().toString();
                //supply_category = supplyCategory.getText().toString();

                if (supply_name.isEmpty()) {
                    supplyName.setError("supplier name is required");
                }
                else if ( supplyCategory.equals("not specified")) {
                    Toast.makeText(SuppliersActivity.this, "Select Category", Toast.LENGTH_SHORT).show();

                }else

                {
                    new Suppliersclass().execute();
                }


            }
            /** @noinspection deprecation*/
            @SuppressLint("StaticFieldLeak")
            class Suppliersclass extends AsyncTask<String, String, String> {
                String responsefromphp;
                //creating progress dialog class
                ProgressDialog pdialog;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    //show dialog while accessing the inventory
                    pdialog = new ProgressDialog(SuppliersActivity.this);
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
                        HttpPost httppost = new HttpPost(supplier_url);
                        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>(3);
                        nameValuePairs.add(new BasicNameValuePair("supplier_name", supply_name));
                        nameValuePairs.add(new BasicNameValuePair("sup_category", supply_category));
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
                        Toast.makeText(SuppliersActivity.this, "Supplier not registered sucessfully.", Toast.LENGTH_SHORT).show();
                    } else if (responsefromphp.equals("1")) {
                        Toast.makeText(SuppliersActivity.this, "Suppliers category Successfully updated.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SuppliersActivity.this, "Technical Failure. Contact Admin.", Toast.LENGTH_SHORT).show();
                        // is the intent
                        Intent intent = new Intent(getApplicationContext(), SuppliersActivity.class);
                        startActivity(intent);
                    }

                }

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

        });

    }
}

