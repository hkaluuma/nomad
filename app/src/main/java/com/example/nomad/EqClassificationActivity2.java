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
public class EqClassificationActivity2 extends AppCompatActivity {
    //Instantiating the config class

    public Config config = new Config();
    String classify_url= "https://"+config.server_ip+"/nomad/classificationcodes.php";
    String code, classification;

    // making references to the widgets in xml
    EditText ed_classify = findViewById(R.id.classify);
    EditText ed_code = findViewById(R.id.code1);
    Button btn1 = findViewById(R.id.create_btn1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eq_classification2);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classification = ed_classify.getText().toString();
                code = ed_code.getText().toString();
                if (classification.isEmpty()) {
                    ed_classify.setError("Classification is Required");
                }
                else if (code.isEmpty()) {
                    ed_code.setError("Code is Required");

                }
                else if (!haveNetworkConnection())
                    Toast.makeText(EqClassificationActivity2.this, " No internet Connected", Toast.LENGTH_SHORT).show();
                else
                {
                    new Classifyclass().execute();

                }

            }

            @SuppressLint("StaticFieldLeak")
            @SuppressWarnings("deprecation")
            class Classifyclass extends AsyncTask<String, String, String> {
                String responsefromphp;
                //create progress dialog class
                ProgressDialog pdialog;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    //show dialog while registering Business
                    pdialog = new ProgressDialog(EqClassificationActivity2.this);
                    pdialog.setMessage("Please wait...");
                    pdialog.setIndeterminate(false);//holds till process is done
                    pdialog.setCancelable(false);// set screen in freez and the user is unable to cancel the process
                    pdialog.show();
                }


                @Override
                protected String doInBackground(String... strings) {
                    //uploading data to the database
                    try {
                        DefaultHttpClient httpclient = new DefaultHttpClient();
                        HttpPost httppost = new HttpPost(classify_url);
                        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>(3);
                        nameValuePairs.add(new BasicNameValuePair("classification",classification));
                        nameValuePairs.add(new BasicNameValuePair("code", code));
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
                        Toast.makeText(EqClassificationActivity2.this, "Equipment Classification not updated.", Toast.LENGTH_SHORT).show();
                    } else if (responsefromphp.equals("1")) {
                        Toast.makeText(EqClassificationActivity2.this, "Equipment Classification updated Successful.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EqClassificationActivity2.this, "Technical Failure. Contact Admin.", Toast.LENGTH_SHORT).show();
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


        });

    }
}
