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
import java.util.Objects;

/** @noinspection deprecation*/
public class Forgot_password extends AppCompatActivity {
    Config config = new Config();
    String Reset_url = "https://" + config.server_ip + "/nomad/password_reset.php";
    //Global variables
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // making references to the xml
        EditText emailAddress = findViewById(R.id.address);
        Button resetButton = findViewById(R.id.reset);
        Toolbar toolbar10 = findViewById(R.id.toolbar10);
        setSupportActionBar(toolbar10);
        //Enabling the navigation
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //setting an Onclick listener
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailAddress.getText().toString();
                if (email.isEmpty()) {
                    emailAddress.setError("Email is required");
                }
                else {
                     if (!haveNetworkConnection())
                        Toast.makeText(Forgot_password.this, " No internet Connected", Toast.LENGTH_SHORT).show();
                    //else
                    //{
                    //    new Resetclass.execute();
                   // }


                }
                @SuppressLint("StaticFieldLeak")
                @SuppressWarnings("deprecation")
                class Resetclass extends AsyncTask<String, String, String> {
                    String responsefromphp;
                    //create progress dialog class
                    ProgressDialog pdialog;

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        //showing the dialog
                        pdialog = new ProgressDialog(Forgot_password.this);
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
                            HttpPost httppost = new HttpPost(Reset_url);
                            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>(3);
                            nameValuePairs.add(new BasicNameValuePair("Email address",  email));
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
                            Toast.makeText(Forgot_password.this, "Unable to Access reset link.", Toast.LENGTH_SHORT).show();
                        } else if (responsefromphp.equals("1")) {
                            Toast.makeText(Forgot_password.this, "Password Reset Successfully.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Forgot_password.this, "Technical Failure. Contact Admin.", Toast.LENGTH_SHORT).show();
                        }
                    }
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
        });
    }
}

