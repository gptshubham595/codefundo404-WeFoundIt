package com.codefundo.votenew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    AppCompatButton addfam;
    RecyclerView   allfamilylist;
    String email;
    private Context mContext;
   public static int[] ch = {0};
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_CODE = 1;
    long startTime,startTime1,startTime2;
    DatabaseReference allfamdatabaseReference,mUser,totalfam;
    AppCompatTextView fam;
    AppCompatTextView counter;
    static long milliseconds = 0;
    static long milliseconds1 = 0;
    String finalstart="",finalend="";
    SwipeRefreshLayout swipe;
    AppCompatTextView day,month,year;
    private FirebaseAuth mAuth;
    private static final String TAG = MainActivity.class.getName();
    long diff=0,oldLong=0,NewLong=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        mAuth = FirebaseAuth.getInstance();
        swipe=findViewById(R.id.swipe);
        findViewById(R.id.checkresult).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(getApplicationContext(),Result.class);
                startActivity(i);
            }
        });
        email="";
        try{
            email=getIntent().getExtras().get("email").toString().toLowerCase();}catch (Exception e ){e.printStackTrace();}
        allfamilylist=(RecyclerView) findViewById(R.id.recycler);
        allfamilylist.setHasFixedSize(true);
        allfamilylist.setLayoutManager(new LinearLayoutManager(this));
        String emailpartwithout[] =email.split("@",2);
        allfamdatabaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(emailpartwithout[0]).child("familymember");
        allfamdatabaseReference.keepSynced(true);
        fam=findViewById(R.id.fam);
        Toast.makeText(this, email, Toast.LENGTH_SHORT).show();
        day=findViewById(R.id.day);
        month=findViewById(R.id.month);
        year=findViewById(R.id.year);
        /////
        Toast.makeText(MainActivity.this, ""+ getData(email,"month"), Toast.LENGTH_SHORT).show();
        Toast.makeText(MainActivity.this, ""+ getData(email,"day"), Toast.LENGTH_SHORT).show();
        Toast.makeText(MainActivity.this, ""+ getData(email,"year"), Toast.LENGTH_SHORT).show();
        Toast.makeText(MainActivity.this, ""+ getData(email,"finaltime"), Toast.LENGTH_SHORT).show();
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(MainActivity.this, ""+ getData(email,"month"), Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, ""+ getData(email,"day"), Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, ""+ getData(email,"year"), Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, ""+ getData(email,"finaltime"), Toast.LENGTH_SHORT).show();

            }
        });



        totalfam= FirebaseDatabase.getInstance().getReference().child("Users").child(emailpartwithout[0]).child("TOTAL_FAMILY_MEMBER");
        totalfam.keepSynced(true);
        totalfam.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value1 = dataSnapshot.getValue(String.class);
                Toast.makeText(MainActivity.this, value1, Toast.LENGTH_SHORT).show();
                fam.setText(value1);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

        addfam=findViewById(R.id.addfam);
        addfam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(getApplicationContext(),Regwithaadhaar.class);
                i.putExtra("email",email);
                startActivity(i);

            }
        });
    }
    private static final String[] requiredPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };
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
    /**
     * checking  permissions at Runtime.
     */
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
    private void counter(String Final)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss");
        formatter.setLenient(false);
        String endTime = Final;

        Date endDate;
        try {
            endDate = formatter.parse(endTime);
            if (endDate != null) {
                milliseconds = endDate.getTime();
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        startTime = System.currentTimeMillis();

        diff = milliseconds - startTime;


        new CountDownTimer(milliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                startTime=startTime-1;
                Long serverUptimeSeconds =
                        (millisUntilFinished - startTime) / 1000;

                String daysLeft = String.format("%d", serverUptimeSeconds / 86400);
                AppCompatTextView dayleft=findViewById(R.id.dayleft);
                dayleft.setText(daysLeft+"d");

                String hoursLeft = String.format("%d", (serverUptimeSeconds % 86400) / 3600);
                AppCompatTextView hourleft=findViewById(R.id.hourleft);
                hourleft.setText(hoursLeft+"h");

                String minutesLeft = String.format("%d", ((serverUptimeSeconds % 86400) % 3600) / 60);
                AppCompatTextView minleft=findViewById(R.id.minleft);
                minleft.setText(minutesLeft+"m");

                String secondsLeft = String.format("%d", ((serverUptimeSeconds % 86400) % 3600) % 60);
                AppCompatTextView secleft=findViewById(R.id.secleft);
                secleft.setText(secondsLeft+"s");

            }

            @Override
            public void onFinish() {

            }
        }.start();
    }



    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<allfamily, Allfamilyviewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<allfamily, Allfamilyviewholder>(
                allfamily.class,R.layout.activity_fam,Allfamilyviewholder.class,allfamdatabaseReference
        ) {
            @Override
            protected void populateViewHolder(final Allfamilyviewholder viewHolder, final allfamily model, final int position) {
                Toast.makeText(MainActivity.this, model.getName(), Toast.LENGTH_SHORT).show();
                viewHolder.setName(model.getName());
                viewHolder.setImage(model.getImage());
                viewHolder.setAadhaar(model.getAadhaar());
                viewHolder.setDob(model.getDob());
                viewHolder.setEligible(model.getEligible());
                viewHolder.setGender(model.getGender());
                viewHolder.counterdiff(model.getSlotstart(),model.getSlotend());

                final String userid=getRef(position).getKey();
                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(viewHolder.counterdiffclick(model.getSlotstart(),model.getSlotend())==1){
                            if(model.getVoted().toLowerCase().equals("no")){
                             showDialog(MainActivity.this,model.getEmail(),model.getAadhaar());}else{Toast.makeText(MainActivity.this, "Sorry YOU Already voted", Toast.LENGTH_SHORT).show();}
                        }else{
                            Toast.makeText(MainActivity.this, "Sorry", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        };
        allfamilylist.setAdapter(firebaseRecyclerAdapter);






    }


    public  String getData(String email, final String yeard){
        final String[] value = new String[1];
        String emailpartwithout[] =email.split("@",2);
        String emailfinal=emailpartwithout[0].toLowerCase();
        DatabaseReference  mUserDatabase=FirebaseDatabase.getInstance().getReference().child("DATE").child(yeard);
        mUserDatabase.keepSynced(true);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                value[0] = dataSnapshot.getValue(String.class);
                //   Toast.makeText(MainActivity.this, "Hello day="+ value[0], Toast.LENGTH_SHORT).show();
                if(yeard.equals("year"))
                    year.setText(value[0]);
                else if(yeard.equals("day"))
                    day.setText(value[0]);
                else if(yeard.equals("month"))
                    month.setText(value[0]);
                else if(yeard.equals("finaltime")) {
                    Toast.makeText(MainActivity.this, yeard + "=" + value[0], Toast.LENGTH_SHORT).show();
                     counter(value[0]);
                }

                else
                    Toast.makeText(MainActivity.this, "Sorry", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(MainActivity.this, "SORRY", Toast.LENGTH_SHORT).show();
            }
        });
        return value[0];
    }

    public static class Allfamilyviewholder extends RecyclerView.ViewHolder{
        View mview;

        String starting="1",ending="1";


        public Allfamilyviewholder(@NonNull View itemView) {
            super(itemView);
            mview=itemView;
        }
        public void setName(String famname){
            TextView nameview = (TextView) mview.findViewById(R.id.famname);
            nameview.setText(famname);
        }


        public void setGender(String gender){
            TextView genderview = (TextView) mview.findViewById(R.id.gender);
            genderview.setText(gender);
        }
        public void setAadhaar(String aadhaar) {
            TextView aadhaarview = (TextView) mview.findViewById(R.id.aadhaar);
            aadhaarview.setText(aadhaar);
        }

        public void setDob(String dob) {
            TextView dobview = (TextView) mview.findViewById(R.id.dob);
            dobview.setText(dob);
        }

        public void setEligible(String eligible) {
            TextView eligibileview = (TextView) mview.findViewById(R.id.eligible);
            eligibileview.setText(eligible);
            ImageView tick = mview.findViewById(R.id.tick);
            ImageView cancel = mview.findViewById(R.id.cross);

            if(eligible.equals("YES")){tick.setVisibility(View.VISIBLE);}
            else{cancel.setVisibility(View.VISIBLE);}
        }


        public void setImage(final String user_thumb_image){
            final CircleImageView thumb_image =  mview.findViewById(R.id.familymemberimage);
            Picasso.with(mview.getContext()).load(user_thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_person_black_24dp).into(thumb_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(mview.getContext()).load(user_thumb_image).placeholder(R.drawable.ic_person_black_24dp).into(thumb_image);
                }
            });

        }
        private void counterdiff(String Start,String end)
        {
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss");
            formatter.setLenient(false);
            String endTime = end;
            final String[] startTime = {Start};
            Long millisecondsend=null;
            final Long[] millisecondsstart = {null};

            Date endDate,startDate;
            try {
                endDate = formatter.parse(endTime);
                if (endDate != null) {
                    millisecondsend = endDate.getTime();
                }

            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                startDate = formatter.parse(startTime[0]);
                if (startDate != null) {
                    millisecondsstart[0] = startDate.getTime();
                }

            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            final Long[] startTime5 = {System.currentTimeMillis()};
            Long diff = Math.abs(millisecondsend - millisecondsstart[0]);
            Long diff2=millisecondsend-System.currentTimeMillis();
            Toast.makeText(mview.getContext(), "START="+millisecondsstart[0], Toast.LENGTH_SHORT).show();
            Toast.makeText(mview.getContext(), "SYS="+System.currentTimeMillis(), Toast.LENGTH_SHORT).show();
            Toast.makeText(mview.getContext(), "START="+millisecondsstart[0]+"SYS="+System.currentTimeMillis()+"END="+millisecondsend, Toast.LENGTH_SHORT).show();

            Log.i(TAG,"START="+millisecondsstart[0]);
            Log.i(TAG,"SYS="+System.currentTimeMillis());
            Log.i(TAG,"END="+millisecondsend);
            Long diffcurr=   millisecondsstart[0]-System.currentTimeMillis();
            Long diffcurr2=   millisecondsend-System.currentTimeMillis();

            if(millisecondsstart[0]>=System.currentTimeMillis() && System.currentTimeMillis()<=millisecondsend){
                Toast.makeText(mview.getContext(), "YES NOW VOTE", Toast.LENGTH_SHORT).show();
                new CountDownTimer(millisecondsend, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                        startTime5[0] = startTime5[0] -1;
                        Long serverUptimeSeconds =
                                Math.abs(millisUntilFinished - startTime5[0]) / 1000;

                        String daysLeft = String.format("%d", serverUptimeSeconds / 86400);
                        //AppCompatTextView dayleft=mview.findViewById(R.id.dayleft);
                        //dayleft.setText(daysLeft+"d");

                        String hoursLeft = String.format("%d", (serverUptimeSeconds % 86400) / 3600);
                        //AppCompatTextView hourleft=mview.findViewById(R.id.hourleft);
                        //hourleft.setText(hoursLeft+"h");

                        String minutesLeft = String.format("%d", ((serverUptimeSeconds % 86400) % 3600) / 60);
                        AppCompatTextView minleft=mview.findViewById(R.id.minleft);
                        minleft.setText(minutesLeft);

                        String secondsLeft = String.format("%d", ((serverUptimeSeconds % 86400) % 3600) % 60);
                        AppCompatTextView secleft=mview.findViewById(R.id.secleft);
                        secleft.setText(secondsLeft);

                        if(minleft.getText().toString().trim().equals("0")&&secleft.getText().toString().trim().equals("0")) {
                            onFinish();
                        }   if(!minleft.getText().toString().trim().equals("XX"))
                            if(Integer.parseInt(minleft.getText().toString().trim())>35){
                                onFinish();
                            }



                    }

                    @Override
                    public void onFinish() {
                        AppCompatTextView minleft=mview.findViewById(R.id.minleft);
                        AppCompatTextView secleft=mview.findViewById(R.id.secleft);
                        minleft.setText(" XX ");
                        secleft.setText(" XX ");

                    }
                }.start();
            }
            if(System.currentTimeMillis()>millisecondsend){
                Toast.makeText(mview.getContext(), "Sorry WAIT for your turn", Toast.LENGTH_SHORT).show();
                AppCompatTextView minleft=mview.findViewById(R.id.minleft);
                minleft.setText(" XX ");
                AppCompatTextView time=mview.findViewById(R.id.time);
                time.setText("WAIT");
                AppCompatTextView secleft=mview.findViewById(R.id.secleft);
                secleft.setText(" XX ");

            }if(millisecondsstart[0]<System.currentTimeMillis()){
            Toast.makeText(mview.getContext(), "Sorry your turn is OVER", Toast.LENGTH_SHORT).show();
            AppCompatTextView minleft=mview.findViewById(R.id.minleft);
            minleft.setText(" XX ");
            AppCompatTextView time=mview.findViewById(R.id.time);
            time.setText("OVER");
            AppCompatTextView secleft=mview.findViewById(R.id.secleft);
            secleft.setText(" XX ");
        }
            

            
        }

        private int counterdiffclick(String Start,String end)
        {
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss");
            formatter.setLenient(false);
            String endTime = end;
            final String[] startTime = {Start};
            Long millisecondsend=null;
            final Long[] millisecondsstart = {null};

            Date endDate,startDate;
            try {
                endDate = formatter.parse(endTime);
                if (endDate != null) {
                    millisecondsend = endDate.getTime();
                }

            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                startDate = formatter.parse(startTime[0]);
                if (startDate != null) {
                    millisecondsstart[0] = startDate.getTime();
                }

            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Long diff = millisecondsend - millisecondsstart[0];

            Long diffcurr=   millisecondsstart[0]-System.currentTimeMillis();
            Long diffcurr2=   millisecondsend-System.currentTimeMillis();

            if(millisecondsstart[0]>=System.currentTimeMillis() && System.currentTimeMillis()<=millisecondsend){
                Toast.makeText(mview.getContext(), "YES NOW VOTE", Toast.LENGTH_SHORT).show();
                AppCompatTextView minleft=mview.findViewById(R.id.minleft);
                AppCompatTextView time=mview.findViewById(R.id.time);
                AppCompatTextView secleft=mview.findViewById(R.id.secleft);

                if(minleft.getText().toString().trim().equals("0")&&secleft.getText().toString().trim().equals("0")) {
                return 0;
                }
                if(!minleft.getText().toString().trim().equals("XX"))
                    if(Integer.parseInt(minleft.getText().toString().trim())>35){
                return 0 ;
                    }


                return 1;
            }
            if(System.currentTimeMillis()>millisecondsend){
                Toast.makeText(mview.getContext(), "Sorry WAIT for your turn", Toast.LENGTH_SHORT).show();
                AppCompatTextView minleft=mview.findViewById(R.id.minleft);
                minleft.setText(" XX ");
                AppCompatTextView time=mview.findViewById(R.id.time);
                time.setText("WAIT");
                AppCompatTextView secleft=mview.findViewById(R.id.secleft);
                secleft.setText(" XX ");
                return 0;
            }if(millisecondsstart[0]<System.currentTimeMillis()){
            Toast.makeText(mview.getContext(), "Sorry your turn is OVER", Toast.LENGTH_SHORT).show();
            AppCompatTextView minleft=mview.findViewById(R.id.minleft);
            minleft.setText(" XX ");
            AppCompatTextView time=mview.findViewById(R.id.time);
            time.setText("OVER");
            AppCompatTextView secleft=mview.findViewById(R.id.secleft);
            secleft.setText(" XX ");
            return 0;}


            return 0;
        }



    }

    public int check(String email, String aadhaar){

        String emailpartwithout[] =email.split("@",2);
        DatabaseReference check=FirebaseDatabase.getInstance().getReference().child("Users").child(emailpartwithout[0]).child("familymember").child(aadhaar).child("voted");
        check.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                if(value.toLowerCase().equals("no")){
                    ch[0] =0;
                }else{ch[0]=1;}
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
        return ch[0];
    }
    public void showDialog2(Activity activity, final String aadhaar) {
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
                deleteit(aadhaar);
            }
        });


        dialog.show();
    }
    public void showDialog(Activity activity, String email, final String aadhaar) {


        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.newcustom_layout2);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button delete = dialog.findViewById(R.id.delete);
        TextView ad=dialog.findViewById(R.id.ad);
        ad.setText(aadhaar);
        ImageView cancel = dialog.findViewById(R.id.cancel_action);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                showDialog2(MainActivity.this,aadhaar);

            }
        });
        Button vote = dialog.findViewById(R.id.vote);
        vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                voteit(aadhaar, MainActivity.this.email);
            }
        });

        if(ch[0]==0)
        dialog.show();
        else{
            Toast.makeText(activity, "SORRY You already voted", Toast.LENGTH_SHORT).show();
        }
    }

    private void voteit(String aadhaar,String email) {
        Intent i =new Intent(getApplicationContext(), EnterPintoVote.class);
        i.putExtra("aadhaar",aadhaar);
        i.putExtra("email",email);
        startActivity(i);
    }

    private void deleteit(String aadhaar) {
        mUser= FirebaseDatabase.getInstance().getReference().child("Users").child(email).child("familymember").child(aadhaar);
        mUser.keepSynced(true);
        mUser.removeValue();
        String current_user_id = mAuth.getCurrentUser().getUid();
        mUser= FirebaseDatabase.getInstance().getReference().child("voters").child(current_user_id);
        mUser.keepSynced(true);
        mUser.removeValue();
    }
}
