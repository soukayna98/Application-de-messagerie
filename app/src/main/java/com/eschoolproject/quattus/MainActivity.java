package com.eschoolproject.quattus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private SectionsPagerAdapter section_pager_adapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String Request = getResources().getString(R.string.tab_request);
        String Chat = getResources().getString(R.string.tab_chat);
        String Friends = getResources().getString(R.string.tab_friends);
        mAuth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Quattus");

        viewPager = findViewById(R.id.main_pager);
        //section_pager_adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        section_pager_adapter = new SectionsPagerAdapter(getSupportFragmentManager(),Request,Chat,Friends);


        viewPager.setAdapter(section_pager_adapter);

            tabLayout =  findViewById(R.id.main_tab);

               tabLayout.setupWithViewPager(viewPager);

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null) {
           send_to_start();
        }
    }

    private void send_to_start() {

        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         getMenuInflater().inflate(R.menu.main_menu,menu);

        return  true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);


        if(item.getItemId() == R.id.main_menu_account_setting){

            send_to_setting();

        }


        if(item.getItemId() == R.id.main_menu_logout){

            FirebaseAuth.getInstance().signOut();
            send_to_start();

        }


        if(item.getItemId() == R.id.main_menu_all_users){

            send_to_users();

        }
      /* switch (item.getItemId()){

           case   R.id.main_menu_logout :  {
               FirebaseAuth.getInstance().signOut();
               send_to_start();
           }
           case   R.id.main_menu_account_setting :  send_to_setting();
           case   R.id.main_menu_all_users : send_to_users();
       }*/




        return  true;
    }

    private void send_to_users() {

        Intent users_Intent = new Intent(MainActivity.this,UsersActivity.class);
        startActivity(users_Intent);



    }

    private void send_to_setting() {

        Intent setting_Intent = new Intent(MainActivity.this,SettingActivity.class);
        startActivity(setting_Intent);



    }
}
