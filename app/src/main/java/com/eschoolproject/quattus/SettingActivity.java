package com.eschoolproject.quattus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.internal.TaskUtil;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


public class SettingActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseUser current_user;

    private CircleImageView imageView;
    private TextView Username ,Status;
    private Toolbar toolbar;
    private Button Name,status,Image;
    public static final int GALLERY_PICK =1;
    private StorageReference storage;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        toolbar = findViewById(R.id.setting_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.setting_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        storage = FirebaseStorage.getInstance().getReference();
        imageView = findViewById(R.id.profile_image);
        Username = findViewById(R.id.account_username);
        Status = findViewById(R.id.account_status);
        Image = findViewById(R.id.account_change_image);
        Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent gallaryIntent = new Intent();
                gallaryIntent.setType(getResources().getString(R.string.image));
                gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallaryIntent,getResources().getString(R.string.select_image)),GALLERY_PICK);



              /* CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingActivity.this); */
            }
        });
        status = findViewById(R.id.account_change_status);
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sstatus = Status.getText().toString();
                Intent statusIntent = new Intent(SettingActivity.this,StatusActivity.class);
                statusIntent.putExtra(getResources().getString(R.string.status_value),sstatus);
                startActivity(statusIntent);
            }
        });


        current_user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = current_user.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.database_users)).child(uid);
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


           String username = dataSnapshot.child(getResources().getString(R.string.database_username)).getValue().toString();
           String status =  dataSnapshot.child(getResources().getString(R.string.database_status)).getValue().toString();
           final String image = dataSnapshot.child(getResources().getString(R.string.database_image)).getValue().toString();
           String thumb_image = dataSnapshot.child(getResources().getString(R.string.database_image_thmb)).getValue().toString();

           Username.setText(username);
           Status.setText(status);

           if(!image.equals(getResources().getString(R.string.database_image_value))){


               Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.mipmap.ic_launcher).into(imageView, new Callback() {
                   @Override
                   public void onSuccess() {

                   }

                   @Override
                   public void onError(Exception e) {

                       Picasso.get().load(image).placeholder(R.mipmap.ic_launcher).into(imageView);
                   }
               });
           }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            Uri image_uri = data.getData();


            CropImage.activity(image_uri)
                    .setAspectRatio(1,1)
                    .start(this);

            //  Toast.makeText(SettingActivity.this,image_uri,Toast.LENGTH_LONG).show();

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                progressDialog = new ProgressDialog(SettingActivity.this);
                progressDialog.setTitle(R.string.uploading_image);
                progressDialog.setMessage(getResources().getString(R.string.uploading_image_wait));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                Uri resultUri = result.getUri();

                final File thumb_filepath =  new File(resultUri.getPath());



                String current_user_id = current_user.getUid();
                Bitmap thumb_bitmap = null;

                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(25)
                            .compressToBitmap(thumb_filepath);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();




                final StorageReference filepath = storage.child(getResources().getString(R.string.storage_path)).child(current_user_id + ".jpeg");
                final StorageReference thumbs_filepath = storage.child(getResources().getString(R.string.storage_path)).child(getResources().getString(R.string.storage_path_thumb)).child(current_user_id + ".jpeg");


                final Task uploadTask = filepath.putFile(resultUri);

               /* final Task uploadTask = filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {


                        if(task.isSuccessful()){

                            Toast.makeText(SettingActivity.this, "task complete", Toast.LENGTH_LONG).show();
                            final String download_url = task.getResult().getMetadata().getReference().getDownloadUrl().getResult().toString();


                            UploadTask uploadTask = thumbs_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                    String  thumb_donload_url = task.getResult().getMetadata().getReference().getDownloadUrl().getResult().toString();
                                    if(task.isSuccessful()){

                                        HashMap<String,String> upload_hashMap = new HashMap<>();
                                        upload_hashMap.put(getResources().getString(R.string.database_image),download_url);
                                        upload_hashMap.put(getResources().getString(R.string.database_image_thmb),thumb_donload_url);

                                        databaseReference.setValue(upload_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){

                                                    progressDialog.dismiss();
                                                    Toast.makeText(SettingActivity.this, "upload success", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {

                                        progressDialog.dismiss();
                                        Toast.makeText(SettingActivity.this, "Error uploading", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });



                        }
                    }
                }); */


                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull final Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();

                             final String download_url = downloadUri.toString();
                             UploadTask uploadtask = thumbs_filepath.putBytes(thumb_byte);

                            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot , Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if(!task.isSuccessful()){
                                        throw task.getException();
                                    }
                                    return thumbs_filepath.getDownloadUrl();

                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if(task.isSuccessful()){

                                        Uri downloadUri = task.getResult();


                                        final String thumb_download_url = downloadUri.toString();
                                        Toast.makeText(SettingActivity.this, thumb_download_url, Toast.LENGTH_SHORT).show();

                                        Map update_hashMap = new HashMap<>();
                                        update_hashMap.put(getResources().getString(R.string.database_image),download_url);
                                        update_hashMap.put(getResources().getString(R.string.database_image_thmb),thumb_download_url);
                                        databaseReference.updateChildren(update_hashMap);

                                        if (downloadUri == null)
                                            return ;
                                    }

                                }
                            });
                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });


                progressDialog.dismiss();
                Toast.makeText(SettingActivity.this,"Wooow",Toast.LENGTH_LONG).show();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }



}
