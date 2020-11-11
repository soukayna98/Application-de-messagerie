package com.eschoolproject.quattus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private RecyclerView users_list;
    private FirebaseRecyclerAdapter<User,UserViewHolder> firebaseRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        toolbar = findViewById(R.id.users_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.users_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        users_list = findViewById(R.id.users_list);
        users_list.setHasFixedSize(true);
        users_list.setLayoutManager(new LinearLayoutManager(this));


        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child(getResources().getString(R.string.database_users));




        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(query, User.class)
                        .build();

         firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {

             @Override
            protected void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int position, @NonNull User user) {

                userViewHolder.setUserame(user.getUsername());
                userViewHolder.setStatus(user.getStatus());
                //userViewHolder.setImage(user.getImage());
                userViewHolder.setUser_Image(user.getImage_Thmb());
                final String user_id = getRef(position).getKey() ;

                userViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        Intent profileIntent = new Intent(UsersActivity.this,ProfileActivity.class);
                        profileIntent.putExtra(getResources().getString(R.string.user_id),user_id);
                        startActivity(profileIntent);
                    }
                });


             }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout, parent, false);

                return new UserViewHolder(view);
            }
        };

         users_list.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    public  class  UserViewHolder extends RecyclerView.ViewHolder{

        View view;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView;

        }

        public void setUserame(String name) {

            TextView Name =  view.findViewById(R.id.user_single_username);
            Name.setText(name);

        }


        public void setStatus(String status) {

            TextView Status= view.findViewById((R.id.user_single_status));
            Status.setText(status);
        }

        public void setImage(String image) {

            CircleImageView Image = view.findViewById(R.id.user_single_image);
            Picasso.get().load(image).into(Image);

        }

        public void setUser_Image(String image  ) {

            CircleImageView Image = view.findViewById(R.id.user_single_image);
            Picasso.get().load(image).placeholder(R.mipmap.ic_launcher).into(Image);

        }
    }
}
