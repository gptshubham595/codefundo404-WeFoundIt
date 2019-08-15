package com.codefundo.votenew.Offline;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;

import com.codefundo.votenew.AESCrypt;
import com.codefundo.votenew.MainActivity;
import com.codefundo.votenew.R;

import java.util.ArrayList;

public class Offline extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);
        EditText name=findViewById(R.id.name);
        EditText mobile=findViewById(R.id.mobile);
        EditText aadhaar=findViewById(R.id.aadhaar);
        EditText pin=findViewById(R.id.pin);


        try {
            sendSMS("+91"+mobile
                    .getText().toString().trim(), AESCrypt.encrypt(name.getText().toString().trim()+" "+mobile.getText().toString().trim()+" "+aadhaar.getText().toString().trim()+" "+pin.getText().toString().trim()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
    public void sendLongSMS(String phone,final String msg) {
        String phoneNumber = "0123456789";
        String message = "Hello World! Now we are going to demonstrate " +
                "how to send a message with more than 160 characters from your Android application.";
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> parts = smsManager.divideMessage(msg);
        smsManager.sendMultipartTextMessage(phone, null, parts, null, null);
    }
}
