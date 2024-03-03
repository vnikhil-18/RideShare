package com.example.rideshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUpFragment extends Fragment {
    private EditText name;
    private EditText email;
    private EditText password;
    private EditText pno;
    private EditText aadhaarnumber;
    private Button signup;
    private FirebaseAuth auth;
    private FirebaseFirestore fstore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //initialize the below variables
        View view = inflater.inflate(R.layout.fragment_signup_tab, container, false);
        signup = view.findViewById(R.id.signup);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        pno = view.findViewById(R.id.pno);
        aadhaarnumber = view.findViewById(R.id.aadhaarnumber);
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //when Signup button is clicked these fields are updated in Firebase Firestore
                String username = name.getText().toString();
                String useremail = email.getText().toString();
                String userpassword = password.getText().toString();
                String userpno = pno.getText().toString();
                String useraadhaarnumber = aadhaarnumber.getText().toString();
                //Ensuring every Field is non-Empty
                if (username.isEmpty() || useremail.isEmpty() || userpassword.isEmpty()) {
                    Toast.makeText(getActivity(), "Please Enter All Details", Toast.LENGTH_SHORT).show();
                }
                //Ensuring Password is of minimum 6 characters
                if(userpassword.length()<6) Toast.makeText(getActivity(), "Password should have minimum 6 characters", Toast.LENGTH_SHORT).show();
                else {
                    auth.createUserWithEmailAndPassword(useremail, userpassword).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        //When the user is successfully created we are storing the data in Firebase Firestore
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Account Created", Toast.LENGTH_SHORT).show();
                                HashMap<String, Object> user = new HashMap<>();
                                String userId = auth.getCurrentUser().getUid();
                                DocumentReference documentReference = fstore.collection("users").document(userId);
                                user.put("name", username);
                                user.put("email", useremail);
                                user.put("password", userpassword);
                                user.put("pno", userpno);
                                user.put("aadhaarnumber", useraadhaarnumber);
                                user.put("dob", "");
                                user.put("vehicletype", "");
                                user.put("vehiclenumber", "");
                                user.put("dlno", "");
                                user.put("costumerMail", "");
                                user.put("from1", "");
                                user.put("to1", "");
                                user.put("from2", "");
                                user.put("to2", "");
                                user.put("isdriver", "false");
                                user.put("isrider", "false");

                                documentReference.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //if task is successfull we are starting the OptionsActivity
                                            // allowing user to choose whether he is a driver or a passenger
                                            startActivity(new Intent(getActivity(), OptionsActivity.class));
                                            getActivity().finish();
                                        } else {
                                            Toast.makeText(getActivity(), "Data Storage Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                //if task is not successfull we are displaying a toast message
                                Toast.makeText(getActivity(), "SignUp Failed. Please Try Again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        return view;
    }
}
