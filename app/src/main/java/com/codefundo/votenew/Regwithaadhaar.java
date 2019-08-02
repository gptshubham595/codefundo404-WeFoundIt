package com.codefundo.votenew;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codefundo.votenew.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static java.lang.Thread.sleep;


public class Regwithaadhaar extends AppCompatActivity {
    Button scan;
    EditText e;
    DatabaseReference mUserDatabase;
    FirebaseAuth mAuth;
    String aadhaar,email;
    Boolean result=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerwithaadhaar);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        mAuth = FirebaseAuth.getInstance();
        scan = (Button) findViewById(R.id.scan);
        scan.setEnabled(false);
        e=findViewById(R.id.num);
        Button b=findViewById(R.id.numbtn);
        email=getIntent().getExtras().get("email").toString().toLowerCase();
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aadhaar=e.getText().toString();
                if((!TextUtils.isEmpty(aadhaar))&& aadhaar.length()==12){
                    result=Verhoeff.validateVerhoeff(aadhaar);
                    if(!result){
                        Toast.makeText(Regwithaadhaar.this, "Sorry Please Provide Proper Aadhaar Number", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(Regwithaadhaar.this, "Verified Aadhaar Number", Toast.LENGTH_SHORT).show();
                        try {
                            sleep(20);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        if(finddupuser(aadhaar)!=0)
                        {showDialog(Regwithaadhaar.this);}else{
                            Toast.makeText(Regwithaadhaar.this, "Sorry User already exist!", Toast.LENGTH_SHORT).show();
                        }
                        //scan.setEnabled(true);
                    }
                }else{
                    Toast.makeText(Regwithaadhaar.this, "Sorry Please Provide Proper Aadhaar Number", Toast.LENGTH_SHORT).show();
                }
            }

        });


        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent rIntent = new Intent(Regwithaadhaar.this, ReaderActivity.class);
                rIntent.putExtra("aadhaar",aadhaar);
                rIntent.putExtra("email",email);
                startActivity(rIntent);
            }
        });

    }
    public void showDialog(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.newcustom_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button cancel = dialog.findViewById(R.id.retry);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        Button ok = dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                e.setEnabled(false);
                scan.setEnabled(true);
            }
        });


        dialog.show();
    }
    public int finddupuser(final String aadhaar){

        final int[] check = {0};
        final int[] check2 = {0};
        mUserDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                alladmin admin = dataSnapshot.getValue(alladmin.class); // pojo
                if(admin.getAadhaar().equals("A"+aadhaar)) {
                    check[0]=1;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()){
                    allfamily fam_mem=userSnapshot.getValue(allfamily.class);
                    if(fam_mem.getAadhaar().equals(aadhaar)) {
                        check2[0] =1;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if(check2[0]==0 && check[0]==0){return 1;}
        else return 0;
    }
}