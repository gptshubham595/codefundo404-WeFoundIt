package com.codefundo.votenew.Offline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.codefundo.votenew.AESCrypt;
import com.codefundo.votenew.MainActivity;
import com.codefundo.votenew.R;

import java.util.ArrayList;
import java.util.List;

public class Offline extends AppCompatActivity {
    private static final String[] requiredPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
    };
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_CODE = 1;
    EditText pin,mobile,name,aadhaar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
        setContentView(R.layout.activity_offline);
        name=findViewById(R.id.name);
        mobile=findViewById(R.id.mobile);
        aadhaar=findViewById(R.id.aadhaar);
        pin=findViewById(R.id.pin);
        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sendLongSMS("+917578983840",AESCrypt.encrypt(mobile.getText().toString().trim()+" "+name.getText().toString().trim()+" "+mobile.getText().toString().trim()+" "+aadhaar.getText().toString().trim()+" "+pin.getText().toString().trim()));
                    //sendSMS("+917578983840","HE");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_CODE: {
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    checkPermissions();
                }
            }
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        final List<String> neededPermissions = new ArrayList<>();

        for (final String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    permission) != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(permission);
            }
        }
        if (!neededPermissions.isEmpty()) {
            requestPermissions(neededPermissions.toArray(new String[]{}),
                    MY_PERMISSIONS_REQUEST_ACCESS_CODE);
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
