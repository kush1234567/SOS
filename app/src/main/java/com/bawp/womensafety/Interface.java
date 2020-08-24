package com.bawp.womensafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bawp.womensafety.ui.NearByPlaces;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Objects;


public class Interface extends AppCompatActivity implements View.OnClickListener {
    private  Button verify;
    ImageButton profile,sos,police;
    private TextView username,verifymail;
    String userid,usern;

    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    private FirebaseUser user;

    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interface);

        //toolbar
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        username=findViewById(R.id.username);
        verify=findViewById(R.id.verifynow);
        verifymail=findViewById(R.id.Emailnotverified);
        profile=findViewById(R.id.profile);
        police=findViewById(R.id.police);
        sos=findViewById(R.id.sos);



        final AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        assert mAudioManager != null;
        final int originalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource("http://buildappswithpaulo.com/music/watch_me.mp3");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
            }
        });
        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(Interface.this,MapsActivity.class));

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();

                }

        }});




                profile.setOnClickListener(this);
                police.setOnClickListener(this);

        firebaseAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

/*
        if(womenSafetyApi.getInstance()!=null)
        {
            usern=womenSafetyApi.getInstance().getUsername();
        }
        username.setText(usern);
*/
/*
//getting username
        userid= Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        DocumentReference documentReference=db.collection("User").document(userid);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                assert documentSnapshot != null;
                Log.d("asd", Objects.requireNonNull(documentSnapshot.getString("userName")));
                username.setText(MessageFormat.format("Hello {0}", documentSnapshot.getString("userName")));
            }
        });
*/
        //email verification
        user=firebaseAuth.getCurrentUser();
        assert user != null;
        if(!user.isEmailVerified())
        {
            verifymail.setVisibility(View.VISIBLE);
            verify.setVisibility(View.VISIBLE);
            verify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Interface.this,"VERIFICATION EMAIL HAS BEEN SENT",Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Interface.this,"EMAIL NOT SENT ! "+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });


                }
            });
        }

    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.profile:
               startActivity(new Intent(Interface.this, com.bawp.womensafety.ui.profile.class));
                break;
            case R.id.police:
                Log.d("police","police");
                startActivity(new Intent(Interface.this, NearByPlaces.class));
                Log.d("police1","police1");
                break;

        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.menu_one:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Interface.this,Login.class));
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null)
        {
            mediaPlayer.pause();
            mediaPlayer.release();
        }
    }
}
