package com.codefundo.vote;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.codefundo.vote.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.phonenumberui.PhoneNumberActivity;

import java.util.Calendar;
import java.util.Objects;

import static com.codefundo.vote.AuthSuccessScreen.pin;

public class ReaderActivity extends AppCompatActivity {
    private Button scan_btn,reg_vote;
    String aadhaar;
    FirebaseUser fbUser;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase,mDatabase,mDatabase2;
    private String mobileNumber = "";
    private Button btnVerify;
    EditText ad,adt;
    EditText uid1,name1,dist1,state1,pc1,age1,gender1,dob1,number,email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        mAuth = FirebaseAuth.getInstance();
        fbUser = mAuth.getCurrentUser();
        @SuppressLint("HardwareIds") final String id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID) + "@gmail.com";
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        scan_btn = (Button) findViewById(R.id.scan_btn);
        aadhaar=getIntent().getExtras().get("aadhaar").toString();
        final Activity activity = this;
        email=findViewById(R.id.email);
        uid1=findViewById(R.id.uid);
        name1=findViewById(R.id.name);
        dist1=findViewById(R.id.dist);
        state1=findViewById(R.id.state);
        pc1=findViewById(R.id.pc);
        age1=findViewById(R.id.age);
        gender1=findViewById(R.id.gender);
        dob1=findViewById(R.id.dob);
        uid1.setFocusable(false);
        uid1.setClickable(true);
        name1.setFocusable(false);
        name1.setClickable(true);
        age1.setFocusable(false);
        age1.setClickable(true);
        state1.setFocusable(false);
        state1.setClickable(true);
        dist1.setFocusable(false);
        dist1.setClickable(true);
        pc1.setFocusable(false);
        pc1.setClickable(true);
        dob1.setFocusable(false);
        dob1.setClickable(true);
        gender1.setFocusable(false);
        gender1.setClickable(true);
        uid1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Sorry You Cannot Edit this. Just Scan", Toast.LENGTH_SHORT).show();
            }
        });
        name1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Sorry You Cannot Edit this. Just Scan", Toast.LENGTH_SHORT).show();
            }
        });
        dist1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Sorry You Cannot Edit this. Just Scan", Toast.LENGTH_SHORT).show();
            }
        });
        state1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Sorry You Cannot Edit this. Just Scan", Toast.LENGTH_SHORT).show();
            }
        });
        pc1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Sorry You Cannot Edit this. Just Scan", Toast.LENGTH_SHORT).show();
            }
        });
        age1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Sorry You Cannot Edit this. Just Scan", Toast.LENGTH_SHORT).show();
            }
        });
        gender1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Sorry You Cannot Edit this. Just Scan", Toast.LENGTH_SHORT).show();
            }
        });
        dob1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Sorry You Cannot Edit this. Just Scan", Toast.LENGTH_SHORT).show();
            }
        });

        number=findViewById(R.id.number);
        reg_vote=findViewById(R.id.btn);
        reg_vote.setEnabled(false);
        number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length()==10&& !TextUtils.isEmpty(email.getText().toString())){reg_vote.setEnabled(true);}
                else{number.setError("10 digit Number");reg_vote.setEnabled(false);}
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        ad=findViewById(R.id.ad);

        ad.setText(aadhaar.trim());
        adt=findViewById(R.id.adto);

        reg_vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("email");
                    mDatabase.setValue(email.getText().toString());
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("mobile");
                    mDatabase.setValue(number.getText().toString());
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("aadhaar");
                    mDatabase.setValue(uid1.getText().toString());
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("age");
                    mDatabase.setValue(age1.getText().toString());
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("dist");
                    mDatabase.setValue(dist1.getText().toString());
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("state");
                    mDatabase.setValue(state1.getText().toString());
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("gender");
                    mDatabase.setValue(gender1.getText().toString());
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("name");
                    mDatabase.setValue(name1.getText().toString());
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("pincode");
                    mDatabase.setValue(pc1.getText().toString());
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("dob");
                    mDatabase.setValue(dob1.getText().toString());
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("ADMIN");
                    mDatabase.setValue("YES");

                }catch (Exception e ){e.printStackTrace();}


                /*try{
                //mAuth.signInWithEmailAndPassword(id,pin);
                fbUser.updateEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            fbUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(), "Verification Email Sent To Your Email.. Please Verify and Login", Toast.LENGTH_LONG).show();
                                    // Logout From Firebase
                                    /*Intent intent = new Intent(ReaderActivity.this, MainActivitymob.class);
                                    intent.putExtra("PHONE_NUMBER", number.getText().toString());
                                    startActivityForResult(intent, 1080);
                                }
                            });

                        } else {

                            try {
                                throw Objects.requireNonNull(task.getException());
                            }

                            // Invalid Email
                            catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                Toast.makeText(getApplicationContext(), "Invalid Email...", Toast.LENGTH_LONG).show();

                            }
                            // Email Already Exists
                            catch (FirebaseAuthUserCollisionException existEmail) {
                                Toast.makeText(getApplicationContext(), "Email Used By Someone Else, Please Give Another Email...", Toast.LENGTH_LONG).show();

                            }
                            // Any Other Exception
                            catch (Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                            }
                        }

                    }
                });}catch (Exception e){e.printStackTrace();}*/
                Intent intent = new Intent(ReaderActivity.this, MainActivitymob.class);
                intent.putExtra("PHONE_NUMBER", number.getText().toString());
                startActivityForResult(intent, 1080);
            }
        });


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
            String adh=adt.getText().toString();
            if(!adh.equals(aadhaar.trim())){Toast.makeText(this, "Not Matched", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Please Use only that id which you have used earlier.", Toast.LENGTH_SHORT).show();
            }

            else {
                Toast.makeText(this, "Matched", Toast.LENGTH_SHORT).show();
                reg_vote.setEnabled(true);
                is = complete.indexOf("name=");
                String name = complete.substring(is + 6, complete.indexOf("gender") - 2);
                Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
                String gender = complete.substring(complete.indexOf("gender=") + 8, complete.indexOf("yob") - 2);
                if (gender.equals("M")) {
                    gender = "Male";
                }
                if (gender.equals("F")) {
                    gender = "Female";
                }
                Toast.makeText(this, gender, Toast.LENGTH_SHORT).show();
                String dist = complete.substring(complete.indexOf("dist=") + 6, complete.indexOf("state") - 2);
                Toast.makeText(this, dist, Toast.LENGTH_SHORT).show();
                String pc = complete.substring(complete.indexOf("pc=") + 4, complete.indexOf("dob") - 2);
                Toast.makeText(this, pc, Toast.LENGTH_SHORT).show();
                String state = complete.substring(complete.indexOf("state=") + 7, complete.indexOf("pc") - 2);
                Toast.makeText(this, state, Toast.LENGTH_SHORT).show();
                String dob = complete.substring(complete.indexOf("dob=") + 5, complete.lastIndexOf("/>") - 1);
                Toast.makeText(this, dob, Toast.LENGTH_SHORT).show();
                String yyyy = dob.substring(0, dob.indexOf('-'));
                String mmm = dob.substring(dob.indexOf('-'), dob.lastIndexOf('-'));
                String dd = dob.substring(dob.lastIndexOf('-'));
                //  Toast.makeText(this, yyyy+" "+mmm+" "+dd, Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Age =" + getAge(Integer.parseInt(yyyy), Integer.parseInt(mmm), Integer.parseInt(dd)), Toast.LENGTH_SHORT).show();
                String age=getAge(Integer.parseInt(yyyy), Integer.parseInt(mmm), Integer.parseInt(dd));

                uid1.setText(uid);
                name1.setText(name);
                dist1.setText(dist);
                state1.setText(state);
                pc1.setText(pc);
                age1.setText(age);
                gender1.setText(gender);
                dob1.setText(dob);

            }
        }
    }
}
