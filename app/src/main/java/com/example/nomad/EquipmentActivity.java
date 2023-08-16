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

/** @noinspection deprecation*/
public class EquipmentActivity extends AppCompatActivity {
    //Instantiating the config class
    Config config = new Config();
    String equipment_url= "https://"+config.server_ip+"/nomad/equipment.php";
    // Referencing  of the widgets in equipment.xml
    // global variables
    String Name, selectedType, selectedCode, hc2, hc3, hc4, GH, RRH, NRH, amount;

// Referencing  of the widgets in equipment.xml

    EditText edtName = findViewById(R.id.Name);
    Spinner edttype = findViewById(R.id.type1);
    Spinner edtcode = findViewById(R.id.code1);
    EditText edthc2 = findViewById(R.id.recommendation1);
    EditText edthc3 = findViewById(R.id.recommendation2);
    EditText edthc4 = findViewById(R.id.recommendation3);
    EditText edtGH = findViewById(R.id.recommendation4);
    EditText edtRRH = findViewById(R.id.recommendation5);
    EditText edtNRH = findViewById(R.id.record);
    EditText cost = findViewById(R.id.cost);
    Button btn2 = findViewById(R.id.create_btn2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment);

        // data source from where the spinner will pick
        String[] EquipmentType = {"8888", "needle", "injection"};
        String[] classificationCode = {"MIS", "HSS", "ME"};

        //configuring the array adapter for the spinner-acts the glue

        ArrayAdapter<String> equipmentAdapter = new ArrayAdapter<>(EquipmentActivity.this, R.layout.spinner_item, R.id.TextView_spinner, EquipmentType);
        ArrayAdapter<String> codeAdapter = new ArrayAdapter<>(EquipmentActivity.this, R.layout.spinner_item, R.id.TextView_spinner, classificationCode);

        // assign Array adapter to the spinner

        edttype.setAdapter(equipmentAdapter);
        edtcode.setAdapter(codeAdapter);
// the connection method is having an issue

        // selecting an Item from the spinner
        edttype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long id) {
                selectedType=EquipmentType [i];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedType="not specified";
            }
        });
        edtcode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedCode=classificationCode[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedCode="not specified";
            }
        });

        // setting the onclick method for the  the create button
        btn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Name = edtName.getText().toString();
                hc2 = edthc2.getText().toString();
                hc3 = edthc3.getText().toString();
                hc4 = edthc4.getText().toString();
                GH = edtGH.getText().toString();
                RRH = edtRRH.getText().toString();
                amount = cost.getText().toString();
                NRH = edtNRH.getText().toString();

                if (Name.isEmpty()) {
                    edtName.setError("Name is Required");
                }
                if (amount.isEmpty()) {
                    edtName.setError("Name is Required");
                }
                else if (selectedType.equals("not specified"))
                {
                    Toast.makeText(EquipmentActivity.this, "Select Type", Toast.LENGTH_SHORT).show();
                }
                else if (selectedCode.equals("not specified"))
                {
                    Toast.makeText(EquipmentActivity.this, "Select Code", Toast.LENGTH_SHORT).show();
                    // Boolean haveNetworkConnection;

                    if (haveNetworkConnection())
                        Toast.makeText(EquipmentActivity.this, " Connected", Toast.LENGTH_SHORT).show();
                    else {
                        Toast.makeText(EquipmentActivity.this, "No internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            //

            //method to check if user connected to the internet
            private boolean haveNetworkConnection () {

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
            @SuppressLint("StaticFieldLeak")
            @SuppressWarnings("deprecation")
            class Equipmentclass extends AsyncTask<String, String, String> {
                String responcefromphp;
                //create progress dialog class
                ProgressDialog pdialog;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    //show dialog while registering Business
                    pdialog = new ProgressDialog(EquipmentActivity.this);
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
                        HttpPost httppost = new HttpPost(equipment_url);
                        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>(3);
                        nameValuePairs.add(new BasicNameValuePair("Name", Name));
                        //   nameValuePairs.add(new BasicNameValuePair(" selecttype",type));
                        //   nameValuePairs.add(new BasicNameValuePair("selectcode", ));
                        nameValuePairs.add(new BasicNameValuePair("health centre2", hc2));
                        nameValuePairs.add(new BasicNameValuePair("health centre3", hc3));
                        nameValuePairs.add(new BasicNameValuePair("health centre4", hc4));
                        nameValuePairs.add(new BasicNameValuePair("general hospital", GH));
                        nameValuePairs.add(new BasicNameValuePair("RegionalReferral", RRH));
                        nameValuePairs.add(new BasicNameValuePair("NationalReferral", NRH));
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
                        responcefromphp = sb.toString();
                        inputStream.close();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Try Again", Toast.LENGTH_LONG).show();
                    }
                    return responcefromphp;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    pdialog.dismiss();
                    if (responcefromphp.equals("0")) {
                        Toast.makeText(EquipmentActivity.this, " Failed!Equipment not Registered.", Toast.LENGTH_SHORT).show();
                    } else if (responcefromphp.equals("1")) {
                        Toast.makeText(EquipmentActivity.this, "Equipment Successfully created.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EquipmentActivity.this, "Technical Failure. Contact Admin.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

}
