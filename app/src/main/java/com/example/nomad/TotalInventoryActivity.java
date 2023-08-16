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

/** @noinspection deprecation*/
public class TotalInventoryActivity extends AppCompatActivity {
    //Instantiating the config class
    Config config = new Config();
    String inventory_url = "https://" + config.server_ip + "/nomad/inventory.php";



    //Global variables
    String Type1, engravedNo, serial1, model1, quantity, condition1,  inventoryName;
    String remark, selectedSupplier, selectedFacility, selectedDepartment,selectedCondition;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_inventory);
        // making reference to the widgets in the xml

        EditText ed_Type = findViewById(R.id.type);
        EditText ed_engraved = findViewById(R.id.engraved);
        EditText ed_serial = findViewById(R.id.serial);
        EditText ed_model = findViewById(R.id.model);
        EditText ed_quantity = findViewById(R.id.quantity);
        Spinner ed_condition = findViewById(R.id.condition);
        EditText ed_remark = findViewById(R.id.remark);
        Spinner ed_supplier = findViewById(R.id.supplier);
        Spinner ed_department = findViewById(R.id.department1);
        Spinner ed_facility = findViewById(R.id.facility);
        Button btn7 = findViewById(R.id.create_btn7);
        EditText ed_inventory = findViewById(R.id.ed_Name);


        // data source from where the spinner will pick
        String[] Supplier = {"8888", "needle", "injection"};
        String[] department = {"MIS", "HSS", "ME"};
        String[] facility = {"Nakuru", "Mulago", "dME"};
        String[] condition = {"Oxygen Concentrator", "Needle", "Bed side"};

        //configuring the array adapter for the spinner-acts the glue

        ArrayAdapter<String> supplyAdapter = new ArrayAdapter<>(TotalInventoryActivity.this, R.layout.spinner_item, R.id.TextView_spinner, Supplier);
        ArrayAdapter<String> departAdapter = new ArrayAdapter<>(TotalInventoryActivity.this, R.layout.spinner_item, R.id.TextView_spinner, department);
        ArrayAdapter<String> facilityAdapter = new ArrayAdapter<>(TotalInventoryActivity.this, R.layout.spinner_item, R.id.TextView_spinner, facility);
        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<>(TotalInventoryActivity.this, R.layout.spinner_item, R.id.TextView_spinner, condition);

        // assigning Array adapter to the spinner
        ed_supplier.setAdapter(supplyAdapter);
        ed_department.setAdapter(departAdapter);
        ed_facility.setAdapter(facilityAdapter);
        ed_condition.setAdapter( conditionAdapter);


        // selecting an Item from the spinner
        ed_supplier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int k, long id) {
                selectedSupplier= Supplier [k];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedSupplier="not specified";

                Toast.makeText(TotalInventoryActivity.this, "Select Supplier", Toast.LENGTH_SHORT).show();
            }

        });
        ed_department.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posn, long id) {
                selectedDepartment=department [posn];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedDepartment ="not specified";
                Toast.makeText(TotalInventoryActivity.this, "Select Department", Toast.LENGTH_SHORT).show();
            }
        });
        ed_facility.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posn, long id) {
                selectedFacility=facility [posn];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedFacility ="not specified";
                Toast.makeText(TotalInventoryActivity.this, "Select Facility", Toast.LENGTH_SHORT).show();
            }
        });
        ed_condition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int postn, long id) {
                selectedCondition=condition [postn];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedCondition ="not specified";
                Toast.makeText(TotalInventoryActivity.this, "Select Inventory", Toast.LENGTH_SHORT).show();
            }
        });
        // set an onclick button listener
        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Type1 = ed_Type.getText().toString();
                engravedNo = ed_engraved.getText().toString();
                serial1 = ed_serial.getText().toString();
                model1 = ed_model.getText().toString();
                quantity = ed_quantity.getText().toString();
                inventoryName =ed_inventory.getText().toString();
                remark = ed_remark.getText().toString();

                if (Type1.isEmpty()) {
                    ed_Type.setError("Type is Required");
                } else if (engravedNo.isEmpty()) {
                    ed_engraved.setError("Engraved Number is Required");
                } else if (quantity.isEmpty()) {
                    ed_quantity.setError("Quantity is Required");
                } else if (  inventoryName.isEmpty()) {
                    ed_inventory .setError("Condition is Required");
                }
                else if (!haveNetworkConnection())
                    Toast.makeText(TotalInventoryActivity.this, " No internet Connected", Toast.LENGTH_SHORT).show();
                else {
                    new Inventoryclass().execute();

                }

            }
            //inner class
            @SuppressLint("StaticFieldLeak")
            @SuppressWarnings("deprecation")
            class Inventoryclass extends AsyncTask<String, String, String> {
                String responsefromphp;
                //create progress dialog class
                ProgressDialog pdialog;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    //show dialog while accessing the inventory
                    pdialog = new ProgressDialog(TotalInventoryActivity.this);
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
                        HttpPost httppost = new HttpPost(inventory_url);
                        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>(3);
                        nameValuePairs.add(new BasicNameValuePair("type",Type1));
                        nameValuePairs.add(new BasicNameValuePair("Engraved Number", engravedNo));
                        nameValuePairs.add(new BasicNameValuePair("Serial Number",  serial1));
                        nameValuePairs.add(new BasicNameValuePair("Model",model1));
                        nameValuePairs.add(new BasicNameValuePair("Quantity",quantity));
                        nameValuePairs.add(new BasicNameValuePair("Condition",condition1));
                        nameValuePairs.add(new BasicNameValuePair("Remarks",remark));
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
                        Toast.makeText(TotalInventoryActivity.this, "Inventory not updated.", Toast.LENGTH_SHORT).show();
                    } else if (responsefromphp.equals("1")) {
                        Toast.makeText(TotalInventoryActivity.this, "Inventory updated Successful.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TotalInventoryActivity.this, "Technical Failure. Contact Admin.", Toast.LENGTH_SHORT).show();
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





