package com.eschoolproject.quattus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.io.Console;
import java.util.HashMap;
import java.util.concurrent.Delayed;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText Username, Email, Password;
    private Button Create;
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;
    private DatabaseReference database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog = new ProgressDialog(this);


       mAuth = FirebaseAuth.getInstance();
       toolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.create_account);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



           Username =  findViewById(R.id.register_username);
           Email =   findViewById(R.id.register_email);
           Password =  findViewById(R.id.register_password);
           Create = findViewById(R.id.register_btn);


        Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = Username.getText().toString();
                String email = Email.getText().toString();
                String password = Password.getText().toString();

                if(! TextUtils.isEmpty(username) || ! TextUtils.isEmpty(email) || ! TextUtils.isEmpty(password) )
                {
                    progressDialog.setTitle(R.string.pro_diag_registration);
                    progressDialog.setMessage(getResources().getString(R.string.pro_diag_registration_message));
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    register_user(username,email,password);

                }
            }
        });
    }

    private void register_user(final String username, String email, String password) {

      mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {

              if(task.isSuccessful()){
                  FirebaseUser current_user;


                     current_user = mAuth.getCurrentUser();

                  String uid = current_user.getUid();

                  database = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.database_users)).child(uid);

                  progressDialog.setMessage(getResources().getString(R.string.pro_diag_dbadd_message));
                  HashMap<String,String> userMap = new HashMap<>();
                  userMap.put(getResources().getString(R.string.database_username),username);
                  userMap.put(getResources().getString(R.string.database_status),getResources().getString(R.string.database_status_value));
                  userMap.put(getResources().getString(R.string.database_image),getResources().getString(R.string.database_image_value));
                  userMap.put(getResources().getString(R.string.database_image_thmb),getResources().getString(R.string.database_image_thmb_value));


                  database.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {


                          if(task.isSuccessful()) {

                              progressDialog.dismiss();
                              Toast.makeText(RegisterActivity.this,R.string.register_success_toast,Toast.LENGTH_LONG).show();
                              Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                              startActivity(mainIntent);
                              finish();
                          }

                      }
                  });




              } else {

                  progressDialog.hide();
                  Toast.makeText(RegisterActivity.this,R.string.registration_error_toast,Toast.LENGTH_LONG).show();
              }
          }
      });

    }
}
