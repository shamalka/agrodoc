package com.snov.agrodoc.Forum;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.snov.agrodoc.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddDiscussionActivity extends AppCompatActivity {

    private Button SelectImageButton, UploadImageButton, AddNewButton;
    private ImageView SelectedImage;
    private Uri filePath;

    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    EditText DiscussionTitle, DiscussionBody;

    RadioGroup radioGroup;
    RadioButton radioButton;

    String DiscussionType, format;

    FirebaseStorage storage;
    StorageReference storageReference;

    ProgressDialog progressDialog;

    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_discussion);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();



        SelectImageButton = (Button)findViewById(R.id.select_image_btn);

        AddNewButton = (Button)findViewById(R.id.add_new_button);
        SelectedImage = (ImageView)findViewById(R.id.discussion_image);

        radioGroup = (RadioGroup)findViewById(R.id.add_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                int selectedId=radioGroup.getCheckedRadioButtonId();
                radioButton=(RadioButton)findViewById(selectedId);
                DiscussionType=radioButton.getText().toString();
                Toast.makeText(getApplicationContext(),radioButton.getText(),Toast.LENGTH_SHORT).show();
            }

        });

        //Get timestamp
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy:hh-mm-ss");
        format = simpleDateFormat.format(new Date());
        Toast.makeText(this,"Time " + format,Toast.LENGTH_SHORT).show();

        DiscussionTitle = (EditText)findViewById(R.id.question_text);
        DiscussionBody = (EditText)findViewById(R.id.description_text);

        SelectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });


        AddNewButton.setOnClickListener(new View.OnClickListener() {
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
                SelectedImage.setImageBitmap(bitmap);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    private void UploadImageToFirestore(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading..");
        progressDialog.show();

        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
        ref.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //Log.e("Tuts+", "uri: " + uri.toString());
                                //Toast.makeText(AddDiscussionActivity.this, uri.toString(), Toast.LENGTH_LONG).show();
                                Picasso.get().load(uri.toString()).into(SelectedImage);
                                //Handle whatever you're going to do with the URL here
                                SendData(uri.toString());
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

    private void SendData(String ImageUrl){
//        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Adding discussion");
//        progressDialog.show();

        FirebaseUser user = mAuth.getCurrentUser();

        String body = DiscussionBody.getText().toString();
        String title = DiscussionTitle.getText().toString();

        Map<String, String> DiscussionMap = new HashMap<>();
        DiscussionMap.put("user_name", user.getDisplayName());
        DiscussionMap.put("user_id", user.getUid());
        DiscussionMap.put("type", DiscussionType);
        DiscussionMap.put("title", title);
        DiscussionMap.put("body", body);
        DiscussionMap.put("image_url", ImageUrl);
        DiscussionMap.put("timestamp", format);



        mFirestore.collection("discussion").add(DiscussionMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getApplicationContext(), "Discussion Added", Toast.LENGTH_LONG).show();
                progressDialog.setTitle("Discussion Added");
                progressDialog.dismiss();
                // getActivity().getSupportFragmentManager().beginTransaction().remove(PopUpDialog.this).commit();


            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed, Try again.!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
