package com.example.rideshare;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.FirebaseDatabase;

public class LoginFragment extends Fragment {
    private static final String PREFS_NAME = "MyPrefsFile";
    private EditText username;
    private EditText password;
    private Button signin;
    private FirebaseAuth auth;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_tab, container, false);
        //initialize the below variables
        signin = view.findViewById(R.id.signin);
        username = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        auth = FirebaseAuth.getInstance();

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String useremail = username.getText().toString();
                String userpassword = password.getText().toString();
                //Ensuring every email  and Password is non-Empty
                if (useremail.isEmpty()) {
                    username.setError("Please Enter Email");
                    username.requestFocus();
                    return;
                }
                if (userpassword.isEmpty()) {
                    password.setError("Please Enter Password");
                    password.requestFocus();
                    return;
                }
                //Signing in the user with the help of Firebase Authentication
                auth.signInWithEmailAndPassword(useremail, userpassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //if the user is successfully logged in he is redirected to OptionsActivity
                        startActivity(new Intent(requireContext(), OptionsActivity.class));
                        requireActivity().finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    //if the user is not successfully logged in he is shown a Toast Of respective Possible error
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthInvalidUserException) {
                            Toast.makeText(requireContext(), "Account not found. Please sign up.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Sign In Failed. Please Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return view;
    }
}
