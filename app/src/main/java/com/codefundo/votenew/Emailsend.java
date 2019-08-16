package com.codefundo.votenew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.AccessController;
import java.util.Random;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

public class Emailsend extends AppCompatActivity {
       private TextView user;
    private FirebaseAuth mAuth;
    private TextView pass;
    private TextView subject;
    private TextView body;
    private TextView recipient;
    EditText pin;
    Button vote;
    String code,email="",aadhaar="";
    int code1,code2,code3,code4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailsend);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        mAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        email="";
        try{
            email=getIntent().getExtras().get("email").toString().toLowerCase();
            aadhaar=getIntent().getExtras().get("aadhaar").toString();
        }catch (Exception e ){e.printStackTrace();}


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fab.performClick();
            }
        }, 3000);

        sendMessage(email);


        pin=findViewById(R.id.pin);
        vote=findViewById(R.id.vote);
        user = findViewById(R.id.username);
        pass = findViewById(R.id.password);
        subject =findViewById(R.id.subject);
        body = findViewById(R.id.body);
        recipient = findViewById(R.id.recipient);

        vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pin.getText().toString().equals(code)){
                    Intent i =new Intent(getApplicationContext(),VOTEFINAL.class);
                    i.putExtra("aadhaar",aadhaar);
                    i.putExtra("email",email);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
        Random rand = new Random();
        code1 = rand.nextInt(9);
        code2 = code1*10+rand.nextInt(8)+1;
        code3 = code2*10+rand.nextInt(7)+2;
        code4 = code3*10+rand.nextInt(6)+3;
        code=code4+"";
        code=code.trim();
        add(email,code,aadhaar);
//        String[] recipients = { recipient.getText().toString() };
        SendEmailAsyncTask email = new SendEmailAsyncTask();
        email.activity = this;
        email.m = new Mail(user, pass);
        email.m.set_from(user);
        email.m.setBody("YOUR OTP To VOTE IS :"+code);
        email.m.set_to(str);
        email.m.set_subject("VOTE4US CODE FOR VOTING");
        email.execute();
    }

    public void displayMessage(String message) {
        Snackbar.make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
    public void add(String email,String code,String aadhaar){
        String emailpartwithout[] =email.split("@",2);
        DatabaseReference allpoliticalparty= FirebaseDatabase.getInstance().getReference().child("Users").child(emailpartwithout[0]).child("familymember").child(aadhaar).child("code");
        allpoliticalparty.keepSynced(true);
        allpoliticalparty.setValue(code).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
           if(task.isSuccessful()){
               vote.setEnabled(true);
           }
            }
        });

    }
}

class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
    Mail m;
    Emailsend activity;

    public SendEmailAsyncTask() {}

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
