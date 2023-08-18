package com.example.nomad;
import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
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

public class LoginActivity extends AppCompatActivity {
    String username, pass;
    //instantiate config class
    Config config = new Config();

    String login_url= "http://"+config.server_ip+"/nomad/login.php";
    //shared preferences variables
    SharedPreferences sharedpreferences;
    public static final String MYPREFERENCES_LOGIN = "MyPreferences_002";
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //new codes
        //ImageView imgicon = findViewById(R.id.nomad_icon);
        final EditText editTextusername = findViewById(R.id.ed_username);
        final EditText editTextpassword = findViewById(R.id.ed_pwd);
        final Button btn1 = findViewById(R.id.login_btn);
        final Button btn2 = findViewById(R.id.forgot_password);
        final Button btn12 = findViewById(R.id.register);
        YoYo.with(Techniques.Shake).duration(10000).repeat(10).playOn(btn1);
        YoYo.with(Techniques.Shake).duration(10000).repeat(10).playOn(btn2);
        YoYo.with(Techniques.Shake).duration(10000).repeat(10).playOn(btn12);
        //YoYo.with(Techniques.DropOut).duration(100).repeat(10).playOn(imgicon);

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.FlipOutX).duration(100).repeat(0).playOn(btn2);
                Intent forgotintent = new Intent(LoginActivity.this, Forgot_password.class);
                startActivity(forgotintent);
            }
        });
        btn12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.FlipOutX).duration(100).repeat(0).playOn(btn12);
                Intent registerintent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(registerintent);
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.Flash).duration(100).repeat(0).playOn(btn1);
               //YoYo.with(Techniques.BounceIn).duration(100).repeat(10).playOn(imgicon);
                pass = editTextpassword.getText().toString();
                username = editTextusername.getText().toString();
                if (username.isEmpty()) {
                    editTextusername.setError("User Name is Required");
                }if (pass.isEmpty()) {
                    editTextpassword.setError("Password is required");
                } else {
                    if (haveNetworkConnection()) {
                        // connected
                       // CreateLogin createLogin = new CreateLogin();
                        //createLogin.execute();
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    } else {
                        // not connected
                        Toast.makeText(LoginActivity.this, "No internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    //method to check internet availability(WiFi and MobileData)
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
    /** @noinspection deprecation*/ //async task class to login
    class CreateLogin extends AsyncTask<String, String, String> {
        String responcefromphp;
        //create progress dialog class
        ProgressDialog pdialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show dialog while registering Business
            pdialog = new ProgressDialog(LoginActivity.this);
            pdialog.setMessage("Please wait...");
            pdialog.setIndeterminate(false);//holds till process is done
            pdialog.setCancelable(false);// set screen in freez
            pdialog.show();
        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(String... strings) {
            //upload data to the database
            try {
                DefaultHttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://172.31.2.26/nomad/login.php");
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("biz_username", username));
                nameValuePairs.add(new BasicNameValuePair("biz_pass", pass));
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
                Log.i(TAG, "doInBackground *********: "+ responcefromphp);
            }
            return responcefromphp;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // dismiss dialog and perform other tasks
            pdialog.dismiss();
            if(responcefromphp.equals("0")){
                Toast.makeText(LoginActivity.this, "Login Failed customer not Registered.", Toast.LENGTH_SHORT).show();
            } else if(responcefromphp.equals(null)) {
                Toast.makeText(LoginActivity.this, "Login Failed null returned.", Toast.LENGTH_SHORT).show();
            } else {
                String[] usercredentials = responcefromphp.split("#");
                Log.e("responcefromphp", responcefromphp);
                SharedPreferences mySharedPreferences = getSharedPreferences(MYPREFERENCES_LOGIN, Activity.MODE_PRIVATE);
                editor = mySharedPreferences.edit();
                editor.putString("username", usercredentials[0]);
                editor.putString("fullname", usercredentials[1]);
                editor.putString("phonenumber", usercredentials[2]);
                editor.putString("location", usercredentials[3]);
                editor.putString("email", usercredentials[4]);
                editor.putString("id", usercredentials[5]);
                editor.commit();
                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        }
    }//end of asnck task
}
