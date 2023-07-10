package com.example.nomad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class AuthoritiesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorities);

        //making references to the widgets

        ImageView imageView1 = findViewById(R.id.total_inventory);
        ImageView imageView2 = findViewById(R.id.job_card);
        ImageView imageView3 = findViewById(R.id.good_condition);
        ImageView imageView4 = findViewById(R.id.image_view23);
        ImageView imageView5 = findViewById(R.id.image_view);
        ImageView imageView6 = findViewById(R.id.image_view22);
        ImageButton imgbtn = findViewById(R.id.imgbtn1);

        // set an onclick listener for  the Image button

        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set onclick-listeners for the icons or buttons to link to other pages
                imageView1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent;
                        intent = new Intent(MainActivity.this, TotalInventoryActivity.class);
                        startActivity(intent);


                    }
                });

                imageView2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, InGoodConditionActivity.class);
                        startActivity(intent);

                    }
                });

                imageView3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, NeedRepairActivity.class);
                        startActivity(intent);

                    }
                });


                imageView4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, NeedReplacementActivity.class);
                        startActivity(intent);
                    }
                });
                imageView5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, MaintenanceRequestsActivity.class);
                        startActivity(intent);
                    }
                });
                imageView6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, NeedRepairActivity.class);
                        startActivity(intent);

                    }
                });
            }


        });


    }
}

