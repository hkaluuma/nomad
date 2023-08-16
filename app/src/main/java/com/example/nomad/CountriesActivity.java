package com.example.nomad;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
public class CountriesActivity extends AppCompatActivity {
    //Instantiating the config class
    Config config = new Config();
    String country_url = "https://" + config.server_ip + "/nomad/country.php";
    //Global variables
    String country;


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countries);
            // making reference to the xml
            EditText countryName = findViewById(R.id.countryName);
            Button btn4 = findViewById(R.id.create_btn4);

            // setting the button click
            btn4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    country = countryName.getText().toString();

                    if (country.isEmpty()) {
                        countryName.setError("Country of origin is required");

                    } else if (!haveNetworkConnection())
                        Toast.makeText(CountriesActivity.this, "No internet Connection ", Toast.LENGTH_SHORT).show();
                    else {

                        @SuppressLint("StaticFieldLeak")
                        class Countriesclass extends AsyncTask<String, String, String> {
                            String responsefromphp;
                            //create progress dialog class
                            ProgressDialog pdialog;

                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                //show dialog while accessing the inventory
                                pdialog = new ProgressDialog(CountriesActivity.this);
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
                                    HttpPost httppost = new HttpPost(country_url);
                                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<>(3);
                                    nameValuePairs.add(new BasicNameValuePair("country", country));

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
                                    Toast.makeText(CountriesActivity.this, "Unable to Access The District data.", Toast.LENGTH_SHORT).show();
                                } else if (responsefromphp.equals("1")) {
                                    Toast.makeText(CountriesActivity.this, "District added Successful.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(CountriesActivity.this, "Technical Failure. Contact Admin.", Toast.LENGTH_SHORT).show();
                                }
                            }
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
