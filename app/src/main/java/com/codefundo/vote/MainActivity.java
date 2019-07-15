package com.codefundo.vote;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.codefundo.vote.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.multidots.fingerprintauth.AuthErrorCodes;
import com.multidots.fingerprintauth.FingerPrintAuthCallback;
import com.multidots.fingerprintauth.FingerPrintAuthHelper;
import com.multidots.fingerprintauth.FingerPrintUtils;

import java.io.IOException;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements FingerPrintAuthCallback {
    private ImageView img;
    private TextView mAuthMsgTv;
    private ViewSwitcher mSwitcher;
    private Button mGoToSettingsBtn,submit;
    private FingerPrintAuthHelper mFingerPrintAuthHelper;
    private ProgressDialog mLoginProgress;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase,mDatabase;
    SharedPreferences prefs=null;
    Boolean isFirstRun=false;
    String first="false";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mLoginProgress = new ProgressDialog(this,R.style.dialog);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        prefs= getSharedPreferences("com.codefundo.vote", MODE_PRIVATE);
        isFirstRun=prefs.getBoolean("isFirstRun", true);
        if(isFirstRun){
            Toast.makeText(this, "First Time User", Toast.LENGTH_SHORT).show();

        }
    first=isFirstRun.toString();
        img=findViewById(R.id.tick);
        mGoToSettingsBtn = (Button) findViewById(R.id.go_to_settings_btn);
        mGoToSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FingerPrintUtils.openSecuritySettings(MainActivity.this);
            }
        });

        mSwitcher = (ViewSwitcher) findViewById(R.id.main_switcher);
        mAuthMsgTv = (TextView) findViewById(R.id.auth_message_tv);

        mFingerPrintAuthHelper = FingerPrintAuthHelper.getHelper(this, this);
    }



    @Override
    protected void onResume() {
        super.onResume();
        mGoToSettingsBtn.setVisibility(View.GONE);
        if (prefs.getBoolean("isFirstRun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean("isFirstRun", false).commit();
        }
        mAuthMsgTv.setText("Scan your finger");

        //start finger print authentication
        mFingerPrintAuthHelper.startAuth();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFingerPrintAuthHelper.stopAuth();
    }

    @Override
    public void onNoFingerPrintHardwareFound() {
        mAuthMsgTv.setText("Your device does not have finger print scanner.SORRY!");
        //mAuthMsgTv.setText("Your device does not have finger print scanner. Please type 1234 to authenticate.");
        //mSwitcher.showNext();
        Intent i=new Intent(MainActivity.this, AuthSuccessScreen.class);
        i.putExtra("first",first);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

    }

    @Override
    public void onNoFingerPrintRegistered() {
        mAuthMsgTv.setText("There are no finger prints registered on this device. Please register your finger from settings.");
        mGoToSettingsBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBelowMarshmallow() {
        mAuthMsgTv.setText("You are running older version of android that does not support finger print authentication. Please type 1234 to authenticate.");
     //   mSwitcher.showNext();
        Intent i=new Intent(MainActivity.this, AuthSuccessScreen.class);
        i.putExtra("first",first);
        startActivity(i);

    }

    @Override
    public void onAuthSuccess(FingerprintManager.CryptoObject cryptoObject) {
        Toast.makeText(MainActivity.this, "Authentication succeeded.", Toast.LENGTH_SHORT).show();
        try {
            sleep(70);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        img.setVisibility(View.VISIBLE);

        try {
            sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Toast.makeText(MainActivity.this, "Success !!", Toast.LENGTH_SHORT).show();
         Intent i=new Intent(MainActivity.this, AuthSuccessScreen.class);
         i.putExtra("first",first);
         startActivity(i);
    }







    @Override
    public void onAuthFailed(int errorCode, String errorMessage) {
        switch (errorCode) {
            case AuthErrorCodes.CANNOT_RECOGNIZE_ERROR:
                mAuthMsgTv.setText("Cannot recognize your finger print. Please try again or enter via PIN.");
                break;
            case AuthErrorCodes.NON_RECOVERABLE_ERROR:
                mAuthMsgTv.setText("Cannot initialize finger print authentication. . Please try again or enter via PIN.");
                Intent i=new Intent(MainActivity.this, AuthSuccessScreen.class);
                i.putExtra("first",first);
                startActivity(i);

                //mSwitcher.showNext();
                break;
            case AuthErrorCodes.RECOVERABLE_ERROR:
                mAuthMsgTv.setText(errorMessage);
                break;
        }
    }


}
