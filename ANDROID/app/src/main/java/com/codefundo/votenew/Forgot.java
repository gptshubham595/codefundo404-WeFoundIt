package com.codefundo.votenew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import maes.tech.intentanim.CustomIntent;
import test.jinesh.captchaimageviewlib.CaptchaImageView;

public class Forgot extends AppCompatActivity {
    ImageView refreshButton;
    EditText captchaInput;
    Button submitButton;
    AppCompatButton reset;
    CaptchaImageView captchaImageView;
    private AppCompatTextView help,forgot;
    private AppCompatButton login,register;
    private static final int MYcode = 78;
    private ProgressDialog mLoginProgress;

    private FirebaseAuth mAuth;

    private DatabaseReference mUserDatabase,mDatabase;

    MaterialEditText email,password,aadhaar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        mAuth = FirebaseAuth.getInstance();

        mLoginProgress = new ProgressDialog(this,R.style.dialog);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        email=findViewById(R.id.email);
        refreshButton= (ImageView) findViewById(R.id.regen);
        reset=findViewById(R.id.reset);
        captchaInput= (EditText) findViewById(R.id.captchaInput);
        captchaImageView= (CaptchaImageView) findViewById(R.id.image);
        captchaImageView.setCaptchaType(CaptchaImageView.CaptchaGenerator.BOTH);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captchaImageView.regenerate();
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(captchaInput.getText().toString().equals(captchaImageView.getCaptchaCode())){
                    Toast.makeText(Forgot.this, "Matched", Toast.LENGTH_SHORT).show();

                    //Forgot
                    String emailis = email.getText().toString();
                    if(!TextUtils.isEmpty(emailis)){
                        password.setError(null);
                        email.setError(null);
                        mAuth.sendPasswordResetEmail(emailis).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(),"Check Your mail and reset your password",Toast.LENGTH_LONG).show();
                                    Intent i =new Intent(getApplicationContext(),ChoicePortal.class);
                                    startActivity(i);
                                    CustomIntent.customType(Forgot.this,"fadein-to-fadeout");
                                }
                            }
                        });
                    }
                    else{ email.setError("Enter email !");
                        password.setError(null);

                        Toast.makeText(getApplicationContext(),"Please enter a email..", Toast.LENGTH_SHORT).show();}

                }else{
                    Toast.makeText(Forgot.this, "Not Matching", Toast.LENGTH_SHORT).show();
                    captchaInput.setError("Enter Correct code!");
                }
            }
        });
    }
}