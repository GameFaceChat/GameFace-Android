package com.magnitudestudios.sriharivishnu.supremevideo.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.magnitudestudios.sriharivishnu.supremevideo.Interfaces.UserLoginListener;
import com.magnitudestudios.sriharivishnu.supremevideo.R;

public class LoginScreenFragment extends Fragment implements View.OnClickListener {
    private Button goToSignUp;
    private UserLoginListener listener;

    public LoginScreenFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        goToSignUp = view.findViewById(R.id.login_btn_signup);
        goToSignUp.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn_signup:
                listener.onClickSignUpButton();
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
