package com.example.letschat.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letschat.Adapter.UserAdapter;
import com.example.letschat.ModelClass.Users;
import com.example.letschat.R;
import com.example.letschat.SettingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
FirebaseAuth auth;
RecyclerView mainUserRecyclerView;
UserAdapter adapter;
FirebaseDatabase database;
ArrayList<Users> usersArrayList;
ImageView imgLogout;
ImageView imgSetting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        usersArrayList=new ArrayList<>();
        DatabaseReference reference=database.getReference().child("user");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Users users=dataSnapshot.getValue(Users.class);
                    usersArrayList.add(users);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
       imgLogout=findViewById(R.id.img_logOut);
       imgSetting=findViewById(R.id.img_setting);
        mainUserRecyclerView=findViewById(R.id.mainUserRecyclerView);
        mainUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new UserAdapter(HomeActivity.this,usersArrayList);
        mainUserRecyclerView.setAdapter(adapter);



        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog=new Dialog(HomeActivity.this,R.style.Dialoge);

                dialog.setContentView(R.layout.dialog_layout);
                TextView noBtn,yesBtn;
                yesBtn=dialog.findViewById(R.id.yesBtn);
                noBtn=dialog.findViewById(R.id.noBtn);

                yesBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(HomeActivity.this,RegistrationActivity.class));
                    }
                });
                noBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });







                dialog.show();
            }
        });
        imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, SettingActivity.class));
            }
        });


    }

}