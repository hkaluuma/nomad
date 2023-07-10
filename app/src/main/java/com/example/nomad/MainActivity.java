package com.example.nomad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //making references to the widgets

        ImageView total_inventory = findViewById(R.id.total_inventory);
        ImageView job_card = findViewById(R.id.job_card);
        ImageView good_condition = findViewById(R.id.good_condition);
        ImageView need_repair = findViewById(R.id.need_repair);
        ImageView need_replacement = findViewById(R.id.need_replacement);
        ImageView maintain_requests = findViewById(R.id.maintain_requests);

        // set an onclick listener for  the Image button
                //set onclick-listeners for the icons or buttons to link to other pages
                total_inventory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent;
                        intent = new Intent(MainActivity.this, TotalInventoryActivity.class);
                        startActivity(intent);


                    }
                });

                job_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, JobCardsActivity.class);
                        startActivity(intent);

                    }
                });

                good_condition.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, InGoodConditionActivity.class);
                        startActivity(intent);

                    }
                });


                need_repair.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, NeedRepairActivity.class);
                        startActivity(intent);
                    }
                });
                need_replacement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, NeedReplacementActivity.class);
                        startActivity(intent);
                    }
                });
                maintain_requests.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, MaintenanceRequestsActivity.class);
                        startActivity(intent);

                    }
                });

    }
}



