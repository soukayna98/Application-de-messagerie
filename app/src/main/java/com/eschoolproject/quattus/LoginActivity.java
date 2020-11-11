package com.eschoolproject.quattus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputLayout Email,Password;
    private Button Login;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();


       progressDialog = new ProgressDialog(this);


        toolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Email =  findViewById(R.id.login_email);
        Password = findViewById(R.id.login_password);
        Login = findViewById(R.id.login_btn);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = Email.getEditText().getText().toString();
                String password = Password.getEditText().getText().toString();
                if (! TextUtils.isEmpty(email) || ! TextUtils.isEmpty(password)){

                    progressDialog.setTitle(R.string.pro_diag_login);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    login_user(email,password);

                }
            }
        });
    }

    private void login_user(String email, String password) {

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if(task.isSuccessful()) {

                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this,R.string.login_success_toast,Toast.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                } else {

                    progressDialog.hide();
                    Toast.makeText(LoginActivity.this,R.string.login_error_toast,Toast.LENGTH_LONG).show();


                }
                }

        });

    }




}
