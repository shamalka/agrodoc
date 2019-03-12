package com.snov.agrodoc;

import android.os.Bundle;
import android.support.annotation.BinderThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PopUpDialog extends DialogFragment {

    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    RadioGroup radioGroup;
    RadioButton radioButton;

    String DiscussionType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialogue, container,false);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        radioGroup = (RadioGroup)view.findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                int selectedId=radioGroup.getCheckedRadioButtonId();
                radioButton=(RadioButton)view.findViewById(selectedId);
                DiscussionType=radioButton.getText().toString();
                //Toast.makeText(getContext(),radioButton.getText(),Toast.LENGTH_SHORT).show();
            }

        });

        ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.progress_bar_pop);
        progressBar.setVisibility(View.GONE);
        EditText BodyText = (EditText)view.findViewById(R.id.body_txt);
        Button SubmitButton = (Button)view.findViewById(R.id.submit_button);
        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), BodyText.getText(), Toast.LENGTH_LONG).show();

                progressBar.setVisibility(View.VISIBLE);
                String body = BodyText.getText().toString();



                Map<String, String> DiscussionMap = new HashMap<>();
                DiscussionMap.put("user_name", user.getDisplayName());
                DiscussionMap.put("type", DiscussionType);
                DiscussionMap.put("body", body);

                mFirestore.collection("discussion").add(DiscussionMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getContext(), "Discussion Added", Toast.LENGTH_LONG).show();
                        getDialog().dismiss();
                    }


                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed, Try again.!", Toast.LENGTH_LONG).show();
                    }
                });


            }
        });


        return view;



    }
}
