package com.example.jannatchatingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    TextView sign_up;
    EditText email,password;
    TextView login;
    FirebaseAuth auth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();

        sign_up = findViewById(R.id.singUp);
        email =findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String Email = email.getText().toString();
            String Password = password.getText().toString();


            if(TextUtils.isEmpty(Email)|| TextUtils.isEmpty(Password)){
                Toast.makeText(LoginActivity.this, "Enter Valid Data", Toast.LENGTH_SHORT).show();

            }else if (!Email.matches(emailPattern)){
                email.setError("Invalid Email");
                Toast.makeText(LoginActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
            }else if (Password.length()<6){
                email.setError("Invalid Email");
                Toast.makeText(LoginActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
            }else{
                auth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                        }else {
                            Toast.makeText(LoginActivity.this, "Error in Login", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }



            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
            }
        });
    }
}