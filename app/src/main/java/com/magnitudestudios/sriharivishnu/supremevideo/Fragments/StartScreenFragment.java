package com.magnitudestudios.sriharivishnu.supremevideo.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.magnitudestudios.sriharivishnu.supremevideo.Interfaces.UserLoginListener;
import com.magnitudestudios.sriharivishnu.supremevideo.R;

public class StartScreenFragment extends Fragment implements View.OnClickListener {

    private RelativeLayout signUpWithGoogle, signUpWithEmail;
    private Button goToLogin;
    private UserLoginListener listener;
    public StartScreenFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_startscreen, container, false);
        signUpWithGoogle = view.findViewById(R.id.startscreen_card_signupwithgoogle);
        signUpWithGoogle.setOnClickListener(this);

        signUpWithEmail = view.findViewById(R.id.startscreen_card_signupwithemail);
        signUpWithEmail.setOnClickListener(this);

        goToLogin = view.findViewById(R.id.startscreen_gottologin);
        goToLogin.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startscreen_card_signupwithgoogle:
                break;
            case R.id.startscreen_card_signupwithemail:
                listener.onClickSignUpButton();
                break;
            case R.id.startscreen_gottologin:
                listener.onClickLoginButton();
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