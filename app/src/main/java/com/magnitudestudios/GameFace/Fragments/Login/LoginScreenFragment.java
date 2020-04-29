package com.magnitudestudios.GameFace.Fragments.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.magnitudestudios.GameFace.Interfaces.UserLoginListener;
import com.magnitudestudios.GameFace.R;

public class LoginScreenFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "LoginScreenFragment";
    private Button loginBtn, goToSignUp;
    private UserLoginListener listener;
    private EditText emailInput, passwordInput;
    private RelativeLayout signInWithGoogle;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private static final int GOOGLE_RESULT = 101;

    public LoginScreenFragment() {}


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        goToSignUp = view.findViewById(R.id.login_btn_signup);
        goToSignUp.setOnClickListener(this);

        emailInput = view.findViewById(R.id.login_emailInput);
        passwordInput = view.findViewById(R.id.login_passwordInput);
        loginBtn = view.findViewById(R.id.login_sign_button);
        loginBtn.setOnClickListener(this);
        signInWithGoogle = view.findViewById(R.id.login_card_signinwithgoogle);
        signInWithGoogle.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_oAuth_client_ID))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        mAuth = FirebaseAuth.getInstance();
        return view;
    }

    private void signInUser() {
        mAuth.signInWithEmailAndPassword(emailInput.getText().toString(), passwordInput.getText().toString())
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        listener.signedInUser();
                    } else {
                        Toast.makeText(getContext(), "Login Failed", Toast.LENGTH_LONG).show();
                    }
                }
            });
    }

    private boolean validate() {
        boolean valid = true;
        if (emailInput.getText().toString().isEmpty()) {
            emailInput.setError("Please enter an email address");
            valid = false;
        }
        if (passwordInput.getText().toString().isEmpty()) {
            passwordInput.setError("Please enter your password");
            valid = false;
        }
        return valid;
    }

    private void onClickSignWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_RESULT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_RESULT) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("LoginFragment", "Google sign in failed", e);
                Toast.makeText(getContext(), "Sign in failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    listener.signedInUser();
                }
                else {
                    Toast.makeText(getContext(), "Sign up failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn_signup:
                listener.onClickSignUpButton();
                break;
            case R.id.login_sign_button:
                if (validate()) {
                    signInUser();
                }
                break;
            case R.id.login_card_signinwithgoogle:
                onClickSignWithGoogle();
                break;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (UserLoginListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " Must implement UserLoginListener");
        }
    }
}
