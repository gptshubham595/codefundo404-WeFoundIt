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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class VOTEFINAL extends AppCompatActivity {
    private FirebaseAuth mAuth;
    DatabaseReference allpoliticalparty;
    RecyclerView allfamilylist;
    String email="";
    String aadhaar="";
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
        allpoliticalparty= FirebaseDatabase.getInstance().getReference().child("Users").child(emailpartwithout[0]).child("Party");


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
                voteit(party);
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
public void voteit(String party){
    final int[] votes = {0};
    String emailpartwithout[] =email.split("@",2);
    allpoliticalparty= FirebaseDatabase.getInstance().getReference().child("Users").child(emailpartwithout[0]).child("Party").child(party).child("votes");
    allpoliticalparty.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            String value = dataSnapshot.getValue(String.class);
            votes[0] =Integer.parseInt(value)+1;
        }

        @Override
        public void onCancelled(DatabaseError error) {
        }
    });
allpoliticalparty.setValue(""+votes[0]);

    allpoliticalparty= FirebaseDatabase.getInstance().getReference().child("Users").child(emailpartwithout[0]).child("familymember").child(aadhaar).child("voted");
    allpoliticalparty.setValue("YES");

    Toast.makeText(this, "You have Voted!!", Toast.LENGTH_SHORT).show();
    Intent i =new Intent(getApplicationContext(),MainActivity.class);
    i.putExtra("email",email);
    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(i);
}
}
