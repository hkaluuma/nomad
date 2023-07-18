package com.example.nomad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

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

        //lets have the buttons move / shake before being tapped.
        YoYo.with(Techniques.Shake).duration(10000).repeat(10).playOn(total_inventory);
        YoYo.with(Techniques.Shake).duration(10000).repeat(10).playOn(job_card);
        YoYo.with(Techniques.Shake).duration(10000).repeat(10).playOn(good_condition);
        YoYo.with(Techniques.Shake).duration(10000).repeat(10).playOn(need_repair);
        YoYo.with(Techniques.Shake).duration(10000).repeat(10).playOn(need_replacement);
        YoYo.with(Techniques.Shake).duration(10000).repeat(10).playOn(maintain_requests);

        // set an onclick listener for  the Image button
                //set onclick-listeners for the icons or buttons to link to other pages
                total_inventory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        YoYo.with(Techniques.Shake).duration(100).repeat(0).playOn(total_inventory);
                        //YoYo.with(Techniques.Tada).duration(1000).repeat(0).playOn(img);
                        Intent intent;
                        intent = new Intent(MainActivity.this, TotalInventoryActivity.class);
                        startActivity(intent);


                    }
                });

                job_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        YoYo.with(Techniques.Shake).duration(1000).repeat(0).playOn(job_card);
                        Intent intent = new Intent(MainActivity.this, JobCardsActivity.class);
                        startActivity(intent);

                    }
                });

                good_condition.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        YoYo.with(Techniques.Shake).duration(1000).repeat(0).playOn(good_condition);
                        Intent intent = new Intent(MainActivity.this, InGoodConditionActivity.class);
                        startActivity(intent);

                    }
                });


                need_repair.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        YoYo.with(Techniques.Shake).duration(1000).repeat(0).playOn(need_repair);
                        Intent intent = new Intent(MainActivity.this, NeedRepairActivity.class);
                        startActivity(intent);
                    }
                });
                need_replacement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        YoYo.with(Techniques.Shake).duration(1000).repeat(0).playOn(need_replacement);
                        Intent intent = new Intent(MainActivity.this, NeedReplacementActivity.class);
                        startActivity(intent);
                    }
                });
                maintain_requests.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        YoYo.with(Techniques.Shake).duration(1000).repeat(0).playOn(maintain_requests);
                        Intent intent = new Intent(MainActivity.this, MaintenanceRequestsActivity.class);
                        startActivity(intent);
                    }
                });

    }
}



