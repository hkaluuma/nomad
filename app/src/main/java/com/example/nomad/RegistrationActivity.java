package com.example.nomad;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

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

public class RegistrationActivity extends AppCompatActivity {

    //instantiate config class
    Config config = new Config();
    String register_url= "http://"+config.server_ip+"/nomad/register.php";
    //global variables
    String name, email, username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        //new codes
        //make refferences to the widgets
        Button btn_reg = findViewById(R.id.btn_register);
        EditText  edx_name = findViewById(R.id.edt_name);
        EditText  edx_usrname = findViewById(R.id.edt_username);
        EditText  edx_pass = findViewById(R.id.edt_password);
        EditText  edx_pass2 = findViewById(R.id.edt_password2);
        EditText  edx_email = findViewById(R.id.edt_email);

        //set onclick listener for button
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = edx_pass.getText().toString();
                name = edx_name.getText().toString();
                username = edx_usrname.getText().toString();
                email = edx_email.getText().toString();

                if (username.isEmpty()) {
                    edx_usrname.setError("User Name is Required");
                }
                if (password.isEmpty()) {
                    edx_pass.setError("Password is required");
                }
                if (email.isEmpty()) {
                    edx_usrname.setError("User Name is Required");
                }
                if (name.isEmpty()) {
                    edx_name.setError("Password is required");
                } else {
                    if (haveNetworkConnection()) {
                        // connected
                        Registerclass regobj = new Registerclass();
                        regobj.execute();
                    } else {
                        // not connected
                        Toast.makeText(RegistrationActivity.this, "No internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }

    //inner class
    @SuppressWarnings("deprecation")
    class Registerclass extends AsyncTask<String, String, String>{
        String responsefromphp;
        //create progress dialog class
        ProgressDialog pdialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show dialog while registering Business
            pdialog = new ProgressDialog(RegistrationActivity.this);
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
                HttpPost httppost = new HttpPost("http://172.31.2.75/nomad/register.php");
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                nameValuePairs.add(new BasicNameValuePair("name", name));
                nameValuePairs.add(new BasicNameValuePair("username", username));
                nameValuePairs.add(new BasicNameValuePair("pass", password));
                nameValuePairs.add(new BasicNameValuePair("email", email));
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
                Log.i(TAG, "doInBackground: *****"+ responsefromphp);
            }
            return responsefromphp;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pdialog.dismiss();
            if(responsefromphp.equals("0")){
                Toast.makeText(RegistrationActivity.this, "Login Failed customer not Registered.", Toast.LENGTH_SHORT).show();
            } else if(responsefromphp.equals("1")){
                Toast.makeText(RegistrationActivity.this, "Registration Successful.", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(RegistrationActivity.this, "Technical Failure. Contact Admin.", Toast.LENGTH_SHORT).show();
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

}