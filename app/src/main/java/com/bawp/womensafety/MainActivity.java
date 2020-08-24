package com.bawp.womensafety;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private EditText contactname1;
    private EditText contactnumber1;
    private EditText contactname2;
    private EditText contactnumber2;
    private EditText contactname3;
    private EditText contactnumber3;
    private Button save;
    String username;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db=FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contactname1=findViewById(R.id.contactname1);
        contactnumber1=findViewById(R.id.contactnumber1);
        contactname2=findViewById(R.id.contactname2);
        contactnumber2=findViewById(R.id.contactnumber2);
        contactname3=findViewById(R.id.contactname3);
        contactnumber3=findViewById(R.id.contactnumber3);
        username=getIntent().getStringExtra("Username");

        firebaseAuth=FirebaseAuth.getInstance();
        save=findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String c1=contactname1.getText().toString().trim();
                String n1=contactnumber1.getText().toString().trim();
                String c2=contactname2.getText().toString().trim();
                String n2=contactnumber2.getText().toString().trim();
                String c3=contactname3.getText().toString().trim();
                String n3=contactnumber3.getText().toString().trim();

                String userid= Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

                DocumentReference documentReference=db.collection("User").document(userid);
                Map<String,Object>editobj=new HashMap<>();
                editobj.put("ContactName1",c1);
                editobj.put("ContactNumber1",n1);
                editobj.put("ContactName2",c2);
                editobj.put("ContactNumber2",n2);
                editobj.put("ContactName3",c3);
                editobj.put("ContactNumber3",n3);
                documentReference.update(editobj).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(MainActivity.this,Interface.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,"Error: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



    }
}
