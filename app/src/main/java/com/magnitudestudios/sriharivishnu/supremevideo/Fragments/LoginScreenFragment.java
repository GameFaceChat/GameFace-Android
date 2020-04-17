package com.magnitudestudios.sriharivishnu.supremevideo.Fragments;

import android.content.Context;
import android.os.Bundle;
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
import com.magnitudestudios.sriharivishnu.supremevideo.Interfaces.UserLoginListener;
import com.magnitudestudios.sriharivishnu.supremevideo.R;

public class LoginScreenFragment extends Fragment implements View.OnClickListener {
    private Button loginBtn, goToSignUp;
    private UserLoginListener listener;
    private EditText emailInput, passwordInput;

    FirebaseAuth mAuth;

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn_signup:
                listener.onClickSignUpButton();
                break;
            case R.id.login_sign_button:
                signInUser();
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
