package com.codefundo.votenew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.codefundo.votenew.activities.MainActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.lang.Thread.sleep;

public class VOTEFINAL extends AppCompatActivity {
    private FirebaseAuth mAuth;
    DatabaseReference allpoliticalparty;
    RecyclerView allfamilylist;
    String email="";
    String aadhaar="";
    static int time=0;
     int votes=0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votefinal);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        allfamilylist=(RecyclerView) findViewById(R.id.recycler);
        allfamilylist.setHasFixedSize(true);
        allfamilylist.setLayoutManager(new LinearLayoutManager(this));

        try{
            email=getIntent().getExtras().get("email").toString().toLowerCase();
            aadhaar=getIntent().getExtras().get("aadhaar").toString();
        }catch (Exception e ){e.printStackTrace();}

        mAuth = FirebaseAuth.getInstance();
        String emailpartwithout[] =email.split("@",2);
        //allpoliticalparty= FirebaseDatabase.getInstance().getReference().child("Users").child(emailpartwithout[0]).child("Party");
        allpoliticalparty= FirebaseDatabase.getInstance().getReference().child("Party");

    }@Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<politicalparty, Allfamilyviewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<politicalparty, Allfamilyviewholder>(
                politicalparty.class,R.layout.activity_party,Allfamilyviewholder.class,allpoliticalparty)
   {
            @Override
            protected void populateViewHolder(final Allfamilyviewholder viewHolder, final politicalparty model, final int position) {
                viewHolder.setImage(model.getImage());
                viewHolder.setName(model.getName());
                viewHolder.setParty(model.getParty());
                viewHolder.setManifesto(model.getManifesto());
                viewHolder.setPartyimg(model.getPartyimg());viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(VOTEFINAL.this,model.getParty(),model.getName());
                    }
                });

            }
        };
        allfamilylist.setAdapter(firebaseRecyclerAdapter);

    }
    public static class Allfamilyviewholder extends RecyclerView.ViewHolder{
        View mview;


        public Allfamilyviewholder(@NonNull View itemView) {
            super(itemView);
            mview=itemView;
        }

        public void setName(String famname){
            TextView nameview = (TextView) mview.findViewById(R.id.name);
            nameview.setText(famname);
        }
        public void setParty(String famname){
            TextView nameview = (TextView) mview.findViewById(R.id.partyname);
            nameview.setText(famname);
        }

        public void setImage(final String user_thumb_image){
            final CircleImageView thumb_image =  mview.findViewById(R.id.personimg);
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


        public void setPartyimg(final String partyimg) {
            final ImageView thumb_image =  mview.findViewById(R.id.partyimg);
            Picasso.with(mview.getContext()).load(partyimg).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_person_black_24dp).into(thumb_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(mview.getContext()).load(partyimg).placeholder(R.drawable.ic_person_black_24dp).into(thumb_image);
                }
            });

        }

        public void setManifesto(String manifesto) {
            TextView nameview = (TextView) mview.findViewById(R.id.manifesto);
            nameview.setText(manifesto);
        }
    }
    public void showDialog3(Activity activity, final String key) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.newcustom_layout5);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView keyth=dialog.findViewById(R.id.key);
        keyth.setText(key);
        dialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.cancel();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("email", email);
                i.putExtra("aadhaar", aadhaar);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

            }
        }, 6000);




    }

    public void showDialog2(Activity activity, final String party, String name) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.newcustom_layout4);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView partyname=dialog.findViewById(R.id.partyname);
        TextView person=dialog.findViewById(R.id.personname);
        partyname.setText(party);
        person.setText(name);
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
                //voteit(party);
                //changetime(party);
                setTime(party,"down");
            }
        });


        dialog.show();
    }

    public void showDialog(Activity activity, final String party, final String name) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.newcustom_layout3);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button delete = dialog.findViewById(R.id.delete);
        TextView partyname=dialog.findViewById(R.id.partyname);
        TextView person=dialog.findViewById(R.id.personname);
        partyname.setText(party);
        person.setText(name);
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


            }
        });
        Button vote = dialog.findViewById(R.id.vote);
        vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                showDialog2(VOTEFINAL.this, party,name);
            }
        });


        dialog.show();
    }


    public  void setVotes(final String party, final String operation) {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Party").child(party);
        DatabaseReference votesRef = rootRef.child("votes");
        votesRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer votes = Integer.parseInt(mutableData.getValue(String.class));
                if (votes == null) {
                    return Transaction.success(mutableData);
                }

                if (operation.equals("voted")) {
                    votes++;
                    mutableData.setValue(votes+"");

                } else if (operation.equals("decreaseScore")){
                    mutableData.setValue(votes - 1);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                final String emailpartwithout[] =email.split("@",2);
                DatabaseReference allpoliticalparty2 = FirebaseDatabase.getInstance().getReference().child("Users").child(emailpartwithout[0]).child("familymember").child(aadhaar).child("voted");
                allpoliticalparty2.setValue("YES").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        DatabaseReference allpoliticalparty2 = FirebaseDatabase.getInstance().getReference().child("Users").child(emailpartwithout[0]).child("familymember").child(aadhaar).child("partyvoted");
                        allpoliticalparty2.setValue(getSHA256(party)).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(VOTEFINAL.this, "You have Voted!!", Toast.LENGTH_SHORT).show();
                                sendMessage(email);
                                showDialog3(VOTEFINAL.this,getSHA256(party));

                            }
                        });


                    }
                });

            }
        });



        }


    public  void setTime(final String party, final String operation) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Party").child(party);
        DatabaseReference timeRef = rootRef.child("time");
        timeRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer votes = Integer.parseInt(mutableData.getValue(String.class));
                if (votes == null) {
                    return Transaction.success(mutableData);
                }

                if (operation.equals("voted")) {
                    votes++;
                    mutableData.setValue(""+votes);
                    Toast.makeText(VOTEFINAL.this, "Voted", Toast.LENGTH_SHORT).show();
                } else if (operation.equals("down")){
                    votes--;
                    mutableData.setValue(""+votes);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Toast.makeText(VOTEFINAL.this, "VotedTIME", Toast.LENGTH_SHORT).show();
                setVotes(party,"voted");

            }
        });

    }



    private void sendMessage(String email1) {
        String rec="gptshubham595@gmail.com";
        String str[]=email1.split(" ");
        String user="vote4usiitg@gmail.com";
        String pass="iitg00000000";

        SendEmailAsyncTask3 email = new SendEmailAsyncTask3();
        email.activity = this;
        email.m = new Mail(user, pass);
        email.m.set_from(user);
        email.m.setBody(aadhaar+": YOU HAVE VOTED");
        email.m.set_to(str);
        email.m.set_subject("VOTE4US VOTING Success!!");
        email.execute();
    }

    public void displayMessage(String message) {
        Snackbar.make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
    private static final char[] hexArray = "0123456789abcdef".toCharArray();

    public static String getSHA256(String data) {
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data.getBytes());
            byte[] byteData = md.digest();
            sb.append(bytesToHex(byteData));
        } catch(Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return String.valueOf(hexChars);
    }

}

class SendEmailAsyncTask3 extends AsyncTask<Void, Void, Boolean> {
    Mail m;
    VOTEFINAL activity;

    public SendEmailAsyncTask3() {}

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

