package com.snov.agrodoc;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PopUpDialog extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialogue, container,false);

        EditText BodyText = (EditText)view.findViewById(R.id.body_txt);
        Button SubmitButton = (Button)view.findViewById(R.id.submit_button);
        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), BodyText.getText(), Toast.LENGTH_LONG).show();
                getDialog().dismiss();
            }
        });


        return view;



    }
}
