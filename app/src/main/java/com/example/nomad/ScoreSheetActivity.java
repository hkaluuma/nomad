package com.example.nomad;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
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

public class ScoreSheetActivity extends AppCompatActivity {
    //Instantiating the config class
    Config config = new Config();
    String scoresheet_url = "https://" + config.server_ip + "/nomad/scoresheet.php";

    String workshop,staff_entry,budget_time,update_entry,jobcard_entry,spare_entry,prevent_entry,routine_entry,planned_entry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_sheet);

        // making references of the widgets to the xml

        EditText wrkshp = findViewById(R.id.workshop);
        EditText staff = findViewById(R.id.availability);
        EditText time = findViewById(R.id.budget);
        EditText update = findViewById(R.id.inventory_update);
        EditText spare = findViewById(R.id.spare);
        EditText preventive = findViewById(R.id.preventive);
        EditText routine = findViewById(R.id.maintain);
        EditText planned = findViewById(R.id.planned);
        Button btn8 = findViewById(R.id.create_btn8);

        // setting the onclick-listener

        btn8.setOnClickListener(new View.OnClickListener() {
            class Scoresheetclass {
                public void execute() {
                }
            }

            @Override
            public void onClick(View v) {
                workshop = wrkshp.getText().toString();
                staff_entry = staff.getText().toString();
                budget_time = time.getText().toString();
                update_entry = update.getText().toString();
                spare_entry = spare.getText().toString();
                prevent_entry = preventive.getText().toString();
                routine_entry = routine.getText().toString();
                planned_entry = planned.getText().toString();

                if (workshop.isEmpty()) {
                    wrkshp.setError("workshop is required");
                }
                if (staff_entry.isEmpty()) {
                    staff.setError(" staff is required");
                }
                if (routine_entry.isEmpty()) {
                    routine.setError(" routine entry is required");
                }
                if (planned_entry.isEmpty()) {
                    routine.setError("planned entry is required");
                }
                if (spare_entry.isEmpty()) {
                    spare.setError("planned entry is required");
                }
                else{
                    new Scoresheetclass().execute();
                }

            }

            @SuppressLint("StaticFieldLeak")
            @SuppressWarnings("deprecation")
            class ScoreSheet extends AsyncTask<String, String, String> {
                String responsefromphp;
                //show dialog while accessing score sheet
                //creating the progress dialog class
                ProgressDialog pdialog;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    pdialog = new ProgressDialog(ScoreSheetActivity.this);
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
                        HttpPost httppost = new HttpPost(scoresheet_url);
                        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>(3);
                        nameValuePairs.add(new BasicNameValuePair("workshop", workshop));
                        nameValuePairs.add(new BasicNameValuePair("staff available", staff_entry));
                        nameValuePairs.add(new BasicNameValuePair("budget_time", budget_time));
                        nameValuePairs.add(new BasicNameValuePair("update", update_entry));
                        nameValuePairs.add(new BasicNameValuePair("jobcard", jobcard_entry));
                        nameValuePairs.add(new BasicNameValuePair("spare_part", spare_entry));
                        nameValuePairs.add(new BasicNameValuePair("preventive_maintenance", prevent_entry));
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
                        Toast.makeText(ScoreSheetActivity.this, "Scoresheet not submitted.", Toast.LENGTH_SHORT).show();
                    } else if (responsefromphp.equals("1")) {
                        Toast.makeText(ScoreSheetActivity.this, "Scoresheet Successfully updated.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ScoreSheetActivity.this, "Technical Failure. Contact Admin.", Toast.LENGTH_SHORT).show();
                        // is the intent
                        Intent intent = new Intent(getApplicationContext(), ScoreSheetActivity.class);
                        startActivity(intent);
                    }

                }


            }
        });
    }
}


