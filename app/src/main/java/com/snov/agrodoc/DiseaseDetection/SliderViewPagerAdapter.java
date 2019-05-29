package com.snov.agrodoc.DiseaseDetection;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.snov.agrodoc.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SliderViewPagerAdapter extends PagerAdapter {

    private List<String> imageUrl;

    private Context context;
    private LayoutInflater layoutInflater;
    private String[] images = {
            "https://static2.cbrimages.com/wordpress/wp-content/uploads/2019/02/batman-last-knight-on-earth-cover-header.jpg",
            "https://i.ytimg.com/vi/JUWgGzqxgQo/maxresdefault.jpg",
            "https://www.syfy.com/sites/syfy/files/wire/legacy/screen_shot_2015-02-17_at_3.27.39_pm.png.jpeg"};

    public SliderViewPagerAdapter(Context context, List<String> imageUrl){

        this.context = context;
        this.imageUrl = imageUrl;
    }

    @Override
    public int getCount() {
        return images.length;
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
        Picasso.get().load(images[position]).into(imageView);

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