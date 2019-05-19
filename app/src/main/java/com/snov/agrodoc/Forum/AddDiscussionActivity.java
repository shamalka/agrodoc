package com.snov.agrodoc.Forum;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.snov.agrodoc.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

public class AddDiscussionActivity extends AppCompatActivity {

    private Button SelectImageButton, UploadImageButton;
    private ImageView SelectedImage;
    private Uri filePath;

    FirebaseStorage storage;
    StorageReference storageReference;

    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_discussion);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        SelectImageButton = (Button)findViewById(R.id.select_image_btn);
        UploadImageButton = (Button)findViewById(R.id.upload_image_btn);
        SelectedImage = (ImageView)findViewById(R.id.discussion_image);

        SelectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });

        UploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImageToFirestore();
            }
        });

    }



    private void SelectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image"),PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null & data.getData() != null){
            filePath = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                //SelectedImage.setImageBitmap(bitmap);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    private void UploadImageToFirestore(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading..");
        progressDialog.show();

        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
        ref.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //Log.e("Tuts+", "uri: " + uri.toString());
                                //Toast.makeText(AddDiscussionActivity.this, uri.toString(), Toast.LENGTH_LONG).show();
                                Picasso.get().load(uri.toString()).into(SelectedImage);
                                //Handle whatever you're going to do with the URL here
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddDiscussionActivity.this, "Failed " + e.getMessage() , Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int)progress + "%");
                    }
                });
    }
}
