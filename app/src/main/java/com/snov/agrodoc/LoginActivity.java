package com.snov.agrodoc;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.snov.agrodoc.Forum.ForumHomeActivity;
import com.squareup.picasso.Picasso;

public class LoginActivity extends AppCompatActivity {

    static final int GOOGLE_SIGN = 123;
    FirebaseAuth mAuth;
    Button LogoutButton;
    SignInButton LoginButton;
    ProgressBar progressBar;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton = findViewById(R.id.login_btn);
        LogoutButton = findViewById(R.id.logout_btn);
        progressBar = (ProgressBar)findViewById(R.id.progress_circular);
        progressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        LoginButton.setOnClickListener(v -> SignInGoogle());
        LogoutButton.setOnClickListener(v -> LogOut());

        if(mAuth.getCurrentUser()!=null){
            FirebaseUser user = mAuth.getCurrentUser();
            UpdateUI(user);
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        }


    }

    void SignInGoogle(){
        progressBar.setVisibility(View.VISIBLE);
        Intent signIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signIntent, GOOGLE_SIGN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GOOGLE_SIGN){
            Task<GoogleSignInAccount> task = GoogleSignIn
                    .getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if(account!=null)firebaseAuthWithGoogle(account);
            }catch (ApiException e){
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d("TAG", "firebaseAuthWithGoogle: " + account.getId());
        AuthCredential authCredential = GoogleAuthProvider
                .getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        progressBar.setVisibility(View.GONE);
                        Log.d("TAG", "Sign In Success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        UpdateUI(user);
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }else{
                        progressBar.setVisibility(View.GONE);
                        Log.w("TAG", "Sign in Failed", task.getException());
                        Toast.makeText(this, "Sign In Failed", Toast.LENGTH_LONG);
                        UpdateUI(null);
                    }
                });
    }

    private void UpdateUI(FirebaseUser user) {
        if(user!=null){
            String name = user.getDisplayName();
            String email = user.getUid();
            String Photo = String.valueOf(user.getPhotoUrl());

            TextView UserName = (TextView)findViewById(R.id.user_name);
            TextView UserEmail = (TextView)findViewById(R.id.user_email);
            ImageView UserImage = (ImageView)findViewById(R.id.user_image);

            UserName.setText(name);
            UserEmail.setText(email);

            Picasso.get().load(Photo).into(UserImage);
            LoginButton.setVisibility(View.GONE);
            LogoutButton.setVisibility(View.VISIBLE);
        }else{
            LoginButton.setVisibility(View.VISIBLE);
            LogoutButton.setVisibility(View.GONE);
        }
    }

    void LogOut(){
        TextView UserName = (TextView)findViewById(R.id.user_name);
        TextView UserEmail = (TextView)findViewById(R.id.user_email);
        ImageView UserImage = (ImageView)findViewById(R.id.user_image);

        UserName.setText("Name");
        UserEmail.setText("Email");
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this,
                        task->UpdateUI(null));
    }
}
