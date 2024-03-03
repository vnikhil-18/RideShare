package com.example.rideshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class OptionsActivity extends AppCompatActivity {
    private Button newride;
    private Button shareride;
    private FirebaseAuth auth;
    private FirebaseFirestore fstore;
    private Button logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        //initializing the variables
        newride=findViewById(R.id.newride);
        shareride=findViewById(R.id.shareride);
        logout= findViewById(R.id.opt_logout);
        auth= FirebaseAuth.getInstance();
        fstore= FirebaseFirestore.getInstance();
        //storing the currentUser UID in userId which will be helpful as we need to update data in his document in firestore
        String userId= auth.getCurrentUser().getUid();
        DocumentReference documentReference= fstore.collection("users").document(userId);
        newride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            //updating the fields in Firebase Firestore
                            String dln= documentSnapshot.getString("dlno");
                            String vehiclenumber= documentSnapshot.getString("vehiclenumber");
                            if(!Objects.equals(dln, "") && !Objects.equals(vehiclenumber, "")){
                                //checking if user is using the app for the first time or not
                                documentSnapshot.getReference().update("isdriver","true");
                                documentSnapshot.getReference().update("isrider","false");
                                //Now the user is redirected to HomePage where he can post his ride
                                startActivity(new Intent(OptionsActivity.this, HomePage.class));
                            }
                            else{
                                //if user is using the app for the first time he is redirected
                                // to ProfileInfo Activity to update his details
                                documentSnapshot.getReference().update("isdriver","false");
                                documentSnapshot.getReference().update("isrider","true");
                                startActivity(new Intent(OptionsActivity.this,ProfileInfo.class));
                                finish();
                            }
                        }
                    }
                });

            }
        });
        shareride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //If user wants to share ride with other user he is redirected to CustomerHomePage
                startActivity(new Intent(OptionsActivity.this,CustomerHomePage.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //when user clicks on logout button he is logged out and redirected to Login Activity
                auth.signOut();
                Toast.makeText(OptionsActivity.this, "Logout Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(OptionsActivity.this,Login.class));
                finish();
            }
        });

    }
}