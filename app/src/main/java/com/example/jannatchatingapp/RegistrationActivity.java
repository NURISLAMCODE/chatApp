package com.example.jannatchatingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    TextView sing_in,reg_btn_signUp;
    CircleImageView profile_image;
    EditText reg_name,reg_email,reg_pass,reg_con_pass;
    FirebaseAuth auth;
    Uri imageUri;
    FirebaseDatabase database;
    FirebaseStorage firebaseStorage;
    String  image_uri;
    ProgressDialog progressDialog;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        auth =FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(this);
         progressDialog.setMessage("Please Waiting");
         progressDialog.setCancelable(false);

        sing_in = findViewById(R.id.sing_in);
        profile_image=findViewById(R.id.profile_image);
        reg_name = findViewById(R.id.reg_name);
        reg_email = findViewById(R.id.reg_email);
        reg_pass =findViewById(R.id.reg_pass);
        reg_con_pass = findViewById(R.id.reg_con_pass);
        reg_btn_signUp = findViewById(R.id.reg_btn_singUp);

        reg_btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String name = reg_name.getText().toString();
                String email =reg_email.getText().toString();
                String password =reg_pass.getText().toString();
                String cPassword = reg_con_pass.getText().toString();
                String status ="Hey there I'm Using this Application";

                if(TextUtils.isEmpty(name)||TextUtils.isEmpty(email)|| TextUtils.isEmpty(password)||TextUtils.isEmpty(cPassword)){
                    progressDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this, "Enter Valid Data", Toast.LENGTH_SHORT).show();

                }else if (!email.matches(emailPattern)){
                    reg_email.setError("Invalid Email");
                    progressDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                }else if (!password.equals(cPassword)){
                    reg_con_pass.setError("Password doesn't match");
                    progressDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this, "Password doesn't match", Toast.LENGTH_SHORT).show();
                }else if (password.length()<6){
                    reg_con_pass.setError("Enter at least 6 character");
                    progressDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this, "Enter at least 6 character", Toast.LENGTH_SHORT).show();
                }else {
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                                DatabaseReference databaseReference = database.getReference().child("user").child(auth.getUid());
                                StorageReference storageReference = firebaseStorage.getReference().child("upload").child(auth.getUid());

                                if (imageUri != null){
                                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()){
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        image_uri =   uri.toString();
                                                        Users users = new Users(auth.getUid(),name,email, image_uri,status);
                                                        databaseReference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                progressDialog.dismiss();
                                                                startActivity(new Intent(RegistrationActivity.this,HomeActivity.class));
                                                            }else {
                                                                Toast.makeText(RegistrationActivity.this, "Error in creating new User", Toast.LENGTH_SHORT).show();
                                                            }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }else {
                                    String status ="Hey there I'm Using this Application";
                                    image_uri = "https://firebasestorage.googleapis.com/v0/b/jannatchatingapp.appspot.com/o/profile.png?alt=media&token=bb6c548e-4202-4855-b573-8ea9023d6bd7";
                                    Users users = new Users(auth.getUid(),name,email, image_uri,status);
                                    databaseReference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                progressDialog.dismiss();
                                                startActivity(new Intent(RegistrationActivity.this,HomeActivity.class));
                                            }else {
                                                Toast.makeText(RegistrationActivity.this, "Error in creating new User", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }

                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(RegistrationActivity.this, "Some Thing wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),10);
            }
        });

        sing_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==10){
            if (data!=null){
                imageUri = data.getData();
                profile_image.setImageURI(imageUri);
            }
        }
    }
}