package com.magnitudestudios.sriharivishnu.supremevideo.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.magnitudestudios.sriharivishnu.supremevideo.Interfaces.UserLoginListener;
import com.magnitudestudios.sriharivishnu.supremevideo.R;
import com.magnitudestudios.sriharivishnu.supremevideo.pojo.User;

public class SignUpScreenFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "SignUpScreenFragment";
    private Button goToLogin, signUp;
    private UserLoginListener listener;
    private EditText usernameInput, emailInput, passwordInput, cPasswordInput;

    private FirebaseAuth mAuth;

    public SignUpScreenFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        //Buttons
        goToLogin = view.findViewById(R.id.signup_gottologin);
        goToLogin.setOnClickListener(this);
        signUp = view.findViewById(R.id.signup_button_signup);
        signUp.setOnClickListener(this);

        //EditTexts
        usernameInput = view.findViewById(R.id.signup_usernameInput);
        emailInput = view.findViewById(R.id.signup_emailInput);
        passwordInput = view.findViewById(R.id.signup_passwordInput);
        cPasswordInput = view.findViewById(R.id.signup_cPasswordInput);

        mAuth = FirebaseAuth.getInstance();
        return view;
    }

    private boolean validateDetails() {
        boolean valid = true;
        if (!(usernameInput.getText().toString().length() > 5)) {
            valid = false;
            usernameInput.setError("Username must be more than 5 characters");
        }
        if (!emailInput.getText().toString().contains("@") || !emailInput.getText().toString().contains(".")) {
            emailInput.setError("Please enter a valid email");
        }
        if (!(passwordInput.getText().toString().length() > 6)) {
            valid = false;
            passwordInput.setError("Password must be more than 6 characters in length");
        }
        if (!passwordInput.getText().toString().equals(cPasswordInput.getText().toString())) {
            valid = false;
            cPasswordInput.setError("Passwords must match");
        }
        return valid;
    }

    private void signUpUser(final String username, final String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onCompleteSignUp: SUCCESS");
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("users");
                            myRef.child(mAuth.getCurrentUser().getUid()).setValue(new User(email, username));
                            listener.signedInUser();
                        } else {
                            Log.e(TAG, "onCompleteSignUp: FAILURE");
                            Toast.makeText(getContext(), "Sign Up Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signup_gottologin:
                listener.onClickLoginButton();
                break;
            case R.id.signup_button_signup:
                if (validateDetails()) {
                    signUpUser(usernameInput.getText().toString(), emailInput.getText().toString(), passwordInput.getText().toString());
                }
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
