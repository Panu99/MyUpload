package heexa.myproject.myupload;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class Otp_Verification extends Fragment {
    private onSelected listener;
    EditText mPhone,codeEnter;
    Button Click,resend ;
    ProgressBar progressBar;
    FirebaseAuth auth;
    TextView state;
    CountryCodePicker codePicker;
    String VerificationId,resndingotp;
    PhoneAuthProvider.ForceResendingToken Token;
    Boolean VerificationinProgress = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.otp_verfication, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.ProgressBar);
        Click = view.findViewById(R.id.Login);
        resend = view.findViewById(R.id.Resend);
        auth = FirebaseAuth.getInstance();
        mPhone = (EditText) view.findViewById(R.id.Phone);
        codeEnter = (EditText) view.findViewById(R.id.OTP);
        state = (TextView)view.findViewById(R.id.Sending);
        codePicker = (CountryCodePicker) view.findViewById(R.id.ccp);

        progressBar.setVisibility(View.GONE);

        Click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (!VerificationinProgress){
                   if (!mPhone.getText().toString().isEmpty() && mPhone.getText().toString().length() == 10){
                       String phoneNum = "+"+codePicker.getSelectedCountryCode()+mPhone.getText().toString();
                       state.setText("Sending OTP...");
                       progressBar.setVisibility(View.VISIBLE);
                       state.setVisibility(View.VISIBLE);
                       Click.setEnabled(true);
                       requestOtp(phoneNum);


                   }else {
                       mPhone.setError("Error Please Enter Phone Number Currectly");
                       progressBar.setVisibility(View.GONE);
                   }
               }else{
                   String UserOTP= codeEnter.getText().toString();
                   if (!UserOTP.isEmpty() && UserOTP.length() == 6){
                       PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerificationId,UserOTP);
                       verifyauth(credential);
                   }else {
                       codeEnter.setError("Valid OTP is Required");
                       progressBar.setVisibility(View.GONE);
                   }
               }
            }

            private void verifyauth(PhoneAuthCredential credential) {
                progressBar.setVisibility(View.VISIBLE);
                auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            progressBar.setVisibility(View.VISIBLE);
                            Toast.makeText(getActivity(), "Authentication is Successful", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            listener.oSelected();
                        }else{
                            progressBar.setVisibility(View.VISIBLE);
                            Toast.makeText(getActivity(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }


            private void requestOtp(final String phoneNum) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNum, 30L, TimeUnit.SECONDS, getActivity(), new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        state.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        codeEnter.setVisibility(View.VISIBLE);
                        VerificationId =s;
                        Click.setText("Verify");
                        VerificationinProgress = true;
                        Token=forceResendingToken;


                    }

                    @Override
                    public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                        Toast.makeText(getActivity(), "OTP Expired,Re-Request the OTP", Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        verifyauth(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(getActivity(), "Cannot Creat Account"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Otp_Verification.onSelected) {
            listener = (Otp_Verification.onSelected) context;
        }else {
            throw new ClassCastException(context.toString()+"must impliment");
        }
    }

    public interface onSelected{
        public void oSelected();
    }

}
