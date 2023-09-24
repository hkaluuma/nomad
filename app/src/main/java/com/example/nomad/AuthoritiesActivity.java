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
//import android.widget.ImageButton;
//import android.widget.ImageView;
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
public class AuthoritiesActivity extends AppCompatActivity {
    //Instantiating the config class
    Config config = new Config();

    String authorities_url = "https://" + config.server_ip + "/nomad/authority.php";
    String authority;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorities);
        Toolbar toolbar1 = findViewById(R.id.toolbar1);

        // making references to the xml widgets
        EditText ed_authority = findViewById(R.id.authority);
        Button btn4 = findViewById(R.id.create_btn4);

        //setting the On-click listener
        btn4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                authority = ed_authority.getText().toString();
                if (authority.isEmpty()) {
                    ed_authority.setError("the authority is required");
                }

                if (!haveNetworkConnection())
                    Toast.makeText(AuthoritiesActivity.this, "  No internet Connected", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(AuthoritiesActivity.this, "No internet Connection", Toast.LENGTH_SHORT).show();
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

            /** @noinspection deprecation*/
            @SuppressLint("StaticFieldLeak")
            class Authorities extends AsyncTask<String, String, String> {
                String responsefromphp;
                //show dialog while accessing the authority data

                //creating progress dialog class
                ProgressDialog pdialog;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    pdialog = new ProgressDialog(com.example.nomad.AuthoritiesActivity.this);
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
                        HttpPost httppost = new HttpPost(authorities_url);
                        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>(3);
                        nameValuePairs.add(new BasicNameValuePair("Authority", authority));
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
                        Toast.makeText(AuthoritiesActivity.this, "Authority page  not loading.", Toast.LENGTH_SHORT).show();
                    } else if (responsefromphp.equals("1")) {
                        Toast.makeText(AuthoritiesActivity.this, "Authority Successfully updated.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AuthoritiesActivity.this, "Technical Failure. Contact Admin.", Toast.LENGTH_SHORT).show();
                        // is the intent called
                        Intent intent = new Intent(getApplicationContext(), AuthoritiesActivity.class);
                        startActivity(intent);
                    }

                }

            }
        });
    }
}

