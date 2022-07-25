package com.jobfinder.jobfinderandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class EmployerProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActionBarDrawerToggle actionBarDrawerToggle;

    private FirebaseAuth mAuth;
    private FirebaseDatabase dbRef;
    private TextView empName;
    private EditText fname;
    private EditText lname;
    private Spinner gender;
    private EditText birthday;
    private EditText company;
    private EditText description;
    private EditText address;
    private EditText phone;
    private String user;
    private FloatingActionButton btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setSubtitle("employer JobFinder");
        setContentView(R.layout.activity_employer_profile);

        new CommonFunctions().fetchHamburgerDetails((NavigationView) findViewById(R.id.navigation_view), "employer");
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerButton);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(4).setChecked(true);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance();
        user = mAuth.getCurrentUser().getUid();

        empName = (TextView) findViewById(R.id.empProfile_txtName);
        fname = (EditText) findViewById(R.id.empProfile_inputFName);
        lname = (EditText) findViewById(R.id.empProfile_inputLName);
        gender = (Spinner) findViewById(R.id.empProfile_spnGender);
        birthday = (EditText) findViewById(R.id.empProfile_inputBDay);
        company = (EditText) findViewById(R.id.empProfile_inputComp);
        description = (EditText) findViewById(R.id.empProfile_inputDesc);
        address = (EditText) findViewById(R.id.empProfile_inputAddress);
        phone = (EditText) findViewById(R.id.empProfile_inputPhone);
        btnSave = (FloatingActionButton)findViewById(R.id.floatingActionButton);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _btnSave(view);
            }
        });

        dbRef.getReference("user").child("employer").child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                empName.setText(snapshot.child("company").getValue().toString());
                fname.setText(snapshot.child("fname").getValue().toString());
                lname.setText(snapshot.child("lname").getValue().toString());
                birthday.setText(snapshot.child("birthdate").getValue().toString());
                company.setText(snapshot.child("company").getValue().toString());
                description.setText(snapshot.child("description").getValue().toString());
                address.setText(snapshot.child("address").getValue().toString());
                phone.setText(snapshot.child("phone").getValue().toString());

//              For Gender
                ArrayAdapter<CharSequence> adapterGender = ArrayAdapter.createFromResource(EmployerProfile.this,R.array.gender, android.R.layout.simple_spinner_dropdown_item);
                adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                gender.setAdapter(adapterGender);
                if(snapshot.child("gender").getValue().toString() != null){
                    int spinnerPosition = adapterGender.getPosition(snapshot.child("gender").getValue().toString());
                    gender.setSelection(spinnerPosition);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

//    Pushing of Data
    public void _btnSave(View view){

        dbRef.getReference().child("user").child("employer").child(user).child("fname").setValue(fname.getText().toString());
        dbRef.getReference().child("user").child("employer").child(user).child("lname").setValue(lname.getText().toString());
        dbRef.getReference().child("user").child("employer").child(user).child("gender").setValue(gender.getSelectedItem().toString());
        dbRef.getReference().child("user").child("employer").child(user).child("birthdate").setValue(birthday.getText().toString());
        dbRef.getReference().child("user").child("employer").child(user).child("company").setValue(company.getText().toString());
        dbRef.getReference().child("user").child("employer").child(user).child("description").setValue(description.getText().toString());
        dbRef.getReference().child("user").child("employer").child(user).child("address").setValue(address.getText().toString());
        dbRef.getReference().child("user").child("employer").child(user).child("phone").setValue(phone.getText().toString());

        Toast.makeText(view.getContext(), "Profile Update Successfully", Toast.LENGTH_SHORT);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.notification) {
            startActivity(new Intent(this, Notification.class));
        }
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        if (CommonFunctions.employerMenu(this, item, "Profile"))
            finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.notification, menu);
        return super.onCreateOptionsMenu(menu);
    }
}