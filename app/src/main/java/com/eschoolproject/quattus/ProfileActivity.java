package com.eschoolproject.quattus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {


    private TextView Username,Status,FriendCount;
    private Button Request;
    private ImageView Image;
    private  DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private int current_state ;
    private FirebaseUser current_user;
    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        current_user = FirebaseAuth.getInstance().getCurrentUser();
        current_state = 0;
        final String user_id = getIntent().getStringExtra(getResources().getString(R.string.user_id));
        Username = findViewById(R.id.profile_username);
        Request = findViewById(R.id.friend_request_button);
        Status = findViewById(R.id.profile_status);
        Image = findViewById(R.id.profile_profile_image);
        FriendCount = findViewById(R.id.profile_total_friends);
        databaseReference = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.database_users)).child(user_id);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.pro_diag_load_user_message));
        progressDialog.setTitle(getResources().getString(R.string.pro_diag_user));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String username = dataSnapshot.child(getResources().getString(R.string.database_username)).getValue().toString();
                String status = dataSnapshot.child(getResources().getString(R.string.database_status)).getValue().toString();
                String image = dataSnapshot.child(getResources().getString(R.string.database_image)).getValue().toString();

               // Toast.makeText(ProfileActivity.this, image, Toast.LENGTH_SHORT).show();

                Username.setText(username);
                Status.setText(status);
                try {

                    Picasso.get().load(image).placeholder(R.mipmap.ic_launcher).into(Image);

                } catch (Exception e ) {

                    e.printStackTrace();
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

                reference = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.database_friends_request));
                reference.child(current_user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(user_id)){

                            int request_type = Integer.parseInt(dataSnapshot.child(user_id).child(getResources().getString(R.string.database_request_type)).getValue().toString());
                            Toast.makeText(ProfileActivity.this, "request type == " + request_type, Toast.LENGTH_SHORT).show();
                            if(request_type == 0){

                                Request.setEnabled(true);
                                current_state = 2;
                                Request.setText(getResources().getString(R.string.accept_friend_request));

                            } else if(request_type == 1){

                                Request.setEnabled(true);
                                current_state = 1;
                                Request.setText(getResources().getString(R.string.cancel_friend_request));


                            }

                        } else {


                                DatabaseReference friends = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.database_friends));
                                friends.child(current_user.getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                        if(dataSnapshot.hasChild(user_id)){

                                            current_state = 3;
                                            Request.setText(getResources().getString(R.string.unfriends));
                                        } else{

                                            current_state = 0;
                                            Request.setText(getResources().getString(R.string.send_friend_request));
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                Request.setText(R.string.send_friend_request);
                        }

                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        Request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Request.setEnabled(false);
                    databaseReference = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.database_friends_request));

                if(current_state == 0){

                    databaseReference.child(current_user.getUid()).child(user_id).child(getResources().getString(R.string.database_request_type)).setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                databaseReference.child(user_id).child(current_user.getUid()).child(getResources().getString(R.string.database_request_type)).setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {


                                        Request.setEnabled(true);
                                        current_state = 1;
                                        Request.setText(getResources().getString(R.string.cancel_friend_request));

                                    }
                                });

                            } else {

                                Toast.makeText(ProfileActivity.this, "Failed To Send Friend Request", Toast.LENGTH_LONG).show();
                            }


                        }
                    });

                }

                if(current_state == 1){

                    databaseReference.child(current_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {


                            databaseReference.child(user_id).child(current_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Request.setEnabled(true);
                                    current_state = 0;
                                    Request.setText(getResources().getString(R.string.send_friend_request));

                                }
                            });
                        }
                    });



                }

                if(current_state == 2){

                    final DatabaseReference friends = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.database_friends));

                    final String current_date = DateFormat.getInstance().format(new Date());
                    friends.child(current_user.getUid()).child(user_id).setValue(current_date).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            friends.child(user_id).child(current_user.getUid()).setValue(current_date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    databaseReference.child(current_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {


                                            databaseReference.child(user_id).child(current_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    Request.setEnabled(true);
                                                    current_state = 3;
                                                    databaseReference = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.database_users));
                                                    Request.setText(getResources().getString(R.string.unfriends ));

                                                }
                                            });
                                        }
                                    });


                                }
                            });

                        }
                    });

                }

                if(current_state == 3){
                    final DatabaseReference friends = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.database_friends));
                       friends.child(current_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                             friends.child(user_id).child(current_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void aVoid) {

                                     Request.setEnabled(true);
                                     current_state = 0;
                                     Request.setText(getResources().getString(R.string.send_friend_request));

                                 }
                             });

                           }
                       });

                }
                    Request.setEnabled(true);

            }
        });
    }
}
