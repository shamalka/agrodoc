package com.snov.agrodoc.DiseaseDetection;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.snov.agrodoc.Forum.DiscussionDetailsActivity;
import com.snov.agrodoc.R;
import com.snov.agrodoc.Utilities.Config;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import technolifestyle.com.imageslider.FlipperLayout;
import technolifestyle.com.imageslider.FlipperView;

public class DiseaseDetailsActivity extends AppCompatActivity {

    //FlipperLayout ImageSlider;
    ViewPager SliderViewPager;
    private static final String TAG = "FireLog";

    public List<String> DiseaseImageList = new ArrayList<String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_details);

        SliderViewPager = (ViewPager)findViewById(R.id.slider_view_pager);

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
            ImageView imageView = (ImageView)view.findViewById(R.id.slider_imageview);
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


}
