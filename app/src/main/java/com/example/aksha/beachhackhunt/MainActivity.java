package com.example.aksha.beachhackhunt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button buttonNewCampaign;
    Button buttonJoinCampaign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonNewCampaign = findViewById(R.id.createNewButton);
        buttonJoinCampaign = findViewById(R.id.joinCampButton);

        buttonNewCampaign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewCampaignPage.class);
                startActivity(intent);
            }
        });

        buttonJoinCampaign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, huntMapsActivity.class);
                startActivity(intent);
            }
        });



    }
}
