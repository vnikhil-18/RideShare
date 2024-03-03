package com.example.rideshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileInfo extends AppCompatActivity {
    private EditText dob;
    private EditText vehicletype;
    private EditText vehiclenumber;
    private Button saving;
    private EditText dln;
    private FirebaseAuth auth;
    private FirebaseFirestore fstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);
        //initializing the variables
        dob=findViewById(R.id.dob);
        vehicletype=findViewById(R.id.vehicletype);
        vehiclenumber=findViewById(R.id.vehiclenumber);
        saving=findViewById(R.id.saving);
        dln=findViewById(R.id.dln);
        auth= FirebaseAuth.getInstance();
        fstore= FirebaseFirestore.getInstance();
        //when user is using the app for the first time and want to share his ride these fields are updated in Firebase Firestore
        DocumentReference documentReference= fstore.collection("users").document(auth.getCurrentUser().getUid());
        saving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //we ask him to update DateOfBirth, VehicleType, VehicleNumber, DrivingLicenseNumber for security Purposes
                String userdob= dob.getText().toString();
                String usertype= vehicletype.getText().toString();
                String usernumber= vehiclenumber.getText().toString();
                String userdln= dln.getText().toString();
                if(userdob.isEmpty() || usertype.isEmpty() || usernumber.isEmpty() || userdln.isEmpty()){
                    Toast.makeText(ProfileInfo.this, "Please enter all the details", Toast.LENGTH_SHORT).show();
                }
                //these are updated in Firebase Firestore
                documentReference.update("dob",userdob);
                documentReference.update("vehicletype",usertype);
                documentReference.update("vehiclenumber",usernumber);
                documentReference.update("dlno",userdln);
                //after updating the fields the user is redirected to OptionsActivity .
                startActivity(new Intent(ProfileInfo.this,OptionsActivity.class));
                finish();
            }
        });
    }
}