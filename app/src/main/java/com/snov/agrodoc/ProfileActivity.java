package com.snov.agrodoc;

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
import com.snov.agrodoc.Models.Discussion;
import com.snov.agrodoc.Utilities.Config;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    TextView UserName, UserEmail;
    private ListView ProfileDiscussionList;
    ArrayList<Discussion> ProfileDiscussionArray = new ArrayList<Discussion>();

    private static final String TAG = "FireLog";

    public String LoggedUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ProfileDiscussionList = (ListView)findViewById(R.id.post_list);

        FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentFirebaseUser !=null) {
            //Toast.makeText(this, currentFirebaseUser.getUid(), Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, currentFirebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, currentFirebaseUser.getDisplayName(), Toast.LENGTH_SHORT).show();
            UserName = (TextView)findViewById(R.id.profile_username);
            UserEmail = (TextView)findViewById(R.id.profile_email);

            UserName.setText(currentFirebaseUser.getDisplayName());
            UserEmail.setText(currentFirebaseUser.getEmail());
        } else {
            Toast.makeText(this, "No User", Toast.LENGTH_SHORT).show();
        }

        LoggedUserID = currentFirebaseUser.getUid();

        ShowDiscussionList();
    }

    private void ShowDiscussionList() {
        FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();
        mFireStore.collection("discussion").orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(e != null){
                    Log.d(TAG, "Error: " + e.getMessage());
                }

                for(DocumentChange doc : documentSnapshots.getDocumentChanges()){
                    if(doc.getType() == DocumentChange.Type.ADDED && LoggedUserID.equals(doc.getDocument().getString("user_id"))){
                        String name = doc.getDocument().getString("user_name");
                        String type = doc.getDocument().getString("type");
                        String body = doc.getDocument().getString("body");
                        String DocID = doc.getDocument().getId();

                        Discussion discussion = new Discussion();
                        discussion.setUserName(name);
                        discussion.setType(type);
                        discussion.setBody(body);
                        discussion.setDocID(DocID);

//                        Log.d(TAG, "Name: " + name);
//                        //Toast.makeText(getApplicationContext(), "Name: " + name , Toast.LENGTH_LONG).show();
//                        NameString = NameString + "," + name;
//                        //Toast.makeText(getApplicationContext(), NameString, Toast.LENGTH_LONG).show();
//                        NamesList.add(name);
//                        TypeList.add(type);
//                        BodyList.add(body);
//                        DocIDList.add(DocID);

                        ProfileDiscussionArray.add(discussion);
                    }
                }

                DiscussionAdapter discussionAdapter = new DiscussionAdapter(ProfileActivity.this, ProfileDiscussionArray);
                ProfileDiscussionList.setAdapter(discussionAdapter);
                //Toast.makeText(getApplicationContext(), NameString, Toast.LENGTH_LONG).show();
            }
        });
    }

    //adapter is used to bind the data from above arrays to respective UI components
    private class DiscussionAdapter extends ArrayAdapter<Discussion> {

//        private List<String> UserName;
//        private List<String> DiscussionType;
//        private List<String> DiscussionBody;
//        private List<String> DocumentID;

        ArrayList<Discussion> DiscussionArrayList = new ArrayList<Discussion>();


        private Activity context;

        //adapter constructor
        private DiscussionAdapter(Activity context, ArrayList<Discussion> DiscussionArrayList) {
            super(context, R.layout.activity_profile, DiscussionArrayList);
            this.context = context;
            this.DiscussionArrayList = DiscussionArrayList;
        }



        @NonNull
        @Override

        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent){
            View r = convertView;
            DiscussionAdapter.ViewHolder viewHolder = null;

            //things to do onclick of an item
            if(r==null){
                LayoutInflater layoutInflater = context.getLayoutInflater();
                r = layoutInflater.inflate(R.layout.discussion_item,null,true);

                viewHolder = new DiscussionAdapter.ViewHolder(r);
                r.setTag(viewHolder);
            }else{
                viewHolder = (DiscussionAdapter.ViewHolder)r.getTag();
            }


            viewHolder.user_name.setText(DiscussionArrayList.get(position).getUserName());
            viewHolder.discussion_type.setText(DiscussionArrayList.get(position).getType());
            viewHolder.discussion_body.setText(DiscussionArrayList.get(position).getBody());

            viewHolder.Card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Go to  " + DiscussionArrayList.get(position).getUserName(), Toast.LENGTH_SHORT).show();
                    Config.USER_ID= DiscussionArrayList.get(position).getUserName();
                    Config.DOC_ID= DiscussionArrayList.get(position).getDocID();
                    Intent intent = new Intent(ProfileActivity.this, DiscussionDetailsActivity.class);
                    startActivity(intent);
                }
            });


//            //bind data to UI components
//            viewHolder.user_name.setText(UserName.get(position));
//            viewHolder.discussion_type.setText(DiscussionType.get(position));
//            viewHolder.discussion_body.setText(DiscussionBody.get(position));
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
