package com.example.rideshare;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ProfilePage extends AppCompatActivity {

    private EditText name, email, pno, dob, vehicletype, vehiclenumber, aadhar, dln;
    private Button button;
    private FirebaseAuth auth;
    private FirebaseFirestore fstore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        //initializing the variables
        auth= FirebaseAuth.getInstance();
        fstore= FirebaseFirestore.getInstance();
        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        pno=findViewById(R.id.pno);
        dob=findViewById(R.id.dob);
        vehicletype=findViewById(R.id.vehicletype);
        vehiclenumber=findViewById(R.id.vehiclenumber);
        aadhar=findViewById(R.id.aadhaar);
        dln=findViewById(R.id.dln);
        button=findViewById(R.id.button);
        String userId= auth.getCurrentUser().getUid();


        DocumentReference documentReference= fstore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                //when the user clicks on the profile button the editText Fields gets data from FireBase FireStore
                name.setText(documentSnapshot.getString("name"));
                email.setText(documentSnapshot.getString("email"));
                pno.setText(documentSnapshot.getString("pno"));
                dob.setText(documentSnapshot.getString("dob"));
                vehicletype.setText(documentSnapshot.getString("vehicletype"));
                vehiclenumber.setText(documentSnapshot.getString("vehiclenumber"));
                aadhar.setText(documentSnapshot.getString("aadhaarnumber"));
                dln.setText(documentSnapshot.getString("dlno"));
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //when the user clicks on the update button the data available in every editText field is updated in the FireBase FireStore
                documentReference.update("name",name.getText().toString());
                documentReference.update("email",email.getText().toString());
                documentReference.update("pno",pno.getText().toString());
                documentReference.update("dob",dob.getText().toString());
                documentReference.update("vehicletype",vehicletype.getText().toString());
                documentReference.update("vehiclenumber",vehiclenumber.getText().toString());
                documentReference.update("aadhaarnumber",aadhar.getText().toString());
                documentReference.update("dlno",dln.getText().toString());
                //After updating the data the user is redirected to the HomePage
                startActivity(new Intent(ProfilePage.this, HomePage.class));
            }
        });

    }
}