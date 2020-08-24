package com.bawp.womensafety;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Login extends AppCompatActivity {
    private Button loginButton;
    private Button createAcctButton;
    private TextView forgetpassword;
    private EditText emailAddres;
    private EditText loginpassword;

    private FirebaseUser currentuser;
    private FirebaseAuth firebaseAuth;
  //  private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private ProgressBar progressBar;

    //private FirebaseFirestore db=FirebaseFirestore.getInstance();
    //private CollectionReference collectionReference=db.collection("User");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar = findViewById(R.id.login_progress);


        firebaseAuth = FirebaseAuth.getInstance();


        emailAddres = findViewById(R.id.loginEmail);
        loginpassword = findViewById(R.id.loginPassword);
        forgetpassword=findViewById(R.id.forgetPassword);

        loginButton = findViewById(R.id.button);
        createAcctButton = findViewById(R.id.button2);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailAddres.getText().toString().trim();
                String password = loginpassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    emailAddres.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    loginpassword.setError("Password is Required");
                    return;
                }
                if (password.length() < 8) {
                    loginpassword.setError("Password must be greater than or equal to 8 character");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                //authenticate the user
                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                                           progressBar.setVisibility(View.INVISIBLE);
                                           Toast.makeText(Login.this, "User Logged In", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(Login.this, Interface.class);
                                            startActivity(intent);
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(Login.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();

                        }

                    }
                });



            }
        });



        createAcctButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Login.this, CreateAccount.class));
            }
        });




        forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetMail=new EditText(v.getContext());
                AlertDialog.Builder passwordReset=new AlertDialog.Builder(v.getContext());
                passwordReset.setTitle("Reset Password?");
                passwordReset.setMessage("Enter Your Email To Receive Reset Link");
                passwordReset.setView(resetMail);

                passwordReset.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail=resetMail.getText().toString().trim();
                        firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Login.this,"Reset Link Send To Your Email",Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this,"Error ! Reset Link Not Send "+e.getMessage() ,Toast.LENGTH_SHORT).show();

                            }
                        });


                    }
                });
                passwordReset.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                passwordReset.create().show();
            }
        });
    }


}