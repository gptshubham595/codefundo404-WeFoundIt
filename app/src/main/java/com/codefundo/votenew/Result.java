package com.codefundo.votenew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class Result extends AppCompatActivity {
FirebaseAuth mAuth;
    RecyclerView   allfamilylist;
    DatabaseReference allfamdatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        mAuth = FirebaseAuth.getInstance();

        allfamilylist=(RecyclerView) findViewById(R.id.recycler);
        allfamilylist.setHasFixedSize(true);
        allfamilylist.setLayoutManager(new LinearLayoutManager(this));

        allfamdatabaseReference= FirebaseDatabase.getInstance().getReference().child("Party");
        allfamdatabaseReference.keepSynced(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference mUsersDatabase=FirebaseDatabase.getInstance().getReference().child("Party");
        mUsersDatabase.keepSynced(true);
        Query searchpeople=mUsersDatabase.orderByChild("time").limitToLast(15);
        FirebaseRecyclerAdapter<displayresult, Allfamilyviewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<displayresult, Allfamilyviewholder>(
                displayresult.class,R.layout.activity_res, Allfamilyviewholder.class,searchpeople
        ) {
            @Override
            protected void populateViewHolder(final Allfamilyviewholder viewHolder, final displayresult model, final int position) {
                viewHolder.setParty(model.getParty());
                viewHolder.setVotes(model.getVotes());
                viewHolder.setPartyimg(model.getPartyimg());
                final String userid=getRef(position).getKey();
                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


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


        public void setParty(String party) {
            TextView votes=mview.findViewById(R.id.partyname);
            votes.setText(party);
        }

        public void setPartyimg(final String user_thumb_image) {
            final ImageView thumb_image =  mview.findViewById(R.id.partyimg);
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
        public void setVotes(String partyvotes) {
        TextView votes=mview.findViewById(R.id.votes);
        votes.setText(partyvotes);
        }
    }





}
