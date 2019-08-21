package com.codefundo.votenew;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.codefundo.votenew.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


public class ReaderActivity extends AppCompatActivity {

    private ProgressDialog mLoginProgress;

    private FirebaseAuth mAuth;

    private DatabaseReference mUserDatabase,mDatabase;
    private Uri mImageUri = null;

    private static final  int GALLERY_REQUEST =1;

    private static final int CAMERA_REQUEST_CODE=1;

    private StorageReference mStorage;
    private Button scan_btn,reg_vote;
    String aadhaar,emailfinal;
    FirebaseUser fbUser;
    private String mobileNumber = "";
    private Button btnVerify;
    EditText ad,adt;
    EditText uid1,name1,dist1,state1,pc1,age1,gender1,dob1,number,email,pin;
    static int count=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        mAuth = FirebaseAuth.getInstance();
        mLoginProgress = new ProgressDialog(this,R.style.dialog);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabase.keepSynced(true);
        fbUser = mAuth.getCurrentUser();
        @SuppressLint("HardwareIds") final String id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID) + "@gmail.com";
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabase.keepSynced(true);
        mStorage = FirebaseStorage.getInstance().getReference();


        scan_btn = (Button) findViewById(R.id.scan_btn);
        aadhaar=getIntent().getExtras().get("aadhaar").toString();
        emailfinal=getIntent().getExtras().get("email").toString().toLowerCase();
        final Activity activity = this;
        email=findViewById(R.id.email);
        pin=findViewById(R.id.pin);
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




        ad=findViewById(R.id.ad);

        ad.setText(aadhaar.trim());
        adt=findViewById(R.id.adto);

        reg_vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(number.getText().toString().length()==10 && !TextUtils.isEmpty(email.getText().toString())&&!TextUtils.isEmpty(pin.getText().toString())){

                try{

                    senddatatosnap(uid1.getText().toString(),name1.getText().toString(),state1.getText().toString(),pc1.getText().toString(),dist1.getText().toString(),age1.getText().toString(),gender1.getText().toString(),dob1.getText().toString(),number.getText().toString());
                    //adddatatobase(uid1.getText().toString(),name1.getText().toString(),state1.getText().toString(),pc1.getText().toString(),dist1.getText().toString(),age1.getText().toString(),gender1.getText().toString(),dob1.getText().toString(),number.getText().toString());
                }catch (Exception e ){e.printStackTrace();}


                         }if(number.getText().toString().length()!=10){number.setError("Enter correct number");}
                if(TextUtils.isEmpty(pin.getText().toString())){pin.setError("Enter 4 digit pin");}}
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

//                adddatatobase(uid,name,state,pc,dist,age,gender,dob);


            }

        }
    }
    private void senddatatosnap(String uid,String name, String state, String pc, String dist, String age, String gender, String dob,String mobile) {
        Intent i=new Intent(getApplicationContext(),SnapActivity.class);
        i.putExtra("name", name);
        i.putExtra("state", state);
        i.putExtra("pin", pc);
        i.putExtra("district", dist);
        i.putExtra("age", age);
        i.putExtra("gender", gender);
        i.putExtra("dob", dob);
        i.putExtra("email", emailfinal);
        i.putExtra("mobile", mobile);
        i.putExtra("aadhaar", uid);
        i.putExtra("email", email.getText().toString().trim());
        i.putExtra("pin", pin.getText().toString().trim());
        startActivity(i);
    }



}
