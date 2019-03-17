package com.snov.agrodoc.Market;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.snov.agrodoc.Forum.DiscussionDetailsActivity;
import com.snov.agrodoc.Forum.ForumHomeActivity;
import com.snov.agrodoc.Models.Product;
import com.snov.agrodoc.R;
import com.snov.agrodoc.Utilities.Config;

import java.util.ArrayList;
import java.util.List;

public class MarketHomeActivity extends AppCompatActivity {

    private static final String TAG = "FireLog";
    private ListView ProductItemList;
    ArrayList<Product> ProductList = new ArrayList<Product>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_home);

        ProductItemList = (ListView)findViewById(R.id.product_item_list);
        FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();

        HorizontalScrollView horizontalScrollView = (HorizontalScrollView)findViewById(R.id.category_layout);
        TextView SeemoreText = (TextView)findViewById(R.id.see_more_txt);

        CardView SeemoreButton = (CardView) findViewById(R.id.seemore_btn);
        SeemoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Config.SEEMORE_FLAG.equals("0")){
                    horizontalScrollView.setVisibility(View.GONE);
                    Config.SEEMORE_FLAG="1";
                    SeemoreText.setText("See More");
                }else{
                    horizontalScrollView.setVisibility(View.VISIBLE);
                    Config.SEEMORE_FLAG="0";
                    SeemoreText.setText("See Less");
                }

            }
        });

        mFireStore.collection("products").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(e != null){
                    Log.d(TAG, "Error: " + e.getMessage());
                }

                for(DocumentChange doc : documentSnapshots.getDocumentChanges()){
                    if(doc.getType() == DocumentChange.Type.ADDED){
                           // Toast.makeText(getApplicationContext(), doc.getDocument().getString("product_name"), Toast.LENGTH_LONG).show();
                            Product product = new Product();
                            product.setProductName(doc.getDocument().getString("product_name"));
                            product.setSupplierName(doc.getDocument().getString("supplier"));
                            product.setPrice(doc.getDocument().getString("price"));
                            product.setRating(doc.getDocument().getString("rating"));
                            product.setType(doc.getDocument().getString("type"));
                        //product.setTimestamp(doc.getDocument().getString("timestamp"));
//                        String DocID = doc.getDocument().getId();
//
                            ProductList.add(product);
                        //Toast.makeText(getApplicationContext(), "Name: " + name , Toast.LENGTH_LONG).show();

                        //Toast.makeText(getApplicationContext(), NameString, Toast.LENGTH_LONG).show();
//                        NamesList.add(name);
//                        TypeList.add(type);
//                        BodyList.add(body);
//                        DocIDList.add(DocID);
                    }
                }


                ProductAdapter productAdapter = new ProductAdapter(MarketHomeActivity.this,ProductList);
                ProductItemList.setAdapter(productAdapter);



//                ForumHomeActivity.DiscussionAdapter discussionAdapter = new ForumHomeActivity.DiscussionAdapter(ForumHomeActivity.this, NamesList, TypeList, BodyList,DocIDList);
//                mMainList.setAdapter(discussionAdapter);
                //Toast.makeText(getApplicationContext(), NameString, Toast.LENGTH_LONG).show();
            }
        });
    }

    //adapter is used to bind the data from above arrays to respective UI components
    private class ProductAdapter extends ArrayAdapter<Product> {

//        private List<String> UserName;
//        private List<String> DiscussionType;
//        private List<String> DiscussionBody;
//        private List<String> DocumentID;
        //private ArrayList<Product> Products;
        private ArrayList<Product> Products = new ArrayList<Product>();



        private Activity context;

        //adapter constructor
        private ProductAdapter(Activity context, ArrayList<Product> Products) {
            super(context, R.layout.activity_market_home, Products);
            this.context = context;
            this.Products=Products;

        }



        @NonNull
        @Override

        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent){
            View r = convertView;
            ProductAdapter.ViewHolder viewHolder = null;

            //things to do onclick of an item
            if(r==null){
                LayoutInflater layoutInflater = context.getLayoutInflater();
                r = layoutInflater.inflate(R.layout.market_product_item,null,true);

                viewHolder = new ProductAdapter.ViewHolder(r);
                r.setTag(viewHolder);
            }else{
                viewHolder = (ProductAdapter.ViewHolder)r.getTag();
            }



            //bind data to UI components
            viewHolder.product_name.setText(Products.get(position).getProductName());
            viewHolder.supplier.setText(Products.get(position).getSupplierName());
            viewHolder.rating.setText(Products.get(position).getRating());
            viewHolder.price.setText(Products.get(position).getPrice());
            viewHolder.type.setText(Products.get(position).getType());
//            viewHolder.Card.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(getContext(), "Go to  " + UserName.get(position), Toast.LENGTH_SHORT).show();
//                    Config.USER_ID=UserName.get(position);
//                    Config.DOC_ID=DocumentID.get(position);
//                    Intent intent = new Intent(ForumHomeActivity.this, DiscussionDetailsActivity.class);
//                    startActivity(intent);
//                }
//            });

            return r;

        }

        //Defining UI components
        class ViewHolder{
            TextView product_name;
            TextView supplier;
            TextView rating;
            TextView price;
            TextView type;
            CardView product_item;

            ViewHolder(View v){
                product_name = (TextView)v.findViewById(R.id.product_name);
                supplier = (TextView)v.findViewById(R.id.supplier_name);
                rating = (TextView)v.findViewById(R.id.product_rating);
                price = (TextView)v.findViewById(R.id.product_price);
                type = (TextView)v.findViewById(R.id.product_type);

            }


        }
    }
}
