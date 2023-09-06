package com.example.nomad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
public class JobCardsActivity extends AppCompatActivity {
    //Instantiating the config class
    Config config = new Config();
    String jobcard_url = "https://" + config.server_ip + "/nomad/jobcard.php";
    //Global variables

    String name, service, fault1, workdone, status, status2, comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_cards);

        // making references to the xml
        //Toolbar toolbar3 = findViewById(R.id.toolbar3);
        EditText record_name = findViewById(R.id.record_name);
        EditText service_date = findViewById(R.id.service_date);
        EditText fault = findViewById(R.id.fault);
        EditText work = findViewById(R.id.work);
        EditText original_status = findViewById(R.id.original_status);
        EditText final_status = findViewById(R.id.final_status);
        EditText fac_comment = findViewById(R.id.fac_comment);
        Button btn9 = findViewById(R.id.create_btn9);

        //setSupportActionBar(toolbar3);
        //Enabling the navigation
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // set the button onclick listener
        btn9.setOnClickListener(new View.OnClickListener() {
            class Cardclass {
                public void execute() {

                }
            }
            @Override
            public void onClick(View v) {
                name = record_name.getText().toString();
                service = service_date.getText().toString();
                fault1 = fault.getText().toString();
                workdone = work.getText().toString();
                comment = fac_comment.getText().toString();
                status = original_status.getText().toString();
                status2 = final_status.getText().toString();

                if (name.isEmpty()) {
                    record_name.setError("record name is required");
                    if (service.isEmpty()) {
                        service_date.setError("Service date is required");
                        if (status.isEmpty()) {
                            original_status.setError("original status is required");
                        }
                        // Establishing the network connection
                        // if (!haveNetworkConnection()) {
                        // not connected
                        Toast.makeText(JobCardsActivity.this, "No internet Connection", Toast.LENGTH_SHORT).show();
                    }


                    // if user is connected to the internet
                    Cardclass cardobj = new Cardclass();
                    cardobj.execute();
                    //inner class which is called the async task to create a dialog
                    @SuppressLint("StaticFieldLeak")
                    @SuppressWarnings("deprecation")
                    class Cardclass extends AsyncTask<String, String, String> {
                        String responsefromphp;
                        //creating progress dialog class
                        ProgressDialog pdialog;

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            //show dialog while registering Business
                            pdialog = new ProgressDialog(JobCardsActivity.this);
                            pdialog.setMessage("Please wait...");
                            pdialog.setIndeterminate(false);//holds till process is done or holds the progress dialog depending on the time it will take to fetch data from the database
                            pdialog.setCancelable(false);// set screen in freez i.e will allow force the user to wait for progress dialog to process without forcing to quit by locking the screen
                            pdialog.show();
                        }


                        @Override
                        protected String doInBackground(String... strings) {
                            //upload data to the database or the libraries to help retrieve and send from the app to the server
                            try {
                                DefaultHttpClient httpclient = new DefaultHttpClient();
                                HttpPost httppost = new HttpPost(jobcard_url);
                                ArrayList<NameValuePair> nameValuePairs = new ArrayList<>(3);
                                nameValuePairs.add(new BasicNameValuePair("record name", name));
                                nameValuePairs.add(new BasicNameValuePair("service date", service));
                                nameValuePairs.add(new BasicNameValuePair("Fault detected", fault1));
                                nameValuePairs.add(new BasicNameValuePair("workdone", workdone));
                                nameValuePairs.add(new BasicNameValuePair("Facility comment", comment));
                                nameValuePairs.add(new BasicNameValuePair("original status", status));
                                nameValuePairs.add(new BasicNameValuePair("final status", status2));
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
                                Toast.makeText(JobCardsActivity.this, "Login Failed customer not Registered.", Toast.LENGTH_SHORT).show();
                            } else if (responsefromphp.equals("1")) {
                                Toast.makeText(JobCardsActivity.this, "Information Submitted.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(JobCardsActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(JobCardsActivity.this, "Technical Failure. Contact Admin.", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                    {
                        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
                        for (NetworkInfo ni : netInfo) {
                            if (ni.getTypeName().equalsIgnoreCase("WIFI")) {
                                ni.isConnected();
                            }
                            if (ni.getTypeName().equalsIgnoreCase("MOBILE")) {
                                ni.isConnected();
                            }
                        }
                        //  return haveConnectedWifi || haveConnectedMobile;
                    }
                }

            }


        });
    }
}
