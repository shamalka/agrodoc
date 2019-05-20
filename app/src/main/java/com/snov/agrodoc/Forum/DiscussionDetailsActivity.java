package com.snov.agrodoc.Forum;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.snov.agrodoc.R;
import com.snov.agrodoc.Utilities.Config;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class DiscussionDetailsActivity extends AppCompatActivity {

    private static final String TAG = "FireLog";
    FirebaseAuth mAuth;
    Button AddCommentButton;
    List<String> CommentUserList = new ArrayList<String>();
    List<String> CommentBodyList = new ArrayList<String>();
    Integer Count=0;

    ImageView ColToolbarImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView listView = findViewById(R.id.listView);

        ColToolbarImage = (ImageView)findViewById(R.id.header);
        //Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/agrodocv2.appspot.com/o/images%2Fea229dc2-5b3a-4181-b6e2-bbbb0d70db0f?alt=media&token=e8021e18-6fc8-4034-9d29-43fb1482131e").into(ColToolbarImage);
//        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
//                new String[] {"Copy", "Paste", "Cut", "Delete", "Convert", "Open"}));

        TextView DisBody = (TextView)findViewById(R.id.discussion_body_item);
        TextView DisTitle = (TextView)findViewById(R.id.title_text);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.details_progress);
        progressBar.setVisibility(View.VISIBLE);

        TextView CommentCount = (TextView)findViewById(R.id.comment_count);

        AddCommentButton = (Button)findViewById(R.id.add_comment_btn);
        AddCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCommentDialog addCommentDialog = new AddCommentDialog();
                addCommentDialog.show(getSupportFragmentManager(), "AddCommentDialog");
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, Config.DOC_ID, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();

        //Discussion body
        DocumentReference docRef = mFireStore.collection("discussions").document(Config.DOC_ID.toString());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        DisTitle.setText(document.getString("title"));
                        DisBody.setText(document.getString("body"));
                        Picasso.get().load(document.getString("image_url")).into(ColToolbarImage);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(getApplicationContext(), "No such document", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        //Comment lsit
        mFireStore.collection("discussions").document(Config.DOC_ID).collection("comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(e != null){
                    Log.d(TAG, "Error: " + e.getMessage());
                }

                for(DocumentChange doc : documentSnapshots.getDocumentChanges()){
                    if(doc.getType() == DocumentChange.Type.ADDED){
                        String UserName = doc.getDocument().getString("user_name");
                        String CommentBody  = doc.getDocument().getString("comment_body");
                        String DocID = doc.getDocument().getId();


                        //Toast.makeText(getApplicationContext(), "Comment" + CommentBody , Toast.LENGTH_LONG).show();
                        CommentUserList.add(UserName);
                        CommentBodyList.add(CommentBody);
                        Count=Count+1;


                    }
                }

                Toast.makeText(getApplicationContext(), "Count: " + Count, Toast.LENGTH_LONG).show();
                CommentCount.setText(Count.toString());
                CommentAdapter commentAdapter = new CommentAdapter(DiscussionDetailsActivity.this, CommentUserList, CommentBodyList);
                listView.setAdapter(commentAdapter);

            }


        });


    }


    //adapter is used to bind the data from above arrays to respective UI components
    private class CommentAdapter extends ArrayAdapter<String> {

        private List<String> UserName;
        private List<String> CommentBody;


        private Activity context;

        //adapter constructor
        private CommentAdapter(Activity context, List<String> UserName, List<String> CommentBody) {
            super(context, R.layout.activity_discussion_details, UserName);
            this.context = context;
            this.UserName = UserName;
            this.CommentBody = CommentBody;
        }



        @NonNull
        @Override

        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent){
            View r = convertView;
            CommentAdapter.ViewHolder viewHolder = null;

            //things to do onclick of an item
            if(r==null){
                LayoutInflater layoutInflater = context.getLayoutInflater();
                r = layoutInflater.inflate(R.layout.comment_list_item,null,true);

                viewHolder = new CommentAdapter.ViewHolder(r);
                r.setTag(viewHolder);
            }else{
                viewHolder = (CommentAdapter.ViewHolder)r.getTag();
            }



            //bind data to UI components
            viewHolder.user_name.setText(UserName.get(position));
            viewHolder.comment_body.setText(CommentBody.get(position));
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
            TextView comment_body;
            CardView Card;


            ViewHolder(View v){
                user_name = (TextView)v.findViewById(R.id.comment_user_name);
                comment_body = (TextView)v.findViewById(R.id.comment_body);
                Card = (CardView)v.findViewById(R.id.comment_card);

            }


        }
    }
}
