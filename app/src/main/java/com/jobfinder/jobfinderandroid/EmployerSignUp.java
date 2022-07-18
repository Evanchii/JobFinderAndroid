package com.jobfinder.jobfinderandroid;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class EmployerSignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_sign_up);

        mAuth = FirebaseAuth.getInstance();
    }

    public void _login(View view){

    }

}