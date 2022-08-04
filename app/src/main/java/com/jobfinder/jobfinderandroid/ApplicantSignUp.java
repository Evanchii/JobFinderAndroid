package com.jobfinder.jobfinderandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ApplicantSignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase dbRef;
    private String user;

    private EditText email;
    private EditText password;
    private EditText conPass;

    private EditText fname;
    private EditText lname;
    private Spinner gender;
    private Spinner specialization;
    private EditText phone;
    private EditText birthday;
    private EditText address;
    private EditText portfolio;
    private ProgressDialog dialog = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_applicant_sign_up);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance();

        email = (EditText) findViewById(R.id.appSignUp_inputEmail);
        password = (EditText) findViewById(R.id.appSignUp_inputPass);
        conPass = (EditText) findViewById(R.id.appSignUp_inputConfPass);

        fname = (EditText) findViewById(R.id.appSignUp_inputName);
        lname = (EditText) findViewById(R.id.appSignUp_inputLName);
        gender = (Spinner) findViewById(R.id.appSignUp_spnGender);
        birthday = (EditText)findViewById(R.id.appSignUp_inputBirthday);
        specialization = (Spinner) findViewById(R.id.appSignUp_spnSpec);
        phone = (EditText) findViewById(R.id.appSignUp_inputContact);
        address = (EditText) findViewById(R.id.appSignUp_inputAddress);
        portfolio = (EditText) findViewById(R.id.appSignUp_inputPortfolio);


    }
    public void _appSignUp(View view){
        boolean validate = true;
        EditText[] txt = new EditText[]{fname,lname,phone,birthday,address,email,password,conPass};

        for(int x = 0; x<8; x++) {
            if(txt[x].getText().toString().isEmpty()) {
//                    input Error Toast
                Toast.makeText(ApplicantSignUp.this,"Please Fill "+txt[x],Toast.LENGTH_SHORT).show();
                Log.d("No Data","Data" +txt[x]);
                validate = false;
            }
        }
        if(validate){
            if(password.getText().toString().trim().equals(conPass.getText().toString().trim())){

                dialog = ProgressDialog.show(ApplicantSignUp.this, "", "Registering User...", true);

                mAuth.createUserWithEmailAndPassword(email.getText().toString().trim(),password.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            user = mAuth.getUid();
                            Log.d("User ","User "+user);
                            dbRef.getReference().child("user").child("applicant").child(user).child("fname").setValue(fname.getText().toString());
                            dbRef.getReference().child("user").child("applicant").child(user).child("lname").setValue(lname.getText().toString());
                            dbRef.getReference().child("user").child("applicant").child(user).child("gender").setValue(gender.getSelectedItem().toString());
                            dbRef.getReference().child("user").child("applicant").child(user).child("specialization").setValue(specialization.getSelectedItem().toString());
                            dbRef.getReference().child("user").child("applicant").child(user).child("phone").setValue(phone.getText().toString());
                            dbRef.getReference().child("user").child("applicant").child(user).child("birthday").setValue(birthday.getText().toString());
                            dbRef.getReference().child("user").child("applicant").child(user).child("address").setValue(address.getText().toString());
                            dbRef.getReference().child("user").child("applicant").child(user).child("email").setValue(email.getText().toString());
                            dbRef.getReference().child("user").child("applicant").child(user).child("portfolio").setValue(portfolio.getText().toString());

                            Log.d("Employer","Sign Up");

                            dialog.dismiss();

                            Toast.makeText(ApplicantSignUp.this,"Successfully Registered",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ApplicantSignUp.this,EmployerSignIn.class));
                        }

                    }
                });
            }
        }
    }
}