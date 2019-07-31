package com.codefundo.votenew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {
    AppCompatButton addfam;
    RecyclerView   allfamilylist;
    String email;
    DatabaseReference allfamdatabaseReference,mUser,totalfam;
    AppCompatTextView fam;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        mAuth = FirebaseAuth.getInstance();
        email="";
        try{
        email=getIntent().getExtras().get("email").toString().toLowerCase();}catch (Exception e ){e.printStackTrace();}
        allfamilylist=(RecyclerView) findViewById(R.id.recycler);
        allfamilylist.setHasFixedSize(true);
        allfamilylist.setLayoutManager(new LinearLayoutManager(this));
        allfamdatabaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(email).child("FAMILY_MEMBER");
        allfamdatabaseReference.keepSynced(true);
        fam=findViewById(R.id.fam);
        Toast.makeText(this, email, Toast.LENGTH_SHORT).show();
        totalfam= FirebaseDatabase.getInstance().getReference().child("Users").child(email).child("TOTAL_FAMILY_MEMBER");
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
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<allfamily, Allfamilyviewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<allfamily, Allfamilyviewholder>(
                allfamily.class,R.layout.activity_fam,Allfamilyviewholder.class,allfamdatabaseReference
        ) {
            @Override
            protected void populateViewHolder(Allfamilyviewholder viewHolder,final allfamily model, final int position) {
                Toast.makeText(MainActivity.this, model.getName(), Toast.LENGTH_SHORT).show();
                viewHolder.setName(model.getName());
                viewHolder.setImage(model.getImage());
                viewHolder.setAadhaar(model.getAadhaar());
                viewHolder.setDob(model.getDob());
                viewHolder.setEligible(model.getEligible());
                viewHolder.setGender(model.getGender());
                final String userid=getRef(position).getKey();
                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(MainActivity.this,model.getAadhaar());
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
        }


        public void setImage(final String user_thumb_image){
            final ImageView thumb_image = (ImageView) mview.findViewById(R.id.familymemberimage);
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
    public void showDialog(Activity activity, final String aadhaar) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.newcustom_layout2);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button delete = dialog.findViewById(R.id.delete);
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
                voteit(aadhaar);
            }
        });


        dialog.show();
    }

    private void voteit(String aadhaar) {
    Intent i =new Intent(getApplicationContext(),VOTEFINAL.class);
    i.putExtra("aadhaar",aadhaar);
    startActivity(i);
    }

    private void deleteit(String aadhaar) {
    mUser= FirebaseDatabase.getInstance().getReference().child("Users").child(email).child("FAMILY_MEMBER").child(aadhaar);
    mUser.keepSynced(true);
    mUser.removeValue();
    }
}
