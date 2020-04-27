package com.magnitudestudios.GameFace.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.magnitudestudios.GameFace.Fragments.LoginScreenFragment;
import com.magnitudestudios.GameFace.Fragments.SignUpScreenFragment;
import com.magnitudestudios.GameFace.Fragments.StartScreenFragment;
import com.magnitudestudios.GameFace.Interfaces.UserLoginListener;
import com.magnitudestudios.GameFace.R;

public class LoginActivity extends AppCompatActivity implements UserLoginListener {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        switchFragment(new StartScreenFragment());
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            goToMainActivity();
        }
    }

    private void switchFragment(Fragment f) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.login_frame_replace, f);
        fragmentTransaction.addToBackStack("");
        fragmentTransaction.commit();
    }

    @Override
    public void onClickSignUpButton() {
        switchFragment(new SignUpScreenFragment());
    }

    @Override
    public void onClickLoginButton() {
        switchFragment(new LoginScreenFragment());
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void signedInUser() {
        goToMainActivity();
    }

    @Override
    public void onBackPressed() {

    }
}