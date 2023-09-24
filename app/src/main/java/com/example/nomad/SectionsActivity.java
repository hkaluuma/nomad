package com.example.nomad;

import static android.content.ContentValues.TAG;

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
public class SectionsActivity extends AppCompatActivity {
    //Instantiating the config class
    Config config = new Config();
    String section_url = "https://" + config.server_ip + "/nomad/section.php";
    //Global variables
    String  section;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sections);
        Toolbar toolbar5 = findViewById(R.id.toolbar5);
        EditText sectionEditText = findViewById(R.id.section);
        Button btn6 = findViewById(R.id.create_btn6);

        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                section =sectionEditText.getText().toString();

                if(section.isEmpty()){
                    sectionEditText.setError("the section field is required");
                }
                else if (section.equals("not specified")) {
                    Toast.makeText(SectionsActivity.this, "Select District", Toast.LENGTH_SHORT).show();

                }else {
                    if(!haveNetworkConnection()) {
                        Toast.makeText(SectionsActivity.this, "No internet Connection", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        new Sectionclass().execute();
                    }
                }

            }
            @SuppressWarnings("deprecation")
            @SuppressLint("StaticFieldLeak")
            class Sectionclass extends AsyncTask<String, String, String> {
                String responsefromphp;
                //create progress dialog class
                ProgressDialog pdialog;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    //show dialog while accessing the sections info
                    pdialog = new ProgressDialog(SectionsActivity.this);
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
                        HttpPost httppost = new HttpPost(section_url);
                        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>(3);
                        nameValuePairs.add(new BasicNameValuePair("section", section));

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
                        Toast.makeText(SectionsActivity.this, "Unable to Access  section.", Toast.LENGTH_SHORT).show();
                    } else if (responsefromphp.equals("1")) {
                        Toast.makeText(SectionsActivity.this, "Section updated Successful.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SectionsActivity.this, "Technical Failure. Contact Admin.", Toast.LENGTH_SHORT).show();
                    }
                }
            }


        });
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


}

