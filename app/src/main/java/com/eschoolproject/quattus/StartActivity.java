package com.eschoolproject.quattus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    private Button Sign_In,Sign_Up ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Sign_Up = findViewById(R.id.register_sign_up_btn);
        Sign_Up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sign_up_intent  = new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(sign_up_intent);


            }
        });

        Sign_In = findViewById(R.id.register_sign_in_btn);
        Sign_In.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sign_up_intent  = new Intent(StartActivity.this,LoginActivity.class);
                startActivity(sign_up_intent);


            }
        });

    }

}
