package com.first75494.facecallx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {

    private CountryCodePicker ccp;
    private EditText phoneText, codeText;
    private Button continueBtn;
    private String checker = "",phoneNo = "";
    private RelativeLayout relativeLayout;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        phoneText = findViewById(R.id.phonetext);
        codeText = findViewById(R.id.codeText);
        continueBtn = findViewById(R.id.SignUpBtn);
        relativeLayout = findViewById(R.id.phoneAuth);
        ccp = findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(phoneText);

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(continueBtn.getText().equals("Submit") || checker.equals("Code_sent")){

                    String verificationCode = codeText.getText().toString();

                    if(verificationCode.equals("")){
                        Toast.makeText(SignUpActivity.this,"Please Enter your Code",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(SignUpActivity.this,"Please Wait We are Verifying Code",Toast.LENGTH_LONG).show();

                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,verificationCode);
                        signInWithPhoneAuthCredential(credential);
                    }
                }else{
                    phoneNo = ccp.getFullNumberWithPlus();

                    if(!phoneNo.equals("")){
                        Toast.makeText(SignUpActivity.this,"Please Wait We are Verifying your No",Toast.LENGTH_LONG).show();

                        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNo,60,TimeUnit.SECONDS,SignUpActivity.this,callbacks);


                    }else{
                        Toast.makeText(SignUpActivity.this,"Please Enter a Valid Format",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(SignUpActivity.this,"Invalid Phone No",Toast.LENGTH_SHORT).show();

                relativeLayout.setVisibility(View.VISIBLE);
                continueBtn.setText("SignUp");
                codeText.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                mVerificationId = s;
                mResendToken = forceResendingToken;

                relativeLayout.setVisibility(View.GONE);
                checker = "Code_sent";
                continueBtn.setText("Submit");
                codeText.setVisibility(View.VISIBLE);

                Toast.makeText(SignUpActivity.this,"Code Sent Successfully",Toast.LENGTH_SHORT).show();
            }
        };
    }


    @Override
    protected void onStart() {
        super.onStart();

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    if(firebaseUser!=null){
        Intent mainIntent = new Intent(SignUpActivity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(SignUpActivity.this,"Logged in Successfully..",Toast.LENGTH_SHORT).show();
                            sendUserToMainActivity();

                        } else {
                            Toast.makeText(SignUpActivity.this,"Error Occurred!",Toast.LENGTH_SHORT).show();
                            }
                        }
                });
    }

    private void sendUserToMainActivity(){
        Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

}
