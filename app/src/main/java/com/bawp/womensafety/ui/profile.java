package com.bawp.womensafety.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bawp.womensafety.Interface;
import com.bawp.womensafety.Login;
import com.bawp.womensafety.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class profile extends AppCompatActivity implements View.OnClickListener {
    private static final int GALLERY_CODE =1;
    private ImageView backArrow;
    private TextView logout,profileUsername;
    private CircularImageView profileimage;
    private EditText firstcontact,firstcontactnumber,secondcontact,secondcontactnumber,thirdcontact,thirdcontactnumber,usernmae,email;
    private Uri imageUri;
    private TextView save,changepassword;


    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    StorageReference storageReference;
    FirebaseFirestore db=FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        backArrow=findViewById(R.id.back_arrow);
        save=findViewById(R.id.sa);
        changepassword=findViewById(R.id.change);
        logout=findViewById(R.id.logout);
        profileimage=findViewById(R.id.profileimage);
        profileUsername=findViewById(R.id.profileUsername);
        firstcontact=findViewById(R.id.firstcontact);
        firstcontactnumber=findViewById(R.id.firstcontactnumber);
        secondcontact=findViewById(R.id.secondcontact);
        secondcontactnumber=findViewById(R.id.secondcontactnumber);
        thirdcontact=findViewById(R.id.thirdcontact);
        thirdcontactnumber=findViewById(R.id.thirdcontactnumber);
        email=findViewById(R.id.email);


        firebaseAuth=FirebaseAuth.getInstance();
        storageReference=FirebaseStorage.getInstance().getReference();
        StorageReference profileRef=storageReference.child("users/"+ Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
               Picasso.get().load(uri).into(profileimage);
            }
        });

        logout.setOnClickListener(this);
        backArrow.setOnClickListener(this);
        profileimage.setOnClickListener(this);


        user=firebaseAuth.getCurrentUser();
        final DocumentReference documentReference=db.collection("User").document(user.getUid());
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e!=null)
                {
                    Toast.makeText(profile.this,"Error: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
                assert documentSnapshot != null;
                profileUsername.setText(documentSnapshot.getString("userName"));
                firstcontact.setText(documentSnapshot.getString("ContactName1"));
                firstcontactnumber.setText(documentSnapshot.getString("ContactNumber1"));
                secondcontact.setText(documentSnapshot.getString("ContactName2"));
                secondcontactnumber.setText(documentSnapshot.getString("ContactNumber2"));
                thirdcontact.setText(documentSnapshot.getString("ContactName3"));
                thirdcontactnumber.setText(documentSnapshot.getString("ContactNumber3"));
                email.setText(documentSnapshot.getString("email"));
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user.updateEmail(email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Map<String,Object>edit=new HashMap<>();
                        edit.put("ContactName1",firstcontact.getText().toString().trim());
                        edit.put("ContactNumber1",firstcontactnumber.getText().toString().trim());
                        edit.put("ContactName2",secondcontact.getText().toString().trim());
                        edit.put("ContactNumber2",secondcontactnumber.getText().toString().trim());
                        edit.put("ContactName3",thirdcontact.getText().toString().trim());
                        edit.put("ContactNumber3",thirdcontactnumber.getText().toString().trim());
                        edit.put("email",email.getText().toString().trim());
                        documentReference.update(edit).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(profile.this,"Successfully Saved",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(profile.this,"Error: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(profile.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });


            }
        });



        changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("change","password");
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
                                Toast.makeText(profile.this,"Reset Link Send To Your Email",Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(profile.this,"Error ! Reset Link Not Send "+e.getMessage() ,Toast.LENGTH_SHORT).show();

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

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.back_arrow:
                startActivity(new Intent(profile.this, Interface.class));
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(profile.this, Login.class));
                finish();
                break;
            case R.id.profileimage:
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
                break;
                }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData(); // we have the actual path to the image
                //profileimage.setImageURI(imageUri);//show image
                updateImagetoFirebase(imageUri);

            }
        }
    }

    private void updateImagetoFirebase(final Uri imageUri) {
        final StorageReference fileRef=storageReference.child("users/"+ Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                   @Override
                   public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileimage);
                   }
               });

            }
        });
    }

}
