package com.codefundo.vote;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codefundo.vote.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity2 extends AppCompatActivity {
    Button scan;
    EditText e;
    Boolean result=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainf);
        scan = (Button) findViewById(R.id.scan);
        scan.setEnabled(false);
        e=findViewById(R.id.num);
        Button b=findViewById(R.id.numbtn);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String aadhaar=e.getText().toString();
                if((!TextUtils.isEmpty(aadhaar))&& aadhaar.length()==12){
                    result=Verhoeff.validateVerhoeff(aadhaar);
                    if(!result){
                        Toast.makeText(MainActivity2.this, "Sorry Please Provide Proper Aadhaar Number", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MainActivity2.this, "Verified Aadhaar Number", Toast.LENGTH_SHORT).show();
                        showDialog(MainActivity2.this);
                        //scan.setEnabled(true);
                    }
                }else{
                    Toast.makeText(MainActivity2.this, "Sorry Please Provide Proper Aadhaar Number", Toast.LENGTH_SHORT).show();
                }
            }

        });
        final String ad =e.getText().toString();


        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent rIntent = new Intent(MainActivity2.this, ReaderActivity.class);
                rIntent.putExtra("aadhaar",e.getText().toString());
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
}