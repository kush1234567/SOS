package com.bawp.womensafety;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bawp.womensafety.model.womenSafetyApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateAccount extends AppCompatActivity {
    private Button loginButton;
    private Button createAccount;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentuser;
    private FirebaseAuth.AuthStateListener authStateListener;

    private TextView create;
    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;
    private EditText userNameEditText;

    private FirebaseFirestore db=FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        firebaseAuth = FirebaseAuth.getInstance();
        create=findViewById(R.id.AlreadyAccount);
        createAccount = findViewById(R.id.createAccount);
        progressBar = findViewById(R.id.create_progress);
        emailEditText = findViewById(R.id.createEmail);
        passwordEditText = findViewById(R.id.createPassword);
        userNameEditText = findViewById(R.id.createUsername);

                if(firebaseAuth.getCurrentUser()!=null) {
                    startActivity(new Intent(CreateAccount.this, Interface.class));
                    finish();
                }


        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateAccount.this,Login.class));
            }
        });
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username=userNameEditText.getText().toString().trim();
                final String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    userNameEditText.setError("Username is required");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    emailEditText.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    passwordEditText.setError("Password is Required");
                    return;
                }
                if (password.length() < 8) {
                    passwordEditText.setError("Password must be greater than or equal to 8 character");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification email
                            currentuser=firebaseAuth.getCurrentUser();
                            assert currentuser != null;
                            currentuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(CreateAccount.this,"VERIFICATION EMAIL HAS BEEN SENT",Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CreateAccount.this,"EMAIL NOT SENT ! "+e.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            });

                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(CreateAccount.this, "User created", Toast.LENGTH_SHORT).show();
                            currentuser=firebaseAuth.getCurrentUser();
                            final String currentuserid=currentuser.getUid();
                            Map<String,Object>userObj=new HashMap<>();
                            userObj.put("userdId",currentuserid);
                            userObj.put("userName",username);
                            userObj.put("email",email);
                            DocumentReference documentReference=db.collection("User").document(currentuserid);
                            documentReference.set(userObj).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    womenSafetyApi womenSafetyApi= com.bawp.womensafety.model.womenSafetyApi.getInstance();
                                    womenSafetyApi.setUsername(username);
                                    womenSafetyApi.setUserId(currentuserid);

                                    Intent intent = new Intent(CreateAccount.this,MainActivity.class);
                                    intent.putExtra("Username",username);
                                    startActivity(intent);
                                }
                            });

                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(CreateAccount.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }

}