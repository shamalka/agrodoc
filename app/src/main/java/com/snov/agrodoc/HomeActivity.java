package com.snov.agrodoc;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.snov.agrodoc.DiseaseDetection.DetectorHomeActivity;
import com.snov.agrodoc.DiseaseDetection.UploadImageActivity;
import com.snov.agrodoc.Forum.ForumHomeActivity;
import com.snov.agrodoc.Market.MarketHomeActivity;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    Button GotoDiscussion, GotoMarket, ImageUpload, ProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        setContentView(R.layout.activity_home);



        ProfileButton = (Button)findViewById(R.id.profile_btn);
        ProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        GotoDiscussion = (Button)findViewById(R.id.discussion_btn_home);
        GotoMarket = (Button)findViewById(R.id.marketplace_btn_home);
        ImageUpload = (Button)findViewById(R.id.image_btn_home);

        GotoDiscussion.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ForumHomeActivity.class);
            startActivity(intent);
        });

       // Toast.makeText(getApplicationContext(), "Happy New Year", Toast.LENGTH_SHORT).show();

        GotoMarket.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MarketHomeActivity.class);
            startActivity(intent);
        });

        ImageUpload.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, DetectorHomeActivity.class);
            startActivity(intent);
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
