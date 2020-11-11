package com.eschoolproject.quattus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputEditText Status;
    private Button Button;
    private DatabaseReference databaseReference;
    private FirebaseUser current_user;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        toolbar = findViewById(R.id.status_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.status_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String status_value = getIntent().getStringExtra(getResources().getString(R.string.status_value));
        current_user = FirebaseAuth.getInstance().getCurrentUser();

        String uid = current_user.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.database_users)).child(uid);

        Status = findViewById(R.id.status_input);
        Status.setText(status_value);
        Button = findViewById(R.id.status_update_btn);

        Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                progressDialog = new ProgressDialog(StatusActivity.this);
                progressDialog.setTitle(getResources().getString(R.string.pro_diag_status_change));
                progressDialog.show();
                String status = Status.getText().toString();

                databaseReference.child(getResources().getString(R.string.database_status)).setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            progressDialog.dismiss();
                        } else {

                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.status_change_error_toast),Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

    }
}
