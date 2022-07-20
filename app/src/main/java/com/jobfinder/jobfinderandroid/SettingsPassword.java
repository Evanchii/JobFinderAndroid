package com.jobfinder.jobfinderandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsPassword extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextInputEditText oldPassword;
    private TextInputEditText newPassword;
    private TextInputEditText conPassword;
    private Button changePassword;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Change Password");
        setContentView(R.layout.activity_settings_password);

        mAuth = FirebaseAuth.getInstance();
        oldPassword = (TextInputEditText) findViewById(R.id.settings_inputCurrPassw);
        newPassword = (TextInputEditText) findViewById(R.id.settings_inputNewPass);
        conPassword = (TextInputEditText) findViewById(R.id.settings_inputConfPass);

    }
    public void _chagePass(View view){

        boolean validate = true;
        TextInputEditText[] txt = new TextInputEditText[]{oldPassword, newPassword, conPassword};

        for(int x = 0; x<3; x++) {
            if(txt[x].getText().toString().isEmpty()) {
//                    input Error Toast
                validate = false;
            }
        }
//        Log.d("validate", "validate "+validate);
        if(validate){
            if(newPassword.getText().toString().trim().equals(conPassword.getText().toString().trim())){
                if(!oldPassword.getText().toString().trim().equals(newPassword.getText().toString().trim())){
                    mAuth.signInWithEmailAndPassword(mAuth.getCurrentUser().getEmail(),oldPassword.getText().toString().trim()).addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    mAuth.getCurrentUser().updatePassword(newPassword.getText().toString().trim());
                                    Toast.makeText(SettingsPassword.this, "Changed Password!", Toast.LENGTH_LONG).show();

                                    startActivity(new Intent(SettingsPassword.this, ApplicantSignIn.class));
                                    finish();
                                }
                            }
                    );
                }else {
                    Log.d("Password", "Old and new Password are equals");
                }
            }else{
                Log.d("Password", "Password not Equal");
            }
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