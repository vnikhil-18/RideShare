package com.example.rideshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {
    TextView wel,learning;
    private static int Splash_timeout=2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wel=findViewById(R.id.textview1);
        learning=findViewById(R.id.textview2);
        String s= FirebaseAuth.getInstance().getUid();
        //Splash Screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(s!=null)
                {
                    //if the user is already logged in he is redirected to OptionsActivity
                    Intent intent =new Intent(MainActivity.this,OptionsActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    //if the user is not logged in he is redirected to Login Activity
                    Intent splashintent=new Intent(MainActivity.this, Login.class);
                    startActivity(splashintent);
                    finish();
                }
            }
        },Splash_timeout);
        Animation myanimation= AnimationUtils.loadAnimation(MainActivity.this,R.anim.animat2);
        wel.startAnimation(myanimation);
        Animation myanimation2= AnimationUtils.loadAnimation(MainActivity.this,R.anim.animat1);
        learning.startAnimation(myanimation2);
    }
}
