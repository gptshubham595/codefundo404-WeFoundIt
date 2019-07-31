package com.codefundo.votenew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.app.AlertDialog;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

import maes.tech.intentanim.CustomIntent;
import test.jinesh.captchaimageviewlib.CaptchaImageView;

public class Register extends AppCompatActivity {
    private AppCompatTextView help,forgot;
    private AppCompatButton login,register;
    private static final int MYcode = 78;
    private ProgressDialog mLoginProgress;
    ImageView refreshButton;
    EditText captchaInput;
    Button submitButton;
    AppCompatButton reset;
    CaptchaImageView captchaImageView;
    private FirebaseAuth mAuth;

    private DatabaseReference mUserDatabase,mDatabase;

    MaterialEditText email,password,aadhaar,mobile;
    public static String emailfinal="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        mLoginProgress = new ProgressDialog(this,R.style.dialog);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        refreshButton= (ImageView) findViewById(R.id.regen);
        captchaInput= (EditText) findViewById(R.id.captchaInput);
        captchaImageView= (CaptchaImageView) findViewById(R.id.image);
        captchaImageView.setCaptchaType(CaptchaImageView.CaptchaGenerator.BOTH);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captchaImageView.regenerate();
            }
        });


        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        register=findViewById(R.id.register);
        aadhaar=findViewById(R.id.aadhaar);
        mobile=findViewById(R.id.mobile);

        ///
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailis = email.getText().toString();
                String passwordis = password.getText().toString();
                String aadhaaris = aadhaar.getText().toString();
                String mobileis = mobile.getText().toString();
                if(TextUtils.isEmpty(emailis)){email.setError("Enter email!");}
                if(TextUtils.isEmpty(passwordis)){password.setError("Enter password!");}
                if(TextUtils.isEmpty(mobileis) || ! isValidMobile(mobileis)){mobile.setError("Enter correct mobile!");}
                if(TextUtils.isEmpty(aadhaaris) || !Verhoeff.validateVerhoeff(aadhaaris)){aadhaar.setError("Enter Proper aadhaar!");}
                if(!TextUtils.isEmpty(emailis) && isValidMail(emailis) && isValidMobile(mobileis) && !TextUtils.isEmpty(passwordis) && Verhoeff.validateVerhoeff(aadhaaris)) {
                    if(captchaInput.getText().toString().equals(captchaImageView.getCaptchaCode())){
                        Toast.makeText(Register.this, "Matched", Toast.LENGTH_SHORT).show();
                    password.setError(null);
                    email.setError(null);
                    captchaInput.setError(null);
                    mLoginProgress.setTitle("Registering User");
                    mLoginProgress.setMessage("Please wait while we create your account !");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();
                    register_user( emailis, passwordis,aadhaaris,mobileis);
                }else{captchaInput.setError("Enter correct Captcha code"); captchaImageView.regenerate();}}else{
                    Toast.makeText(Register.this, "Please Enter Proper Aadhaar", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void register_user(final String email, String password, final String aadhaaris, final String mobileis) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    String emailpartwithout[] =email.split("@",2);

                    emailfinal=emailpartwithout[0].toLowerCase();
                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(emailpartwithout[0]);

                    String device_token = FirebaseInstanceId.getInstance().getToken();

                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("aadhaar", aadhaaris);
                    userMap.put("mobile", mobileis);
                    userMap.put("TOTAL_FAMILY_MEMBER", "0");
                    userMap.put("device_token", device_token);

                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                mLoginProgress.dismiss();
                                sendEmailVerification();
                                Toast.makeText(getApplicationContext(),"Registered!! Please verify Email Confirmation and Login",Toast.LENGTH_LONG).show();
                                Intent i =new Intent(getApplicationContext(),ChoicePortal.class);
                                startActivity(i);
                                CustomIntent.customType(Register.this,"fadein-to-fadeout");
                            }

                        }
                    });


                } else {

                    mLoginProgress.hide();
                    Toast.makeText(getApplicationContext(), "Cannot Sign in. Please check the form and try again.", Toast.LENGTH_LONG).show();

                }

            }
        });

    }
    private void makedialog(){
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(R.layout.dialog_option)
                .setNegativeButton("OK", null)
                .show();

    }


    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }
    private void sendEmailVerification() {
        Toast.makeText(getApplicationContext(),"Registered!! Please verify Email Confirmation and Login",Toast.LENGTH_LONG).show();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Check your Email", Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut();
                    }

                    else{
                        Toast.makeText(getApplicationContext(),"ERROR OCCURED TRY AGAIN!!",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

}
