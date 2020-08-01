package heexa.myproject.myupload;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class Signup_Fragment extends Fragment {
    private onFragmentBtnSelected listener;
    EditText inputEmail, inputPassword,mFullname,mPhone;
    Button  Signup;
    ProgressBar progressBar;
    FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup_fragment, container, false);

        mFullname = (EditText) view.findViewById(R.id.Fname);
        inputEmail = (EditText) view.findViewById(R.id.Email);
        inputPassword = (EditText) view.findViewById(R.id.Password);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        Signup = view.findViewById(R.id.Register);
        auth = FirebaseAuth.getInstance();

        progressBar.setVisibility(View.GONE);
        Signup.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                final String FullName = mFullname.getText().toString().trim();
                final String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getActivity(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getActivity(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                //Create user
                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Register successfully...!", Toast.LENGTH_SHORT).show();
                            listener.onButtonSelected();
                            progressBar.setVisibility(View.GONE);
                        }
                        else {
                            Toast.makeText(getContext(), "Error...!!!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
         return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof onFragmentBtnSelected) {
         listener = (onFragmentBtnSelected) context;
        }else {
            throw new ClassCastException(context.toString()+"must impliment");
        }
    }

    public interface onFragmentBtnSelected{
        public void onButtonSelected();
    }
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

}

