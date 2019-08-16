package com.codefundo.votenew;

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
import android.view.WindowManager;
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


public class PinActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_pin);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

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
        mUserDatabase.keepSynced(true);
        String FILE_NAME="Userpin";
        pref = getApplicationContext().getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        if(pref.getBoolean("pinsettingdone",true)){
            Toast.makeText(this, "pin setting not Done man", Toast.LENGTH_SHORT).show();
            pinset=3;
        }else{
            //Pin not set

            Toast.makeText(this, "pin setting Done man", Toast.LENGTH_SHORT).show();
            pinset=2;
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
                    Intent mainIntent = new Intent(getApplicationContext(), ChoicePortal.class);
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
                        Toast.makeText(PinActivity.this, "User with this email already exist.", Toast.LENGTH_SHORT).show();
                        Toast.makeText(PinActivity.this, "Type Previous PIN now.", Toast.LENGTH_SHORT).show();
                        cnfirmpinEt.setVisibility(View.GONE);
                        savepin.setText("SUBMIT PIN");
                    }else{

                                    mLoginProgress.dismiss();
                                    Toast.makeText(getApplicationContext(),"Registered!! ",Toast.LENGTH_LONG).show();
                                    Toast.makeText(PinActivity.this, "PIN SAVED", Toast.LENGTH_SHORT).show();
                                    pinset=2;
                                    pinsetdone();
                                    Login(id,pinEt.getText().toString());
                           }


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
        editor.putBoolean("pinsettingdone", false);
        editor.commit();
    }
}
