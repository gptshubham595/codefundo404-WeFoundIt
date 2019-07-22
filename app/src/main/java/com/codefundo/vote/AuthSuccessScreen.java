package com.codefundo.vote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class AuthSuccessScreen extends AppCompatActivity {
    private ProgressDialog mLoginProgress;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase,mDatabase,mDatabase2;
    private AppCompatEditText cnfirmpinEt,pinEt;
    private Button savepin,forgot;
    Boolean SET_OR_NOT=false;
    private String first="false";
    private String originalpin="";
    private SharedPreferences setornot=null;
    FirebaseUser fbUser;
    public static String pin;
    TextInputLayout cnfirmpinEt2;
    SharedPreferences pref=null;

    static int pinset=0;
    String id,aid;
    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_success_screen);

        savepin=findViewById(R.id.savepin);
        savepin.setEnabled(false);
        pinEt =  findViewById(R.id.pin_et);
        forgot=findViewById(R.id.forgot);
        cnfirmpinEt =  findViewById(R.id.pin_et2);
        cnfirmpinEt2 =  findViewById(R.id.cnpin);
        aid=Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        id = aid + "@gmail.com";
        mAuth = FirebaseAuth.getInstance();
        fbUser = mAuth.getCurrentUser();
        mLoginProgress = new ProgressDialog(this,R.style.dialog);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        String FILE_NAME="Userpin";
        pref = getApplicationContext().getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        if(pref.getBoolean("pinsettingdone",true)){
            Toast.makeText(this, "pin setting Done man", Toast.LENGTH_SHORT).show();
            pinset=2;
        }else{
            //Pin not set
            Toast.makeText(this, "pin setting not Done man", Toast.LENGTH_SHORT).show();
            pinset=3;
        }

        try{first= getIntent().getExtras().get("first").toString();
            if(pinset==3) {

                    savepin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            register_user(id, pinEt.getText().toString());

                        }
                    });

                } if(pinset==2) {
                    cnfirmpinEt.setVisibility(View.GONE);
                    cnfirmpinEt2.setVisibility(View.GONE);
                    forgot.setVisibility(View.VISIBLE);
                    savepin.setText("SUBMIT PIN");
                    savepin.setEnabled(true);
                    savepin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Login(id, pinEt.getText().toString());
                        }
                    });


                }

        }

        catch (Exception e){e.printStackTrace();}





        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgot_or_changepass();
            }
        });
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
                        savepin.setEnabled(false);
                    }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }


    private void forgot_or_changepass() {
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        final  String uidkey = current_user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(aid).child("ADMIN");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String admin=dataSnapshot.getValue(String.class);
                    if(admin.equals("YES")){
                        mDatabase2 = FirebaseDatabase.getInstance().getReference().child("Users").child(aid).child("email");
                        mDatabase2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final String valueemail=dataSnapshot.getValue(String.class);
                                fbUser.updateEmail(valueemail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            mAuth.sendPasswordResetEmail(valueemail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(getApplicationContext(),"Check Your mail and reset your password",Toast.LENGTH_LONG).show();

                                                    }
                                                }
                                            });
                                        }
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    private void Login(String id, String pass) {

        savepin.setEnabled(true);
        pin=pass;
        mAuth.signInWithEmailAndPassword(id, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    mLoginProgress.dismiss();
                    pinset=3;
                    pinsetdone();
                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity2.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);

                    finish();

                } else {

                    mLoginProgress.hide();

                    String task_result = task.getException().getMessage().toString();

                    Toast.makeText(getApplicationContext(), "Error : " + task_result, Toast.LENGTH_LONG).show();

                }

            }
        });

    }

    private void register_user(final String id, String pass) {

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

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(aid);

                    String device_token = FirebaseInstanceId.getInstance().getToken();

                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("ADMIN", "NO");
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
                        userMap.put("device_id", id);
                    userMap.put("device_token", device_token);

                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                mLoginProgress.dismiss();

                                Toast.makeText(getApplicationContext(),"Registered!! ",Toast.LENGTH_LONG).show();
                                Toast.makeText(AuthSuccessScreen.this, "PIN SAVED", Toast.LENGTH_SHORT).show();
                                pinset=2;
                                pinsetdone();
                                Login(id,pinEt.getText().toString());
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
    public  void pinsetdone()
    {SharedPreferences pref = getApplicationContext().getSharedPreferences("Userpin", MODE_PRIVATE);
    SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("pinsettingdone", true);
                editor.commit();
}
}
