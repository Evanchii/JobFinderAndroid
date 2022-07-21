package com.jobfinder.jobfinderandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ApplicantSignIn extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant_sign_in);

        mAuth = FirebaseAuth.getInstance();
    }

    public void _loginApplicant(View view){
        email = (EditText) findViewById(R.id.appSignIn_inputEmail);
        password = (EditText) findViewById(R.id.appSignIn_inputPassword);

        if(!email.getText().toString().trim().isEmpty() && !password.getText().toString().trim().isEmpty()){
            dialog = ProgressDialog.show(ApplicantSignIn.this,"Please Wait","Loging In",true);
            mAuth.signInWithEmailAndPassword(String.valueOf(email.getText()),String.valueOf(password.getText())).addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", view.getContext().MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("userType", "applicant");
                            editor.commit();

                            String userID = mAuth.getCurrentUser().getUid();
                            startActivity(new Intent(ApplicantSignIn.this,ApplicantDashboard.class));
                        }
                    }
            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(view.getContext() ,"Wrong Email or Password", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(view.getContext() ,"Email and Password is Required", Toast.LENGTH_SHORT).show();
        }
    }

    public void _forgotPassword(View view){
//        Link to Forgot Password
    }

    public void _signUpApplicant(View view){
//        Link to SignUp Applicant
        startActivity(new Intent(ApplicantSignIn.this, ApplicantSignUp.class));
    }

    public void _signInAsEmployer(View view){
//        Link to SignUp Applicant
        startActivity(new Intent(ApplicantSignIn.this, EmployerSignIn.class));
    }
}