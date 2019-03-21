package com.snov.agrodoc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.snov.agrodoc.Forum.ForumHomeActivity;
import com.snov.agrodoc.Market.MarketHomeActivity;

public class HomeActivity extends AppCompatActivity {

    Button GotoDiscussion, GotoMarket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        GotoDiscussion = (Button)findViewById(R.id.discussion_btn_home);
        GotoMarket = (Button)findViewById(R.id.marketplace_btn_home);

        GotoDiscussion.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ForumHomeActivity.class);
            startActivity(intent);
        });

        GotoMarket.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MarketHomeActivity.class);
            startActivity(intent);
        });


    }
}
