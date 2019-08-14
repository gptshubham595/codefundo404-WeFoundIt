package com.codefundo.votenew;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

public class EnterPintoVote extends AppCompatActivity {
    AppCompatEditText pin_et,cnfirmpin,email;
    AppCompatTextView getmail;
    TextInputLayout cnpin;
    Button votenow,forgot;
    String emailintent,aadhaar,finalpin,actualemail;
    DatabaseReference mUser,hidmail,mEmail,mslotstart,mslotend,mpin;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pinto_vote);
        cnpin=findViewById(R.id.cnpin);
        //gone for cnpin
        getmail=findViewById(R.id.getmail);
        pin_et=findViewById(R.id.pin_et);
        cnfirmpin=findViewById(R.id.pin_et2);
        email=findViewById(R.id.pin_email);
        votenow=findViewById(R.id.votenow);
        forgot=findViewById(R.id.forgot);



        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        mAuth = FirebaseAuth.getInstance();
        try{
            emailintent=getIntent().getExtras().get("email").toString().toLowerCase();
            aadhaar=getIntent().getExtras().get("aadhaar").toString().toLowerCase();
        }catch (Exception e ){e.printStackTrace();}

        mUser= FirebaseDatabase.getInstance().getReference().child("Users").child(emailintent).child("familymember").child(aadhaar);
        mUser.keepSynced(true);
        hidmail=mUser.child("hiddenemail");
        hidmail.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                getmail.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        mEmail=mUser.child("email");
        mEmail.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                actualemail= dataSnapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(actualemail);
            }
        });
        mslotend=mUser.child("slotend");
        mslotend.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
        mslotstart=mUser.child("slotstart");
        mslotstart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
        mpin=mUser.child("pin");
        mpin.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                try {
                    finalpin=AESCrypt.decrypt(value);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


        votenow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!actualemail.equals(email.getText().toString())){email.setError("INVALID EMAIL");}
                if(!finalpin.equals(pin_et.getText().toString())){pin_et.setError("INVALID PIN");}
                if(finalpin.equals(pin_et.getText().toString()) && actualemail.equals(email.getText().toString())){
                    Intent i =new Intent(getApplicationContext(),Emailsend.class);
                    i.putExtra("aadhaar",aadhaar);
                    i.putExtra("email",email.getText().toString());
                    startActivity(i);
                }
            }
        });

}
    private void sendMessage(String email1) {
        String rec="gptshubham595@gmail.com";
        String str[]=email1.split(" ");
        String user="vote4usiitg@gmail.com";
        String pass="iitg00000000";

        SendEmailAsyncTask1 email = new SendEmailAsyncTask1();
        email.activity = this;
        email.m = new Mail(user, pass);
        email.m.set_from(user);
        email.m.setBody(aadhaar+" :YOUR PIN To VOTE IS :"+finalpin);
        email.m.set_to(str);
        email.m.set_subject("VOTE4US YOUR PIN IS");
        email.execute();
    }

    public void displayMessage(String message) {
        Snackbar.make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }


}

class SendEmailAsyncTask1 extends AsyncTask<Void, Void, Boolean> {
    Mail m;
    EnterPintoVote activity;

    public SendEmailAsyncTask1() {}

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            if (m.send()) {
                activity.displayMessage("Email sent.");

            } else {
                activity.displayMessage("Email failed to send.");
            }

            return true;
        } catch (AuthenticationFailedException e) {
            Log.e(SendEmailAsyncTask.class.getName(), "Bad account details");
            e.printStackTrace();
            activity.displayMessage("Authentication failed.");
            return false;
        } catch (MessagingException e) {
            Log.e(SendEmailAsyncTask.class.getName(), "Email failed");
            e.printStackTrace();
            activity.displayMessage("Email failed to send.");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            activity.displayMessage("Unexpected error occured.");
            return false;
        }
    }

}
