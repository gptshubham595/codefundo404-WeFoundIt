
package com.codefundo.vote;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.phonenumberui.PhoneNumberActivity;

public class MainActivitymob extends AppCompatActivity {
    private String mobileNumber = "";
    private Button btnVerify;
    private static final int REQUEST_PHONE_VERIFICATION = 1080;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmob);
        btnVerify = findViewById(R.id.btnVerify);
        String PHONE_NUMBER="";
            PHONE_NUMBER=getIntent().getExtras().get("PHONE_NUMBER").toString();
                Intent intent = new Intent(MainActivitymob.this, PhoneNumberActivity.class);
                //Optionally you can add toolbar title
                intent.putExtra("TITLE", getResources().getString(R.string.app_name));
                //Optionally you can pass phone number to populate automatically.
                intent.putExtra("PHONE_NUMBER", PHONE_NUMBER);
                startActivityForResult(intent, REQUEST_PHONE_VERIFICATION);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_PHONE_VERIFICATION:
// If mobile number is verified successfully then you get your phone number to perform further operations.
                if (data != null && data.hasExtra("PHONE_NUMBER") && data.getStringExtra("PHONE_NUMBER") != null) {
                    String phoneNumber = data.getStringExtra("PHONE_NUMBER");
                    mobileNumber = phoneNumber;
                } else {
                    // If mobile number is not verified successfully You can hendle according to your requirement.
                    Toast.makeText(MainActivitymob.this,"Failed",Toast.LENGTH_SHORT);
                }
                break;
        }
    }
}
