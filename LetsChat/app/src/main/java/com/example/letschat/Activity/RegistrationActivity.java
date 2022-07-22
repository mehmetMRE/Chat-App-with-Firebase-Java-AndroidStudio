package com.example.letschat.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.letschat.ModelClass.Users;
import com.example.letschat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {
    TextView txt_signin,kayıtol;
     CircleImageView profile_image;
     EditText reg_name,reg_email,reg_password,reg_cpassword;
     FirebaseAuth auth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Uri imageUri;
    FirebaseDatabase database;
    FirebaseStorage storage;
    String imageURI;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Lütfen bekleyiniz..");
        progressDialog.setCancelable(false);
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();

     txt_signin=findViewById(R.id.txt_signin);
     profile_image=findViewById(R.id.profile_image);
     reg_cpassword=findViewById(R.id.reg_cpassword);
     reg_password=findViewById(R.id.reg_password);
     reg_email=findViewById(R.id.reg_email);
     reg_name=findViewById(R.id.reg_name);
     kayıtol=findViewById(R.id.kayıtol);

     kayıtol.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             progressDialog.show();
             String email=reg_email.getText().toString();
             String name=reg_name.getText().toString();
             String password=reg_password.getText().toString();
             String cPassword=reg_cpassword.getText().toString();
             String status="Merhaba ben Let's Chat Kullanıyorum";

             if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)|| TextUtils.isEmpty(cPassword)){
                 Toast.makeText(RegistrationActivity.this, "Geçersiz Veri", Toast.LENGTH_SHORT).show();
                 progressDialog.dismiss();
             }
             else if (!email.matches(emailPattern)){
                 reg_email.setError("Geçersiz Email");
                 Toast.makeText(RegistrationActivity.this, "Geçersiz Email", Toast.LENGTH_SHORT).show();
                 progressDialog.dismiss();
             }
             else if (!password.equals(cPassword)){
                 Toast.makeText(RegistrationActivity.this, "Eşleşmeyen Şifre", Toast.LENGTH_SHORT).show();
                 progressDialog.dismiss();
             }
             else if (password.length()>6){
                 Toast.makeText(RegistrationActivity.this, "Şifreyi 6 karakter giriniz", Toast.LENGTH_SHORT).show();
                 progressDialog.dismiss();
             }
         else {

                 auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                         if (task.isSuccessful()) {

                             DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
                             StorageReference storageReference = storage.getReference().child("uplod").child(auth.getUid());
                             //  Toast.makeText(RegistrationActivity.this, "Kullanıcı Oluşturuldu", Toast.LENGTH_SHORT).show();


                             if (imageUri != null) {
                                 storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                     @Override
                                     public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                         if (task.isSuccessful()) {
                                             storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                 @Override
                                                 public void onSuccess(Uri uri) {
                                                     imageURI = uri.toString();
                                                     Users users = new Users(auth.getUid(), name, email, imageURI,status);
                                                     reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                         @Override
                                                         public void onComplete(@NonNull Task<Void> task) {
                                                             if (task.isSuccessful()) {
                                                                 progressDialog.dismiss();
                                                                 startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));
                                                             } else {
                                                                 Toast.makeText(RegistrationActivity.this, "kullanıcı oluşturulamadı", Toast.LENGTH_SHORT).show();
                                                             }
                                                         }
                                                     });
                                                 }
                                             });
                                         }
                                     }

                                 });
                             } else {
                                 String status="Merhaba ben Let's Chat Kullanıyorum";

                                 imageURI = "https://firebasestorage.googleapis.com/v0/b/letschat-a2fc7.appspot.com/o/userrr.png?alt=media&token=6e7354d3-c01b-4f35-96ac-23391cbc3f67";
                                 Users users = new Users(auth.getUid(), name, email, imageURI,status);
                                 reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                     @Override
                                     public void onComplete(@NonNull Task<Void> task) {
                                         if (task.isSuccessful()) {
                                             startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));
                                         } else {
                                             Toast.makeText(RegistrationActivity.this, "Yeni kullanıcı oluşturulamadı", Toast.LENGTH_SHORT).show();
                                         }
                                     }
                                 });
                             }
                         }
                         else{
                             progressDialog.dismiss();
                             Toast.makeText(RegistrationActivity.this, "Birşeyler yanlış giti", Toast.LENGTH_SHORT).show();

                         }
                     }
                 });
             }
         }
     });
     profile_image.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             Intent intent = new Intent();
             intent.setType("image/*");
             intent.setAction(Intent.ACTION_GET_CONTENT);
             startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
         }
     });
     txt_signin.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
         }
     });


    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==10){
            if (data!=null){
                imageUri=data.getData();
                profile_image.setImageURI(imageUri);
            }
        }
    }
}
