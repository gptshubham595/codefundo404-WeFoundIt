package com.codefundo.vote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codefundo.vote.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Calendar;

public class ReaderActivity extends AppCompatActivity {
    private Button scan_btn;
    String aadhaar;
    EditText ad,adt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        scan_btn = (Button) findViewById(R.id.scan_btn);
        aadhaar=getIntent().getExtras().get("aadhaar").toString();
        final Activity activity = this;
        ad=findViewById(R.id.ad);
        ad.setText(aadhaar);
        adt=findViewById(R.id.adto);
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()==null){
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
            }
            else {
               // Toast.makeText(this, result.getContents(),Toast.LENGTH_LONG).show();
                String complete=result.getContents();
                getall(complete);
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public String getAge(int DOByear, int DOBmonth, int DOBday) {

        int age;

        final Calendar calenderToday = Calendar.getInstance();
        int currentYear = calenderToday.get(Calendar.YEAR);
        int currentMonth = 1 + calenderToday.get(Calendar.MONTH);
        int todayDay = calenderToday.get(Calendar.DAY_OF_MONTH);

        age = currentYear - DOByear;

        if(DOBmonth > currentMonth) {
            --age;
        } else if(DOBmonth == currentMonth) {
            if(DOBday > todayDay){
                --age;
            }
        }
        return String.valueOf(age);
    }
    private void getall(String complete) {
        int is=complete.indexOf("uid=");
        if(is==-1){
            Toast.makeText(this, "Please Provide proper aadhaar", Toast.LENGTH_SHORT).show();
        }else {
            String uid = complete.substring(is + 5, is + 17);
            Toast.makeText(this, uid , Toast.LENGTH_SHORT).show();
            uid.trim();
            adt.setText(uid);
            if(ad.equals(adt)){
            String name = complete.substring(is + 6, complete.indexOf("gender") - 2);
            Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
            String gender = complete.substring(complete.indexOf("gender=")+8 , complete.indexOf("yob")-2 );
            if(gender.equals("M")){gender="Male";}
            if(gender.equals("F")){gender="Female";}
            Toast.makeText(this, gender, Toast.LENGTH_SHORT).show();
            String dist = complete.substring(complete.indexOf("dist=")+6 , complete.indexOf("state")-2);
            Toast.makeText(this, dist, Toast.LENGTH_SHORT).show();
            String pc = complete.substring(complete.indexOf("pc=") + 4, complete.indexOf("dob") - 2);
            Toast.makeText(this, pc, Toast.LENGTH_SHORT).show();
            String state = complete.substring(complete.indexOf("state=") + 7, complete.indexOf("pc") - 2);
            Toast.makeText(this, state, Toast.LENGTH_SHORT).show();
            String dob = complete.substring(complete.indexOf("dob=")+5, complete.lastIndexOf("/>")-1);
            Toast.makeText(this, dob, Toast.LENGTH_SHORT).show();
            String yyyy=dob.substring(0,dob.indexOf('-'));
            String mmm=dob.substring(dob.indexOf('-'),dob.lastIndexOf('-'));
            String dd=dob.substring(dob.lastIndexOf('-'));
          //  Toast.makeText(this, yyyy+" "+mmm+" "+dd, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Age ="+getAge(Integer.parseInt(yyyy),Integer.parseInt(mmm),Integer.parseInt(dd)), Toast.LENGTH_SHORT).show();

        }else{
                Toast.makeText(this, "Sorry Please Provide proper aadhaar", Toast.LENGTH_SHORT).show();
            }}
    }
}
