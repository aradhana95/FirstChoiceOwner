package com.example.abhatripathi.serverappfoodcubo;

import android.Manifest;
import android.accounts.Account;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.abhatripathi.serverappfoodcubo.Common.Common;
import com.example.abhatripathi.serverappfoodcubo.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SecondActivity extends AppCompatActivity {
    Button btnSignin;
    TextView slogan;
    // Firebase
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_screen);
        btnSignin=(Button)findViewById(R.id.btnSignin);
        slogan=findViewById(R.id.slogan);

        // Typeface face= Typeface.createFromAsset(getAssets(),"fonts/NABILA.TTF");
       btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signin=new Intent(SecondActivity.this,SignIn.class);
                startActivity(signin);
            }
        });




    }
}
