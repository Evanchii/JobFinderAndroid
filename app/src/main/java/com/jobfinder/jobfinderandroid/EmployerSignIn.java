package com.jobfinder.jobfinderandroid;

import android.app.AlertDialog;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EmployerSignIn extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseDatabase dbRef;
    private String user;
    private EditText email;
    private EditText password;
    private ProgressDialog dialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_employer_sign_in);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance();
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
                                user = mAuth.getCurrentUser().getUid();
                                dbRef.getReference().child("user").child("employer").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.hasChild(user)){
                                            SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", view.getContext().MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedpreferences.edit();
                                            editor.putString("userType", "employer");
                                            editor.commit();

                                            String userID = mAuth.getCurrentUser().getUid();
                                            startActivity(new Intent(EmployerSignIn.this,EmployerDashboard.class));
                                        }else{
                                            Toast.makeText(EmployerSignIn.this,"You are not an Employer", Toast.LENGTH_SHORT);
                                            startActivity(new Intent(EmployerSignIn.this, EmployerSignIn.class));
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

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

    public void forgotPassword(View view) {
        EditText reset = new EditText(view.getContext());
        AlertDialog.Builder resetDialog = new AlertDialog.Builder(view.getContext());
        resetDialog.setTitle("Password Reset");
        resetDialog.setMessage("Enter your email");
        resetDialog.setView(reset);

        resetDialog.setPositiveButton("Reset", (dialog, which) -> {
            String email = reset.getText().toString().trim();
            mAuth.sendPasswordResetEmail(email)
                    .addOnSuccessListener(aVoid -> Toast.makeText(EmployerSignIn.this, "Password Reset Email sent!", Toast.LENGTH_LONG).show())
                    .addOnFailureListener(e -> Toast.makeText(EmployerSignIn.this, "An error has occurred!", Toast.LENGTH_LONG).show());
        }).setNegativeButton("Cancel", (dialog, which) -> {});
        resetDialog.create().show();
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