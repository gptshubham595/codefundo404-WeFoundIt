package com.codefundo.votenew;

import android.app.Activity;
import android.app.Dialog;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;


public class Regwithaadhaar extends AppCompatActivity {
    Button scan;
    EditText e;
    int done=0;
    DatabaseReference mUserDatabase;
    FirebaseAuth mAuth;
    String aadhaar,email;
    Boolean result=false;
   public static  int[] checkfinaluser = {0};
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
                        findDupUser3(aadhaar);

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

        if(done==0)
        dialog.show();
    }

    public void findDupUser3(final String name){
        String emailpartwithout[] =email.split("@",2);
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = current_user.getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("voters");
        rootRef.keepSynced(true);
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.hasChild(name)) {
                    Toast.makeText(Regwithaadhaar.this, "NEW!!", Toast.LENGTH_SHORT).show();
                    //
                    final Dialog dialog2 = new Dialog(Regwithaadhaar.this);
                    dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog2.setCancelable(false);
                    dialog2.setContentView(R.layout.newcustom_layout);
                    dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                    Button cancel = dialog2.findViewById(R.id.retry);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog2.cancel();
                        }
                    });
                    Button ok = dialog2.findViewById(R.id.ok);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog2.cancel();
                            e.setEnabled(false);
                            scan.setEnabled(true);
                        }
                    });

                        dialog2.show();
                    //
                }else{
                    Toast.makeText(Regwithaadhaar.this, "You are already registered!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void findDupUser2(final String name){

        String emailpartwithout[] =email.split("@",2);

        mUserDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(emailpartwithout[0]).child("familymember");
        mUserDatabase.keepSynced(true);

        mUserDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                allfamily admin = dataSnapshot.getValue(allfamily.class); // pojo
                if(admin.getAadhaar().equals(aadhaar)) {
                    checkfinaluser[0]=1;
                    Toast.makeText(Regwithaadhaar.this, "user found", Toast.LENGTH_SHORT).show();
                    Toast.makeText(Regwithaadhaar.this, ""+checkfinaluser[0], Toast.LENGTH_SHORT).show();
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
        if(checkfinaluser[0]==1)
        Toast.makeText(this, "USER ALREADY REGISTERD!!"+checkfinaluser[0], Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "check="+checkfinaluser[0], Toast.LENGTH_SHORT).show();
        if(checkfinaluser[0]==1)
            Toast.makeText(this, "USER ALREADY REGISTERD!!"+checkfinaluser[0], Toast.LENGTH_SHORT).show();


    }

    public boolean finddupuser(final String aadhaar){

        final boolean[] check = {false};
        final boolean[] check2 = {false};
        mUserDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabase.keepSynced(true);
        mUserDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                alladmin admin = dataSnapshot.getValue(alladmin.class); // pojo
                Toast.makeText(Regwithaadhaar.this, ""+admin.getAadhaar(), Toast.LENGTH_SHORT).show();
                if(admin.getAadhaar().equals("A"+aadhaar)) {
                    check[0]=true;
                    Toast.makeText(Regwithaadhaar.this, "admin found", Toast.LENGTH_SHORT).show();
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
        return check[0];
            }
}