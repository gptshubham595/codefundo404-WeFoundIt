package com.codefundo.votenew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import java.util.Arrays;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    AppCompatButton addfam;
    RecyclerView   allfamilylist;
    String email;
    private Context mContext;
   public static int[] ch = {0};

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
    long diff=0,oldLong=0,NewLong=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        mAuth = FirebaseAuth.getInstance();
        swipe=findViewById(R.id.swipe);

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
    private String countermin(String Final)
    {
        final String[] minutesLeft = new String[1];
        final String[] secondsLeft = new String[1];
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

                 minutesLeft[0] = String.format("%d", ((serverUptimeSeconds % 86400) % 3600) / 60);

                 secondsLeft[0] = String.format("%d", ((serverUptimeSeconds % 86400) % 3600) % 60);

            }

            @Override
            public void onFinish() {

            }
        }.start();
        return minutesLeft[0];
    }
    private String countersec(String Final)
    {
        final String[] minutesLeft = new String[1];
        final String[] secondsLeft = new String[1];
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

                minutesLeft[0] = String.format("%d", ((serverUptimeSeconds % 86400) % 3600) / 60);

                secondsLeft[0] = String.format("%d", ((serverUptimeSeconds % 86400) % 3600) % 60);

            }

            @Override
            public void onFinish() {

            }
        }.start();
        return secondsLeft[0];
    }

    public String counter3f(String start,String end)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss");
        formatter.setLenient(false);


        Date endDate;
        try {
            endDate = formatter.parse(end);
            milliseconds = endDate.getTime();

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Date startDate;
        try {
            startDate = formatter.parse(start);
            milliseconds1 = startDate.getTime();

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        startTime1 = milliseconds1;
        startTime2 = System.currentTimeMillis();

        diff = milliseconds - startTime1;
       final long diff2=startTime2-startTime1;
        final long diff3=startTime2-milliseconds;

        new CountDownTimer(milliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                startTime=startTime-1;
                Long serverUptimeSeconds1 =
                        (millisUntilFinished - startTime) / 1000;
                String minutesLeft = String.format("%d", ((serverUptimeSeconds1 % 86400) % 3600) / 60);

                String secondsLeft = String.format("%d", ((serverUptimeSeconds1 % 86400) % 3600) % 60);
                finalstart=minutesLeft;
                finalend=secondsLeft;
                //Toast.makeText(MainActivity.this, "1="+diff+" 2="+diff2+" 3="+diff3+"MIN"+Math.abs(Integer.parseInt(finalstart))+"Sec"+Math.abs(Integer.parseInt(finalend)), Toast.LENGTH_SHORT).show();


                //finalstart="0";finalend="0";
                if(diff3>0){

                    //AppCompatTextView dayleft=findViewById(R.id.dayleft);
                    //id.time
                    //minleft,secleft,sep =gone
                //    time.setText("SORRY TIME IS OVER");
                }
                if(diff2<=0){
                    //AppCompatTextView dayleft=findViewById(R.id.dayleft);
                    //id.time
                    //minleft,secleft,sep =gone
                    //    time.setText("WAIT");
                }
                if(diff2>=0 && diff3<=0){
                    //AppCompatTextView dayleft=findViewById(R.id.dayleft);
                    //id.time
                    //minleft=minutesLeft,secleft=secondsLeft,sep =Visible
                    //    counter
                    finalstart=minutesLeft;
                    finalend=secondsLeft;
                    Toast.makeText(MainActivity.this, finalstart, Toast.LENGTH_SHORT).show();
                 //   Toast.makeText(MainActivity.this, finalend, Toast.LENGTH_SHORT).show();
                }



            }

            @Override
            public void onFinish() {

            }
        }.start();

        return finalstart;
    }
    public String counter3s(String start,String end)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss");
        formatter.setLenient(false);


        Date endDate;
        try {
            endDate = formatter.parse(end);
            milliseconds = endDate.getTime();

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Date startDate;
        try {
            startDate = formatter.parse(start);
            milliseconds1 = startDate.getTime();

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        startTime1 = milliseconds1;
        startTime2 = System.currentTimeMillis();

        diff = milliseconds - startTime1;
        final long diff2=startTime2-startTime1;
        final long diff3=startTime2-milliseconds;

        new CountDownTimer(milliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                startTime=startTime-1;
                Long serverUptimeSeconds1 =
                        (millisUntilFinished - startTime) / 1000;
                String minutesLeft = String.format("%d", ((serverUptimeSeconds1 % 86400) % 3600) / 60);

                String secondsLeft = String.format("%d", ((serverUptimeSeconds1 % 86400) % 3600) % 60);
                finalstart=minutesLeft;
                finalend=secondsLeft;
             //   Toast.makeText(MainActivity.this, "1="+diff+" 2="+diff2+" 3="+diff3+"MIN"+finalstart+"Sec"+finalend, Toast.LENGTH_SHORT).show();
                finalstart=minutesLeft;
                finalend=secondsLeft;

                //finalstart="0";finalend="0";
                if(diff3>0){

                    //AppCompatTextView dayleft=findViewById(R.id.dayleft);
                    //id.time
                    //minleft,secleft,sep =gone
                 //     time.setText("SORRY TIME IS OVER");
                }
                if(diff2<=0){
                    //AppCompatTextView dayleft=findViewById(R.id.dayleft);
                    //id.time
                    //minleft,secleft,sep =gone
                    //    time.setText("WAIT");
                }
                if(diff2>=0 && diff3<=0){
                    //AppCompatTextView dayleft=findViewById(R.id.dayleft);
                    //id.time
                    //minleft=minutesLeft,secleft=secondsLeft,sep =Visible
                    //    counter
                    finalstart=minutesLeft;
                    finalend=secondsLeft;
                //    Toast.makeText(MainActivity.this, finalstart, Toast.LENGTH_SHORT).show();
                  //  Toast.makeText(MainActivity.this, finalend, Toast.LENGTH_SHORT).show();
                }



            }

            @Override
            public void onFinish() {

            }
        }.start();
          return finalend;
    }
    public String[] counter3t(String start, String end)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss");
        formatter.setLenient(false);


        Date endDate;
        try {
            endDate = formatter.parse(end);
            milliseconds = endDate.getTime();

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Date startDate;
        try {
            startDate = formatter.parse(start);
            milliseconds1 = startDate.getTime();

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final String[] time = {""};
        startTime1 = milliseconds1;
        startTime2 = System.currentTimeMillis();

        diff = milliseconds - startTime1;
        final long diff2=startTime2-startTime1;
        final long diff3=startTime2-milliseconds;

        new CountDownTimer(milliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                startTime=startTime-1;
                Long serverUptimeSeconds1 =
                        (millisUntilFinished - startTime) / 1000;
                String minutesLeft = String.format("%d", ((serverUptimeSeconds1 % 86400) % 3600) / 60);

                String secondsLeft = String.format("%d", ((serverUptimeSeconds1 % 86400) % 3600) % 60);
                finalstart=minutesLeft;
                finalend=secondsLeft;
                //Toast.makeText(MainActivity.this, "1="+diff+" 2="+diff2+" 3="+diff3+"MIN"+finalstart+"Sec"+finalend, Toast.LENGTH_SHORT).show();
                finalstart=minutesLeft;
                finalend=secondsLeft;

                //
                //       finalstart="0";finalend="0";
                if(diff3>0){

                    //AppCompatTextView dayleft=findViewById(R.id.dayleft);
                    //id.time
                    //minleft,secleft,sep =gone
                        time[0] ="OVER";
                }
                if(diff2<=0){
                    //AppCompatTextView dayleft=findViewById(R.id.dayleft);
                    //id.time
                    //minleft,secleft,sep =gone
                    //    time.setText("WAIT");
                    time[0] ="WAIT";
                }
                if(diff2>=0 && diff3<=0){
                    //AppCompatTextView dayleft=findViewById(R.id.dayleft);
                    //id.time
                    //minleft=minutesLeft,secleft=secondsLeft,sep =Visible
                    //    counter
                    finalstart=minutesLeft;
                    finalend=secondsLeft;
                    Toast.makeText(MainActivity.this, finalstart, Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, finalend, Toast.LENGTH_SHORT).show();
                    time[0]="TIME lEFT";
                }



            }

            @Override
            public void onFinish() {

            }
        }.start();
        return time;

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
//                final String day=counter3d(model.getSlotstart(),model.getSlotend());
  //              final String hour=counter3h(model.getSlotstart(),model.getSlotend());
                final String min=counter3f(model.getSlotstart(),model.getSlotend());
                final String sec=counter3s(model.getSlotstart(),model.getSlotend());
                final String t[]=counter3t(model.getSlotstart(),model.getSlotend());
                viewHolder.setthis(min,sec,t[0]);
                String str = Arrays.toString(new String[]{t[0]});
                str=str.replaceAll("\\[", "").replaceAll("\\]","");
                if(str.equals("WAIT") || str.equals("OVER") ) {
                    viewHolder.setx(str);
                   // Toast.makeText(MainActivity.this, "SORRY wait or its over", Toast.LENGTH_SHORT).show();
                }
                viewHolder.setthis(min,sec,t[0]);
                viewHolder.setthis(min,sec,t[0]);


                //final Long minstart=Long.parseLong(countermin(model.getSlotend()))-Long.parseLong(countermin(model.getSlotstart()));
                //final Long currentsecstart=Long.parseLong(countersec(model.getSlotend()));
                //final Long currentsecend=Long.parseLong(countersec(model.getSlotstart()));
                //final Long secstart=Long.parseLong(countersec(model.getSlotend()))-Long.parseLong(countersec(model.getSlotstart()));
                //final String min=counter3f(model.getSlotstart(),model.getSlotend());
                //final String sec=counter3s(model.getSlotstart(),model.getSlotend());
                //final String t[]=counter3t(model.getSlotstart(),model.getSlotend());
                //viewHolder.setthis(minstart,secstart,currentsecstart,currentsecend);
            // Toast.makeText(MainActivity.this, min + " " + sec, Toast.LENGTH_SHORT).show();
                viewHolder.setSlotend(counter3f(model.getSlotstart(),model.getSlotend()),counter3s(model.getSlotstart(),model.getSlotend()),counter3t(model.getSlotstart(),model.getSlotend()));
                Toast.makeText(MainActivity.this, "ENDS="+model.getSlotend(), Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, model.getName()+model.getSlotend(), Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, countermin(model.getSlotend()), Toast.LENGTH_SHORT).show();
                final String userid=getRef(position).getKey();
                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        viewHolder.setthis(min,sec,t[0]);
                        String str = Arrays.toString(new String[]{t[0]});
                        str=str.replaceAll("\\[", "").replaceAll("\\]","");
                        if(str.equals("WAIT") || str.equals("OVER") ) {
                            viewHolder.setx(str);
                            Toast.makeText(MainActivity.this, "SORRY wait or its over", Toast.LENGTH_SHORT).show();
                        }
                        else{Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
                            showDialog(MainActivity.this,model.getEmail(),model.getAadhaar());}


                        showDialog(MainActivity.this,model.getEmail(),model.getAadhaar());
                      //  showDialog(MainActivity.this,model.getEmail(),model.getAadhaar(),minstart,secstart);
                      //  viewHolder.setthis(minstart,secstart,currentsecstart,currentsecend);

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
        public void setSlotstart(String famname){
            //TextView nameview = (TextView) mview.findViewById(R.id.famname);
            //nameview.setText(famname);
            starting=famname;
        }
        public void setSlotend(String min, String sec, String[] tm){
            Toast.makeText(mview.getContext(), "SETTING  GOT MIN="+min, Toast.LENGTH_SHORT).show();
            AppCompatTextView minleft =  mview.findViewById(R.id.minleft);
            AppCompatTextView secleft =  mview.findViewById(R.id.secleft);
            AppCompatTextView time =  mview.findViewById(R.id.time);

            minleft.setText(min);
            secleft.setText(sec);
            time.setText(Arrays.toString(tm));

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

        public void setthis(String min, String sec, String s) {
            Toast.makeText(mview.getContext(), "SETTING  GOT MIN="+min, Toast.LENGTH_SHORT).show();
            AppCompatTextView minleft =  mview.findViewById(R.id.minleft);
            AppCompatTextView secleft =  mview.findViewById(R.id.secleft);
            AppCompatTextView time =  mview.findViewById(R.id.time);


            String str = Arrays.toString(new String[]{s});
            str=str.replaceAll("\\[", "").replaceAll("\\]","");
            if(!str.equals("WAIT") || !str.equals("OVER") )
            {minleft.setText(min);

                secleft.setText(sec);}
            else{
                minleft.setText("XX");
                secleft.setText("XX");
            }
            time.setText(str);

        }

        public void setx(String str) {
            AppCompatTextView minleft =  mview.findViewById(R.id.minleft);
            AppCompatTextView secleft =  mview.findViewById(R.id.secleft);
            AppCompatTextView time =  mview.findViewById(R.id.time);
            minleft.setText("XX");
            time.setText(str);
            secleft.setText("XX");
        }

/*        public void setthis(Long min1, Long sec1, Long currentsecend, Long  currentsecstart) {
            Toast.makeText(mview.getContext(), "SETTING  GOT MIN="+min1, Toast.LENGTH_SHORT).show();
            String min=min1+"";
            String sec=sec1+"";
            //final Long currentsecstart=countersec(model.getSlotend());
            //final Long currentsecend=countersec(model.getSlotstart());
            AppCompatTextView minleft =  mview.findViewById(R.id.minleft);
            AppCompatTextView secleft =  mview.findViewById(R.id.secleft);
            AppCompatTextView time =  mview.findViewById(R.id.time);
            if(currentsecstart<0){time.setText("OVER");minleft.setText("XX");
                secleft.setText("XX");}
            if(currentsecend<0){time.setText("WAIT");minleft.setText("XX");
                secleft.setText("XX");}
            if(min1<0){
                minleft.setText("XX");
                time.setText("OVER");
                secleft.setText("XX");}
            if(min1>0 && sec1>0) {
                minleft.setText(min);
                secleft.setText(sec);
                time.setText("TIME LEFT");
            }



        }*/

    }
    public void check(String email, String aadhaar){

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
        check(email,aadhaar);




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
