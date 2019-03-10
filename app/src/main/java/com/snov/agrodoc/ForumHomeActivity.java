package com.snov.agrodoc;

import android.app.Activity;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ForumHomeActivity extends AppCompatActivity {

    private static final String TAG = "FireLog";
    private ListView mMainList;
    List<String> NamesList = new ArrayList<String>();
    List<String> TypeList = new ArrayList<String>();
    List<String> BodyList = new ArrayList<String>();
    String NameString = "";
    //private FirebaseFirestore mFireStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_home);

        mMainList = (ListView)findViewById(R.id.discussion_list);
        //Toast.makeText(getApplicationContext(), "Name: " , Toast.LENGTH_LONG).show();

        FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();

        mFireStore.collection("discussion").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(e != null){
                    Log.d(TAG, "Error: " + e.getMessage());
                }

                for(DocumentChange doc : documentSnapshots.getDocumentChanges()){
                    if(doc.getType() == DocumentChange.Type.ADDED){
                        String name = doc.getDocument().getString("user_name");
                        String type = doc.getDocument().getString("type");
                        String body = doc.getDocument().getString("body");

                        Log.d(TAG, "Name: " + name);
                        //Toast.makeText(getApplicationContext(), "Name: " + name , Toast.LENGTH_LONG).show();
                        NameString = NameString + "," + name;
                        //Toast.makeText(getApplicationContext(), NameString, Toast.LENGTH_LONG).show();
                        NamesList.add(name);
                        TypeList.add(type);
                        BodyList.add(body);
                    }
                }

                DiscussionAdapter discussionAdapter = new DiscussionAdapter(ForumHomeActivity.this, NamesList, TypeList, BodyList);
                mMainList.setAdapter(discussionAdapter);
                Toast.makeText(getApplicationContext(), NameString, Toast.LENGTH_LONG).show();
            }
        });




    }

    //adapter is used to bind the data from above arrays to respective UI components
    private class DiscussionAdapter extends ArrayAdapter<String> {

        private List<String> UserName;
        private List<String> DiscussionType;
        private List<String> DiscussionBody;

        private Activity context;

        //adapter constructor
        private DiscussionAdapter(Activity context, List<String> UserName, List<String> DiscussionType, List<String> DiscussionBody) {
            super(context, R.layout.activity_forum_home, UserName);
            this.context = context;
            this.UserName = UserName;
            this.DiscussionType = DiscussionType;
            this.DiscussionBody = DiscussionBody;
        }



        @NonNull
        @Override

        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent){
            View r = convertView;
            ViewHolder viewHolder = null;

            //things to do onclick of an item
            if(r==null){
                LayoutInflater layoutInflater = context.getLayoutInflater();
                r = layoutInflater.inflate(R.layout.discussion_item,null,true);

                viewHolder = new ViewHolder(r);
                r.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)r.getTag();
            }



            //bind data to UI components
            viewHolder.user_name.setText(UserName.get(position));
            viewHolder.discussion_type.setText(DiscussionType.get(position));
            viewHolder.discussion_body.setText(DiscussionBody.get(position));
            viewHolder.Card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Go to  " + UserName.get(position), Toast.LENGTH_SHORT).show();
                }
            });

            return r;

        }

        //Defining UI components
        class ViewHolder{
            TextView user_name;
            TextView discussion_type;
            TextView discussion_body;
            CardView Card;


            ViewHolder(View v){
                user_name = (TextView)v.findViewById(R.id.username);
                discussion_type = (TextView)v.findViewById(R.id.discussion_type);
                discussion_body = (TextView)v.findViewById(R.id.discussion_body);
                Card = (CardView)v.findViewById(R.id.discussion_item_card);

            }


        }
    }
}
