package com.example.varunkumar.ownerfirstchoice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
