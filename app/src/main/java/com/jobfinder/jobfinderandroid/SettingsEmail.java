package com.jobfinder.jobfinderandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsEmail extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextInputEditText currEmail;
    private TextInputEditText newEmail;
    private TextInputEditText password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Change Email");
        setContentView(R.layout.activity_settings_email);

        mAuth = FirebaseAuth.getInstance();
        currEmail = (TextInputEditText) findViewById(R.id.settings_inputCurrEmail);
        newEmail = (TextInputEditText) findViewById(R.id.settings_inputNewEmail);

        password =(TextInputEditText) findViewById(R.id.settings_inputPass);


    }

    public void _changeEmail(View view){
        Log.d("email","Email"+currEmail.getText().toString().trim() + newEmail.getText().toString().trim());
        if(!currEmail.getText().toString().trim().isEmpty() && !newEmail.getText().toString().trim().isEmpty() && !password.getText().toString().trim().isEmpty()){
            if(!currEmail.getText().toString().trim().equals(newEmail.getText().toString().trim())){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                AuthCredential credential = EmailAuthProvider.getCredential(currEmail.getText().toString().trim(),password.getText().toString().trim());
                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    Log.d("Email", "User re-authenticated.");
                    FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                    user1.updateEmail(newEmail.getText().toString().trim())
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Log.d("Email", "User email address updated.");
                                    Toast.makeText(SettingsEmail.this, "User Email Address Updated.", Toast.LENGTH_SHORT);

                                    new CommonFunctions().createLog(SettingsEmail.this, "Email Changed", mAuth.getUid() + " has changed their own email address.",
                                            "User Management", "", mAuth.getUid());

                                    mAuth.signOut();
                                    startActivity(new Intent(SettingsEmail.this, ApplicantSignIn.class));
                                }
                            });
                });
            }else {
                Log.d("Email", "old Email and new Email cant be equal");
                Toast.makeText(SettingsEmail.this, "Old Email and New Email is Equal", Toast.LENGTH_SHORT);
            }
        }else {
            Log.d("Email", "old Email and new Email is Required");
            Toast.makeText(SettingsEmail.this, "Old Email and new Email is Required", Toast.LENGTH_SHORT);
        }

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}