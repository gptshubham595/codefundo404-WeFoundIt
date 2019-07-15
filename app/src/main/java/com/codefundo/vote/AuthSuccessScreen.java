package com.codefundo.vote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class AuthSuccessScreen extends AppCompatActivity {
    private ProgressDialog mLoginProgress;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase,mDatabase;
    private EditText cnfirmpinEt,pinEt;
    private Button savepin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_success_screen);
        savepin=findViewById(R.id.savepin);
        savepin.setEnabled(false);
        pinEt = (EditText) findViewById(R.id.pin_et);
        cnfirmpinEt = (EditText) findViewById(R.id.pin_et2);
        final String id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID) + "@gmail.com";
        mAuth = FirebaseAuth.getInstance();
        mLoginProgress = new ProgressDialog(this,R.style.dialog);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        String first="false";

        try{first= getIntent().getExtras().get("first").toString();
        if(first.equals("true")){
        savepin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register_user(id,pinEt.getText().toString());

            }
        });
            Login(id,pinEt.getText().toString());
        }
        else{
        cnfirmpinEt.setVisibility(View.GONE);
        savepin.setText("SUBMIT PIN");
        savepin.setEnabled(true);
            savepin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Login(id,pinEt.getText().toString());
                }
            });


        }}catch (Exception e){e.printStackTrace();}

        cnfirmpinEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length()==8)
                    if (s.toString().equals(pinEt.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Matched", Toast.LENGTH_SHORT).show();
                    savepin.setEnabled(true);
                }else{Toast.makeText(getApplicationContext(), "Not Matched", Toast.LENGTH_SHORT).show();
                    cnfirmpinEt.setError("Not Matched");
                    }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void Login(String id, String pass) {
        savepin.setEnabled(true);
        mAuth.signInWithEmailAndPassword(id, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    mLoginProgress.dismiss();

                    String current_user_id = mAuth.getCurrentUser().getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent mainIntent = new Intent(getApplicationContext(), MainActivity2.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);

                            finish();


                        }
                    });




                } else {

                    mLoginProgress.hide();

                    String task_result = task.getException().getMessage().toString();

                    Toast.makeText(getApplicationContext(), "Error : " + task_result, Toast.LENGTH_LONG).show();

                }

            }
        });

    }

    private void register_user(String id, String pass) {

        mAuth.createUserWithEmailAndPassword(id, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(AuthSuccessScreen.this, "User with this email already exist.", Toast.LENGTH_SHORT).show();
                        Toast.makeText(AuthSuccessScreen.this, "Type Previous PIN now.", Toast.LENGTH_SHORT).show();
                        cnfirmpinEt.setVisibility(View.GONE);
                        savepin.setText("SUBMIT PIN");
                    }else{

                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    String device_token = FirebaseInstanceId.getInstance().getToken();

                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name", "Default");
                    userMap.put("aadhaar", "default");
                    userMap.put("dob", "default");
                    userMap.put("dist", "default");
                    userMap.put("state", "default");
                    userMap.put("pincode", "default");
                    userMap.put("age", "default");
                    userMap.put("gender", "default");
                    userMap.put("mobile", "default");
                    userMap.put("email", "default");
                    userMap.put("image", "default");
                    userMap.put("thumb_image", "default");
                    userMap.put("device_token", device_token);

                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                mLoginProgress.dismiss();

                                Toast.makeText(getApplicationContext(),"Registered!! ",Toast.LENGTH_LONG).show();
                                Toast.makeText(AuthSuccessScreen.this, "PIN SAVED", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });}


                } else {

                    mLoginProgress.hide();
                    Toast.makeText(getApplicationContext(), "Cannot Sign in. Please check the form and try again.", Toast.LENGTH_LONG).show();

                }

            }
        });
    }
}
