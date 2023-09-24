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
public class DistrictActivity extends AppCompatActivity {
    Config config = new Config();
    String district_url = "https://" + config.server_ip + "/nomad/district.php";
    String station,selectedRegion,selectedWorkshop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_district);

        // referencing  to the xml
        EditText Workshop = findViewById(R.id.workshop);
        Spinner Region = findViewById(R.id.region1);
        Spinner region_workshop = findViewById(R.id.region_workshop);
        Button btn4 = findViewById(R.id.create_btn5);

        // data source from where the spinner will pick
        String[] RegName ={"Eastern","Northern","Southern","Western"};
        String[] RegWorkshop={"Moroto","Gulu","Arua","Abim"};

        //configuring the array adapter for the spinner-acts the glue

        //ArrayAdapter<String> regionAdapter = new ArrayAdapter<>(DistrictActivity.this,R.layout.spinner_item,R.id.region1,RegName);
        //ArrayAdapter<String> workshopAdapter = new ArrayAdapter<>(DistrictActivity.this,R.layout.spinner_item,R.id.region_workshop,RegWorkshop);
        // assign Array adapter to the spinner
        //Region.setAdapter(regionAdapter);
        //region_workshop.setAdapter(workshopAdapter);

        // selecting an Item from the spinner
        Region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long id) {
                selectedRegion=RegName [i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedRegion="not specified";
            }
        });
        region_workshop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedWorkshop=RegWorkshop [position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedWorkshop="not specified";
            }
        });

        // setting on the Onclick Listener
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                station =Workshop.getText().toString();
                if(station.isEmpty()){Workshop.setError("workshop is required");

                } else if ( selectedRegion.equals("not specified")) {
                    Toast.makeText(DistrictActivity.this, "Select Region", Toast.LENGTH_SHORT).show();
                }
                else if ( selectedWorkshop.equals("not specified")) {
                    Toast.makeText(DistrictActivity.this, "Select Workshop", Toast.LENGTH_SHORT).show();
                }

                else if (!haveNetworkConnection())
                    Toast.makeText(DistrictActivity.this, "No internet Connection ", Toast.LENGTH_SHORT).show();
                else {
                    new Districtclass().execute();
                }



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

            //inner class
            @SuppressLint("StaticFieldLeak")
            @SuppressWarnings("deprecation")
            class Districtclass extends AsyncTask<String, String, String> {
                String responsefromphp;
                //create progress dialog class
                ProgressDialog pdialog;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    //show dialog while accessing the inventory
                    pdialog = new ProgressDialog(DistrictActivity.this);
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
                        HttpPost httppost = new HttpPost(district_url);
                        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>(3);
                        nameValuePairs.add(new BasicNameValuePair("workshop", station));
                        nameValuePairs.add(new BasicNameValuePair("region", selectedRegion));
                        nameValuePairs.add(new BasicNameValuePair("workshop_region", selectedWorkshop ));
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
                        Toast.makeText(DistrictActivity.this, "Unable to Access The District data.", Toast.LENGTH_SHORT).show();
                    } else if (responsefromphp.equals("1")) {
                        Toast.makeText(DistrictActivity.this, "District added Successful.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DistrictActivity.this, "Technical Failure. Contact Admin.", Toast.LENGTH_SHORT).show();
                    }
                }
            }


        });
    }
}

