package com.jobfinder.jobfinderandroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class EmployerSignIn extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;
    private ProgressDialog dialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_sign_in);

        mAuth = FirebaseAuth.getInstance();
    }

    public void _loginEmployer(View view){
        Log.d(String.valueOf(this),"Loging In");
        email = (EditText) findViewById(R.id.empSignIn_inputEmail);
        password = (EditText) findViewById(R.id.empSignIn_inputPassword);

        if(!email.getText().toString().trim().isEmpty() && !password.getText().toString().trim().isEmpty()){
            dialog = ProgressDialog.show(EmployerSignIn.this,"Please Wait","Loging In",true);
            mAuth.signInWithEmailAndPassword(String.valueOf(email.getText()),String.valueOf(password.getText())).addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isComplete()){
                                SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", view.getContext().MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString("userType", "employer");
                                editor.commit();

                                String userID = mAuth.getCurrentUser().getUid();
                                startActivity(new Intent(EmployerSignIn.this,EmployerDashboard.class));
                            }
                        }
                    }
            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(view.getContext() ,"Wrong Email or Password", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
//            Error
            Toast.makeText(view.getContext(),"Email and Password Required",Toast.LENGTH_SHORT).show();
        }
    }

    public void _forgotPassword(View view){
//        Link to Forgot Password
    }
    public void _signUpEmployer(View view){
//        Link to SignUp Employer
        startActivity(new Intent(EmployerSignIn.this, EmployerSignUp.class));
    }

    public void _signInAsApplicant(View view){
//        Link to Applicant Sign In
        startActivity(new Intent(EmployerSignIn.this, ApplicantSignIn.class));
    }
}