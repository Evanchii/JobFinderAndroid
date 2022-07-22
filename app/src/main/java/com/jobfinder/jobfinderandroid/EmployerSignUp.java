package com.jobfinder.jobfinderandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class EmployerSignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase dbRef;
    private String user;

    private TextInputEditText email;
    private TextInputEditText password;
    private TextInputEditText conPass;

    private TextInputEditText fname;
    private TextInputEditText lname;
    private Spinner gender;
    private TextInputEditText phone;
    private TextInputEditText birthday;
    private TextInputEditText address;
    private TextInputEditText company;
    private TextInputEditText description;
    private TextInputEditText website;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_sign_up);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance();


        email = (TextInputEditText) findViewById(R.id.empSignUp_inputEmail);
        password = (TextInputEditText) findViewById(R.id.empSignUp_inputPass);
        conPass = (TextInputEditText) findViewById(R.id.empSignUp_inputConPass);

        fname = (TextInputEditText) findViewById(R.id.empSignUp_inputName);
        lname = (TextInputEditText) findViewById(R.id.empSignUp_inputLName);
        gender = (Spinner) findViewById(R.id.empSignUp_spnGender);
        phone = (TextInputEditText) findViewById(R.id.empSignUp_inputContact);
        birthday = (TextInputEditText) findViewById(R.id.empSignUp_inputBirthday);
        address = (TextInputEditText)findViewById(R.id.empSignUp_inputAddress);
        company = (TextInputEditText) findViewById(R.id.empSignUp_inputCompany);
        description = (TextInputEditText) findViewById(R.id.empSignUp_inputDescription);
        website = (TextInputEditText) findViewById(R.id.empSignUp_inputWeb);

    }

    public void _empSignUp(View view){
        boolean validate = true;
        TextInputEditText[] txt = new TextInputEditText[]{fname,lname,phone,birthday,company,description,website,email,password,conPass};

        for(int x = 0; x<10; x++) {
            if(txt[x].getText().toString().isEmpty()) {
//                    input Error Toast
                Toast.makeText(EmployerSignUp.this,"Please Fill "+txt[x],Toast.LENGTH_SHORT).show();
                Log.d("No Data","Data" +txt[x]);
                validate = false;
            }
        }
        if(validate){
            if(password.getText().toString().trim().equals(conPass.getText().toString().trim())){
                mAuth.createUserWithEmailAndPassword(email.getText().toString().trim(),password.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            user = mAuth.getCurrentUser().getUid();
                            Log.d("User ","User "+user);
                            dbRef.getReference().child("user").child("employer").child(user).child("fname").setValue(fname.getText().toString());
                            dbRef.getReference().child("user").child("employer").child(user).child("lname").setValue(lname.getText().toString());
                            dbRef.getReference().child("user").child("employer").child(user).child("gender").setValue(gender.getSelectedItem().toString());
                            dbRef.getReference().child("user").child("employer").child(user).child("phone").setValue(phone.getText().toString());
                            dbRef.getReference().child("user").child("employer").child(user).child("birthdate").setValue(birthday.getText().toString());
                            dbRef.getReference().child("user").child("employer").child(user).child("address").setValue(address.getText().toString());
                            dbRef.getReference().child("user").child("employer").child(user).child("company").setValue(company.getText().toString());
                            dbRef.getReference().child("user").child("employer").child(user).child("description").setValue(description.getText().toString());
                            dbRef.getReference().child("user").child("employer").child(user).child("website").setValue(website.getText().toString());

                            Log.d("Employer","Sign Up");
                            Toast.makeText(EmployerSignUp.this,"Successfully Registered",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EmployerSignUp.this,EmployerSignIn.class));
                        }

                    }
                });
            }
        }

    }


    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        String path = uri.getPath();

                        Log.d("URI"," URI "+uri);
                        Log.d("Path"," Path "+path);
                    }
                }
            }
    );
    public void openFileChooser(View view){
            Intent data = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            data.setType("*/*");
            data = Intent.createChooser(data, "Choose File");
            someActivityResultLauncher.launch(data);

    }
}