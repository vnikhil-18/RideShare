package com.example.rideshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.ktx.Firebase;

public class Travel_Details extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore fstore;
    private TextView traveler, from1, to1, phone1, from2, to2, phone2;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_details);
        //intiiating the variables
        auth= FirebaseAuth.getInstance();
        fstore= FirebaseFirestore.getInstance();
        traveler= findViewById(R.id.traveler);
        from1= findViewById(R.id.from1);
        to1= findViewById(R.id.to1);
        phone1= findViewById(R.id.phone1);
        from2= findViewById(R.id.from2);
        to2= findViewById(R.id.to2);
        phone2= findViewById(R.id.phone2);
        button= findViewById(R.id.endride);
        String userId= auth.getCurrentUser().getUid();
        DocumentReference documentReference= fstore.collection("users").document(userId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@NonNull DocumentSnapshot snapshot, @NonNull FirebaseFirestoreException error) {
                //updating these fields in Firebase Firestore
                if (snapshot != null && snapshot.exists()) {
                    String temp1 = snapshot.getString("isdriver");
                    if (temp1 != null && temp1.equals("true")) {
                        traveler.setText("Travelling as : Driver");
                    } else {
                        traveler.setText("Travelling as : Passenger");
                    }
                    from1.setText(snapshot.getString("from1"));
                    to1.setText(snapshot.getString("to1"));
                    phone1.setText(snapshot.getString("email"));
                    phone2.setText(snapshot.getString("costumerMail"));
                    from2.setText(snapshot.getString("from2"));
                    to2.setText(snapshot.getString("to2"));
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Removing the data from Firebase Firestore
                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@NonNull DocumentSnapshot snapshot, @NonNull FirebaseFirestoreException error) {
                        if (snapshot != null && snapshot.exists()) {
                            documentReference.update("isdriver", "false");
                            documentReference.update("from1", "");
                            documentReference.update("to1", "");
                            documentReference.update("costumerMail", "");
                            documentReference.update("from2", "");
                            documentReference.update("to2", "");
                            from1.setText("");
                            to1.setText("");
                            phone1.setText("");
                            phone2.setText("");
                            from2.setText("");
                            to2.setText("");
                            traveler.setText("");
                        }
                    }
                });
            }
        });
    }
}
