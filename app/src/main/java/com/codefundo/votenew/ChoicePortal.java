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
import android.widget.Toast;

import com.codefundo.votenew.Offline.Offline;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rengwuxian.materialedittext.MaterialEditText;

import maes.tech.intentanim.CustomIntent;

public class ChoicePortal extends AppCompatActivity {
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
        setContentView(R.layout.activity_choice_portal);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        mAuth = FirebaseAuth.getInstance();

        mLoginProgress = new ProgressDialog(this,R.style.dialog);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");



        forgot=findViewById(R.id.forgot);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        login=findViewById(R.id.login);
        register=findViewById(R.id.register);
        help=findViewById(R.id.help);
        findViewById(R.id.offline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(), Offline.class);
                startActivity(i);
            }
        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            makedialog();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(getApplicationContext(),Register.class);
                startActivity(i);
                CustomIntent.customType(ChoicePortal.this,"fadein-to-fadeout");

            }


        });
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(getApplicationContext(),Forgot.class);
                startActivity(i);
                CustomIntent.customType(ChoicePortal.this,"fadein-to-fadeout");

            }


        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailis = email.getText().toString();
                String passwordis = password.getText().toString();
                if(TextUtils.isEmpty(emailis)){email.setError("Enter email!");}
                if(TextUtils.isEmpty(passwordis)){password.setError("Enter password!");}
                if(!TextUtils.isEmpty(emailis) && !TextUtils.isEmpty(passwordis)) {
                    password.setError(null);
                    email.setError(null);
                    mLoginProgress.setTitle("Logging In");
                    mLoginProgress.setMessage("Please wait while we check your credentials.");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();


                    loginUser(emailis, passwordis);
                }else{
                    Toast.makeText(ChoicePortal.this, "Please Enter Details first", Toast.LENGTH_SHORT).show();}

            }
        });

    }
    private void loginUser(final String email, String password) {


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    final String emailpartwithout[] =email.split("@",2);

                    mLoginProgress.dismiss();

                           if(mAuth.getCurrentUser().isEmailVerified()){
                                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                mainIntent.putExtra("email",emailpartwithout[0]);
                                startActivity(mainIntent);
                                CustomIntent.customType(ChoicePortal.this,"left-to-right");
                            }else{Toast.makeText(getApplicationContext(),"Please Verify Email or enter correct details",Toast.LENGTH_SHORT).show();}
                            finish();
                } else {

                    mLoginProgress.hide();

                    String task_result = task.getException().getMessage().toString();

                    Toast.makeText(getApplicationContext(), "Error : " + task_result, Toast.LENGTH_LONG).show();
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



}
