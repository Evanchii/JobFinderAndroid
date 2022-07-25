package com.jobfinder.jobfinderandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ApplicantSignIn extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;
    private String user;
    private FirebaseDatabase dbRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_applicant_sign_in);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance();
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
                            user = mAuth.getCurrentUser().getUid();
                            dbRef.getReference().child("user").child("applicant").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.hasChild(user)) {
                                        SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", view.getContext().MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.putString("userType", "applicant");
                                        editor.commit();

                                        String userID = mAuth.getCurrentUser().getUid();
                                        startActivity(new Intent(ApplicantSignIn.this, ApplicantDashboard.class));
                                    }else{
                                        Toast.makeText(ApplicantSignIn.this,"You are not a Applicant", Toast.LENGTH_SHORT);
                                        startActivity(new Intent(ApplicantSignIn.this, ApplicantSignIn.class));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


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

    public void forgotPassword(View view) {
        EditText reset = new EditText(view.getContext());
        AlertDialog.Builder resetDialog = new AlertDialog.Builder(view.getContext());
        resetDialog.setTitle("Password Reset");
        resetDialog.setMessage("Enter your email");
        resetDialog.setView(reset);

        resetDialog.setPositiveButton("Reset", (dialog, which) -> {
            String email = reset.getText().toString().trim();
            mAuth.sendPasswordResetEmail(email)
                    .addOnSuccessListener(aVoid -> Toast.makeText(ApplicantSignIn.this, "Password Reset Email sent!", Toast.LENGTH_LONG).show())
                    .addOnFailureListener(e -> Toast.makeText(ApplicantSignIn.this, "An error has occurred!", Toast.LENGTH_LONG).show());
        }).setNegativeButton("Cancel", (dialog, which) -> {});
        resetDialog.create().show();
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