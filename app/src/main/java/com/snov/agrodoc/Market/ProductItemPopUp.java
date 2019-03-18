package com.snov.agrodoc.Market;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.snov.agrodoc.R;

public class ProductItemPopUp extends DialogFragment {

    TextView ItemName,ItemDescription,ItemPrice,ItemRating;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.product_item, container,false);

        ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.product_item_progress_bar);
        progressBar.setVisibility(View.GONE);

        ItemName = (TextView)view.findViewById(R.id.popup_item_name);
        ItemDescription = (TextView)view.findViewById(R.id.popup_item_description);
        ItemPrice = (TextView)view.findViewById(R.id.popup_item_price);


        Bundle bundle = this.getArguments();
        if(bundle!=null){
           // Toast.makeText(getContext(), bundle.getString("item_name"), Toast.LENGTH_LONG).show();
            ItemName.setText(bundle.getString("item_name"));
            ItemPrice.setText(bundle.getString("item_price"));
        }

        return view;
    }
}