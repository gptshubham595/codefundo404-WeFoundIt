package com.codefundo.votenew;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.multidots.fingerprintauth.AuthErrorCodes;
import com.multidots.fingerprintauth.FingerPrintAuthCallback;
import com.multidots.fingerprintauth.FingerPrintAuthHelper;
import com.multidots.fingerprintauth.FingerPrintUtils;
import com.scottyab.rootbeer.RootBeer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import maes.tech.intentanim.CustomIntent;

import static java.lang.Thread.sleep;

public class FingerActivity extends AppCompatActivity implements FingerPrintAuthCallback {
    private static final String[] requiredPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
    };
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_CODE = 1;

    private ImageView img;
    private TextView mAuthMsgTv;
    private ViewSwitcher mSwitcher;
    private Button mGoToSettingsBtn,submit;
    private FingerPrintAuthHelper mFingerPrintAuthHelper;
    private FirebaseAuth mAuth;
    SharedPreferences prefs=null;
    Boolean isFirstRun=false;
    String first="false";
    private SharedPreferences prefs1=null,setornot=null;
    private long timeLeft,pinis;
    private CountDownTimer timer;
    TextView _tv;
    static int count=0,count2=0,count3=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger);
        mAuth = FirebaseAuth.getInstance();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        prefs= getSharedPreferences("com.codefundo.votenew", MODE_PRIVATE);
        int adb = Settings.Secure.getInt(getApplicationContext().getContentResolver(), Settings.Secure.ADB_ENABLED, 0);
        RootBeer rootBeer = new RootBeer(getApplicationContext());
       if( rootBeer.isRootedWithoutBusyBoxCheck() || rootBeer.isRooted()){
           Toast.makeText(this, "Sorry We Can't Run in a rooted Device", Toast.LENGTH_SHORT).show();
           ExitActivity.exitApplication(getApplicationContext());       }
        if( adb==1){
            Toast.makeText(this, "Sorry We Can't Run here!! Disable USB Debugging Mode", Toast.LENGTH_SHORT).show();
            //ExitActivity.exitApplication(getApplicationContext());
        }
        /*
        CheckLogin checkLogin=new CheckLogin();
        checkLogin.execute("");
        /////*/
        isFirstRun=prefs.getBoolean("isFirstRun", true);
        if(isFirstRun){
            Toast.makeText(this, "First Time User", Toast.LENGTH_SHORT).show();

        }
        first=isFirstRun.toString();
        //    startActivity(new Intent(getApplicationContext(),MainActivitymob.class));
        _tv = findViewById(R.id.counter);
        img = findViewById(R.id.tick);
        mGoToSettingsBtn = findViewById(R.id.go_to_settings_btn);
        mGoToSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FingerPrintUtils.openSecuritySettings(FingerActivity.this);
            }
        });

        mSwitcher = findViewById(R.id.main_switcher);
        mAuthMsgTv = findViewById(R.id.auth_message_tv);
        initTasks();

        checkTimer();
        mFingerPrintAuthHelper = FingerPrintAuthHelper.getHelper(this, this);

    }
    ////==================
    private void initTasks() {
        // logInButton = (Button) findViewById(R.id.bt);

        prefs1 = getSharedPreferences("file", Context.MODE_PRIVATE);
//   setornot = getSharedPreferences("setornot", Context.MODE_PRIVATE);

    }

    private void checkTimer() {

        if (prefs.contains("time"))
            setTimer();
        else {
            SharedPreferences.Editor editor = prefs1.edit();
            editor.putLong("time", -1L);
            editor.apply();
        }
    }

    private void setTimer() {
        timeLeft = prefs1.getLong("time", -1L);
        if (timeLeft != -1L)
        {startTimer(timeLeft);
            _tv.setText(timeLeft+"");}
        else
            mFingerPrintAuthHelper.startAuth();

    }

    private void startTimer(long time) {
        // logInButton.setEnabled(false);
        mFingerPrintAuthHelper.stopAuth();
        timer = new CountDownTimer(time, 1000) {

            @Override
            public void onFinish() {
                //logInButton.setEnabled(true);
                saveToPref(-1L);
                _tv.setText("Try Now");
                mFingerPrintAuthHelper.startAuth();
            }

            @Override
            public void onTick(long millisUntilFinished) {
                //update UI, if required
                timeLeft = millisUntilFinished;
                saveToPref(timeLeft);
                _tv.setText(timeLeft+"");
            }
        };
    }

    private void saveToPref(long timeLeft){
        SharedPreferences.Editor editor = prefs1.edit();
        editor.putLong("time", timeLeft);
        _tv.setText(timeLeft+"");
        editor.apply();
    }
////==================

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
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


    @Override
    protected void onResume() {
        super.onResume();
        mGoToSettingsBtn.setVisibility(View.GONE);
        if (prefs.getBoolean("isFirstRun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean("isFirstRun", false).commit();
        }
        initTasks();

        checkTimer();
        mAuthMsgTv.setText("Scan your finger");

        //start finger print authentication
        mFingerPrintAuthHelper.startAuth();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFingerPrintAuthHelper.stopAuth();

    }

    @Override
    protected void onStop() {
        super.onStop();
        saveToPref(timeLeft);
           //  ExitActivity.exitApplication(getApplicationContext());

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveToPref(timeLeft);
       // ExitActivity.exitApplication(getApplicationContext());

    }

    @Override
    public void onNoFingerPrintHardwareFound() {
        mAuthMsgTv.setText("Your device does not have finger print scanner.SORRY!");
        Toast.makeText(this, "Your device does not have finger print scanner.SORRY!", Toast.LENGTH_SHORT).show();
        //mAuthMsgTv.setText("Your device does not have finger print scanner. Please type 1234 to authenticate.");
        //mSwitcher.showNext();
        //goIntent(AuthSuccessScreen.class,AuthSuccessScreen.class);
        String action;
        getinntent(ChoicePortal.class);
    }


    @Override
    public void onNoFingerPrintRegistered() {
        mAuthMsgTv.setText("There are no finger prints registered on this device. Please register your finger from settings.");
        mGoToSettingsBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBelowMarshmallow() {
        mAuthMsgTv.setText("You are running older version of android that does not support finger print authentication. Please type 1234 to authenticate.");
        //   mSwitcher.showNext();
        Toast.makeText(this, "Your device does not have finger print scanner.SORRY!", Toast.LENGTH_SHORT).show();
        getinntent(ChoicePortal.class);
    }

    @Override
    public void onAuthSuccess(FingerprintManager.CryptoObject cryptoObject) {
        Toast.makeText(FingerActivity.this, "Authentication succeeded.", Toast.LENGTH_SHORT).show();
        try {
            sleep(70);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        img.setVisibility(View.VISIBLE);

        try {
            sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Toast.makeText(FingerActivity.this, "Success !!", Toast.LENGTH_SHORT).show();
        //goIntent(AuthSuccessScreen.class,TakeMobile.class);
        getinntent(ChoicePortal.class);
    }







    @Override
    public void onAuthFailed(int errorCode, String errorMessage) {
        switch (errorCode) {
            case AuthErrorCodes.CANNOT_RECOGNIZE_ERROR:
                count++;
                Toast.makeText(this, "You Have MAX 5 ATTEMPTS ", Toast.LENGTH_SHORT).show();
                mAuthMsgTv.setText("Cannot recognize your finger print.Attempt = "+count);
                if (count == 5) {
                    mAuthMsgTv.setText("Sorry Please Try after 2 mins !.You Exceeded 5 times.\n");

                    startTimer(120000);
                    mFingerPrintAuthHelper.stopAuth();
                    new CountDownTimer(120000, 1000) { // adjust the milli seconds here

                        public void onTick(long millisUntilFinished) {

                            _tv.setText("" + String.format("%d min, %d sec",
                                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                        }

                        public void onFinish() {
                            _tv.setText("Try Now!!");
                            mFingerPrintAuthHelper.startAuth();
                        }
                    }.start();
                    timeLeft = prefs1.getLong("time", -1L);
                    if (timeLeft != -1L)
                    {startTimer(timeLeft);
                        _tv.setText(timeLeft+"");}
                    else
                        mFingerPrintAuthHelper.startAuth();

                }if(count==10){
                //goIntent(AuthSuccessScreen.class,TakeMobile.class);
                getinntent(ChoicePortal.class);
            }
                break;
            case AuthErrorCodes.NON_RECOVERABLE_ERROR:
                mAuthMsgTv.setText("Cannot initialize finger print authentication. .Sorry Please Try after 2 mins !.You Exceeded 5 times.");
                timeLeft = prefs1.getLong("time", -1L);
                if (timeLeft != -1L)
                {startTimer(timeLeft);
                    _tv.setText(timeLeft+"");}
                else
                    mFingerPrintAuthHelper.startAuth();

                //mSwitcher.showNext();
                break;
            case AuthErrorCodes.RECOVERABLE_ERROR:
                mAuthMsgTv.setText(errorMessage);

                break;
        }
    }

    /*public Connection connectionclass() {
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);
    Connection connection = null;
    String connectionURL = null;
    try {
        Class.forName("net.sourceforge.jtds.jdbc.Driver");
        connectionURL = "jdbc:jtds:sqlserver://db-kvse4w-vot.database.windows.net:1433;DatabaseName=kvse4w-vot;user=dbadmin@db-kvse4w-vot;password=Shivamclass12$#;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
        connection = DriverManager.getConnection(connectionURL);
    } catch (SQLException se) {
        se.printStackTrace();
    } catch (ClassNotFoundException ce) {
        ce.printStackTrace();
    } catch (Exception e) {
        e.printStackTrace();
    }
return connection;
    }*/

    /*public class CheckLogin extends AsyncTask<String,String,String> {
        String z="";
        Boolean isSuccess=true;
        String name1="";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            if(isSuccess){
                Toast.makeText(MainActivity.this, name1, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                Connection con = connectionclass();
                if(con==null){z="Check Internet";}
                else{
                    String query="select * from Users";
                    Statement stmt=con.createStatement();
                    ResultSet rs=stmt.executeQuery(query);
                    if(rs.next()){name1=rs.getString("Username");
                    z="query successful";
                    isSuccess=true;
                    con.close();
                    }else{
                        z="invalid query";
                        isSuccess=false;
                    }
                }
            }catch (Exception e){isSuccess=false;z=e.getMessage(); e.printStackTrace();}
            return z;
        }
    }*/
    private void getinntent(Class<ChoicePortal> authSuccessScreenClass) {
        Intent i =new Intent(getApplicationContext(),authSuccessScreenClass);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //  i.putExtra("first",first);
        startActivity(i);
        CustomIntent.customType(FingerActivity.this,"fadein-to-fadeout");


    }
/*    private void goIntent(Class<AuthSuccessScreen> authSuccessScreenClass, Class<TakeMobile> takeMobileClass) {
        Intent i =new Intent(getApplicationContext(),takeMobileClass);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("first",first);
        startActivity(i);
        if(first.equals("true"))
        {
    }else{
            Intent i =new Intent(getApplicationContext(),authSuccessScreenClass);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.putExtra("first",first);
            startActivity(i);
        }*/

}
