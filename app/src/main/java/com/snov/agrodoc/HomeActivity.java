package com.snov.agrodoc;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.snov.agrodoc.DiseaseDetection.DetectorHomeActivity;
import com.snov.agrodoc.DiseaseDetection.DiseaseDetailsActivity;
import com.snov.agrodoc.DiseaseDetection.UploadImageActivity;
import com.snov.agrodoc.Forum.ForumHomeActivity;
import com.snov.agrodoc.Market.MarketHomeActivity;
import com.snov.agrodoc.classification.ClassifierActivity;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    Button GotoDiscussion, GotoMarket, ImageUpload, ProfileButton;
    private static final int CAMERA_PERMISSION_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        setContentView(R.layout.activity_home);


        requestCameraPermission();

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
            Intent intent = new Intent(HomeActivity.this, ClassifierActivity.class);
            startActivity(intent);
        });

        ImageUpload.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, DiseaseDetailsActivity.class);
            startActivity(intent);
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void requestCameraPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == CAMERA_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }
}
