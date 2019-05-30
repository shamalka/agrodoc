package com.snov.agrodoc.DiseaseDetection;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.iceteck.silicompressorr.SiliCompressor;
import com.snov.agrodoc.Forum.DiscussionDetailsActivity;
import com.snov.agrodoc.R;
import com.snov.agrodoc.Utilities.Config;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import technolifestyle.com.imageslider.FlipperLayout;
import technolifestyle.com.imageslider.FlipperView;

public class DiseaseDetailsActivity extends AppCompatActivity {

    //FlipperLayout ImageSlider;
    ViewPager SliderViewPager;
    private static final String TAG = "FireLog";

    public List<String> DiseaseImageList = new ArrayList<String>();

    Button SolutionsButton, CompareButton, GoBackButton;
    PhotoView CompareImage;
    LinearLayout DescriptionLayout;
    CardView CompareImageCard;

    private ContentValues values;
    private int PICTURE_RESULT = 46;
    Uri imageUri;
    String ImageUrl, CompressedFilePath;

    private static final int STORAGE_PERMISSION_CODE = 123;
    private static final String DTAG = DetectorHomeActivity.class.getSimpleName();

    private static final String TAGG = "upload";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_details);

        requestStoragePermission();

        SliderViewPager = (ViewPager)findViewById(R.id.slider_view_pager);
        DescriptionLayout = (LinearLayout)findViewById(R.id.description_layout);

        CompareImageCard = (CardView) findViewById(R.id.compare_image_card);
        CompareImageCard.setVisibility(View.GONE);

        CompareImage = (PhotoView)findViewById(R.id.compare_image);
        CompareImage.setVisibility(View.GONE);

        GoBackButton = (Button)findViewById(R.id.goback_button);
        GoBackButton.setVisibility(View.GONE);

        CompareButton = (Button)findViewById(R.id.compare_button);
        CompareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DescriptionLayout.setVisibility(View.GONE);
                CompareImage.setVisibility(View.VISIBLE);
                CompareImageCard.setVisibility(View.VISIBLE);
                GoBackButton.setVisibility(View.VISIBLE);
                //Picasso.get().load("https://static2.cbrimages.com/wordpress/wp-content/uploads/2019/02/batman-last-knight-on-earth-cover-header.jpg").into(CompareImage);

                takePhoto();
            }
        });

        GoBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DescriptionLayout.setVisibility(View.VISIBLE);
                CompareImageCard.setVisibility(View.GONE);
                GoBackButton.setVisibility(View.GONE);
            }
        });


        FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();
        //Comment lsit
        mFireStore.collection("diseases").document("bGgbKluI9Iul1B98tPzS").collection("images").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(e != null){
                    Log.d(TAG, "Error: " + e.getMessage());
                }

                for(DocumentChange doc : documentSnapshots.getDocumentChanges()){
                    if(doc.getType() == DocumentChange.Type.ADDED){
                        String ImageUrl = doc.getDocument().getString("url");
                        String DocID = doc.getDocument().getId();
                        DiseaseImageList.add(ImageUrl);
                    }
                }

                // DiscussionDetailsActivity.CommentAdapter commentAdapter = new DiscussionDetailsActivity.CommentAdapter(DiscussionDetailsActivity.this, CommentUserList, CommentBodyList);
                // listView.setAdapter(commentAdapter);
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(DiseaseDetailsActivity.this, DiseaseImageList);
                SliderViewPager.setAdapter(viewPagerAdapter);
            }

        });

//        ImageSlider = (FlipperLayout)findViewById(R.id.image_slider);
//        setImages();
    }





    private void takePhoto() {
//		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
//		startActivityForResult(intent, 0);
        dispatchTakePictureIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        Log.i(TAG, "onActivityResult: " + this);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            setPic();
//			Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//			if (bitmap != null) {
//				mImageView.setImageBitmap(bitmap);
//				try {
//					sendPhoto(bitmap);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
        }
    }

    String mCurrentPhotoPath;

    static final int REQUEST_TAKE_PHOTO = 1;
    File photoFile = null;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * http://developer.android.com/training/camera/photobasics.html
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        String storageDir = Environment.getExternalStorageDirectory() + "/picupload";
        File dir = new File(storageDir);
        if (!dir.exists())
            dir.mkdir();

        File image = new File(storageDir + "/" + imageFileName + ".jpg");

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.i(TAGG, "photo path = " + mCurrentPhotoPath);
        Toast.makeText(this , mCurrentPhotoPath, Toast.LENGTH_LONG).show();
        return image;
    }

    private void setPic() {
//        // Get the dimensions of the View
//        int targetW = CompareImage.getWidth();
//        int targetH = CompareImage.getHeight();
//
//        // Get the dimensions of the bitmap
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//        // Determine how much to scale down the image
//      //  int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//
//        // Decode the image file into a Bitmap sized to fill the View
//        bmOptions.inJustDecodeBounds = false;
//       /// bmOptions.inSampleSize = scaleFactor << 1;
//        bmOptions.inPurgeable = true;
//
          CompressedFilePath = SiliCompressor.with(getApplicationContext()).compress(mCurrentPhotoPath, getExternalFilesDir(Environment.DIRECTORY_PICTURES), true);

          CompareImage.setImageBitmap(BitmapFactory.decodeFile(CompressedFilePath));
//
//        Bitmap bitmap = BitmapFactory.decodeFile(CompressedFilePath, bmOptions);
//
//        Matrix mtx = new Matrix();
//        // Rotating Bitmap
//        Bitmap rotatedBMP = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mtx, true);
//
//        if (rotatedBMP != bitmap)
//            bitmap.recycle();
//
//        CompareImage.setImageBitmap(rotatedBMP);

    }

    public class ViewPagerAdapter extends PagerAdapter {

        private List<String> imageUrls;

        private Context context;
        private LayoutInflater layoutInflater;

        public ViewPagerAdapter(Context context, List<String> imageUrls){
            this.context = context;
            this.imageUrls = imageUrls;
        }



        @Override
        public int getCount() {
            return imageUrls.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.slider_image_layout, null);
            @SuppressLint("WrongViewCast") PhotoView imageView = (PhotoView)view.findViewById(R.id.slider_imageview);
            //Picasso.get().load(images[position]).into(imageView);
           // Toast.makeText(getApplicationContext(), DiseaseImageList.get(position), Toast.LENGTH_SHORT).show();
            Picasso.get().load(DiseaseImageList.get(position)).into(imageView);

            ViewPager viewPager = (ViewPager) container;
            viewPager.addView(view, 0);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            ViewPager vp = (ViewPager) container;
            View view = (View) object;
            vp.removeView(view);
        }
    }

    private void requestStoragePermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

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
