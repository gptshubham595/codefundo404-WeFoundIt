package com.codefundo.votenew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SnapActivity extends AppCompatActivity {

    private ProgressDialog mLoginProgress;

    private FirebaseAuth mAuth;

    private DatabaseReference mUserDatabase,mUserDatabase2,mDatabase;
    private Uri mImageUri = null;

    private static final  int GALLERY_REQUEST =1;

    private static final int CAMERA_REQUEST_CODE=1;

    private StorageReference mStorage;
    private Button scan_btn,reg_vote;
    static int count=0;
    String download_url;
    String thumb_downloadUrl;
    Button mImageBtn;
    private CircleImageView mDisplayImage;

    private static final int GALLERY_PICK = 1;

    // Storage Firebase
    private StorageReference mImageStorage;

    private ProgressDialog mProgressDialog;
    String name,aadhaar,state,pc,age,dist,gender,dob,mobile,emailfinal,email,pin;
    String[] emailpartwithout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap);
        mLoginProgress = new ProgressDialog(this,R.style.dialog);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        mAuth = FirebaseAuth.getInstance();


        //////
        try {
            emailfinal=getIntent().getExtras().get("email").toString().toLowerCase();
            emailpartwithout =emailfinal.split("@",2);
            name = getIntent().getExtras().get("name").toString();
            aadhaar = getIntent().getExtras().get("aadhaar").toString();
            state = getIntent().getExtras().get("state").toString();
            pc = getIntent().getExtras().get("pin").toString();
            age = getIntent().getExtras().get("age").toString();
            dist = getIntent().getExtras().get("district").toString();
            gender = getIntent().getExtras().get("gender").toString();
            dob = getIntent().getExtras().get("dob").toString();
            mobile = getIntent().getExtras().get("mobile").toString();
            email = getIntent().getExtras().get("email").toString();
            pin = getIntent().getExtras().get("pin").toString();
        }catch (Exception e ){e.printStackTrace();}

        //////
        String emailpartwithout[] =email.split("@",2);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(emailpartwithout[0]).child("FAMILY_MEMBER");
        mUserDatabase.keepSynced(true);
        mDisplayImage = (CircleImageView) findViewById(R.id.settings_image);
        mImageBtn = (Button) findViewById(R.id.settings_image_btn);
        mImageStorage = FirebaseStorage.getInstance().getReference();


        Button register=findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(SnapActivity.this);

            }
        });





        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);


            }
        });


////


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(this);

            Toast.makeText(SnapActivity.this, imageUri+"", Toast.LENGTH_LONG).show();

        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Step 1", Toast.LENGTH_SHORT).show();

                mProgressDialog = new ProgressDialog(SnapActivity.this);
                mProgressDialog.setTitle("Uploading Image...");
                mProgressDialog.setMessage("Please wait while we upload and process the image.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();


                Uri resultUri = result.getUri();

                File thumb_filePath = new File(resultUri.getPath());

                String current_user_id = mAuth.getCurrentUser().getUid();

                Toast.makeText(this, "Step 2", Toast.LENGTH_SHORT).show();
                Bitmap thumb_bitmap = null;
                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, "Step 3", Toast.LENGTH_SHORT).show();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                Toast.makeText(this, "Step 4", Toast.LENGTH_SHORT).show();
                StorageReference filepath = mImageStorage.child("profile_images").child(aadhaar + ".jpg");
                final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(aadhaar + ".jpg");


                Toast.makeText(this, "Step 5", Toast.LENGTH_SHORT).show();
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(SnapActivity.this, "Step 6", Toast.LENGTH_SHORT).show();
                            download_url = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                    Toast.makeText(SnapActivity.this, "Step 7", Toast.LENGTH_SHORT).show();
                                    thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();

                                    Toast.makeText(SnapActivity.this, thumb_downloadUrl, Toast.LENGTH_SHORT).show();
                                    if(thumb_task.isSuccessful()){
                                        Toast.makeText(SnapActivity.this, "Step 8", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(SnapActivity.this, "Success Uploading.", Toast.LENGTH_LONG).show();
                                        adddatatobase(aadhaar,name,state,pc,dist,age,gender,dob,mobile,email,pin);

                                        Toast.makeText(getApplicationContext(),"Registered!!",Toast.LENGTH_LONG).show();

                                    } else {
                                        Toast.makeText(SnapActivity.this, "Step 9", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(SnapActivity.this, "Error in uploading thumbnail.", Toast.LENGTH_LONG).show();
                                        mProgressDialog.dismiss();
                                        Intent i=new Intent(getApplicationContext(),ReaderActivity.class);
                                        i.putExtra("email",emailfinal);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                    }


                                }
                            });



                        } else {

                            Toast.makeText(SnapActivity.this, "Error in uploading.", Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();

                        }

                    }
                });



            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(SnapActivity.this, "Step ERR", Toast.LENGTH_SHORT).show();
                Exception error = result.getError();

            }
        }


    }



    public void setImagehere(final String user_thumb_image){
        mProgressDialog.dismiss();
        Picasso.with(SnapActivity.this).load(user_thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_person_black_24dp).into(mDisplayImage, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(SnapActivity.this).load(user_thumb_image).placeholder(R.drawable.ic_person_black_24dp).into(mDisplayImage);
            }
        });

    }

    private void adddatatobase(String uid,String name, String state, String pc, String dist, String age, String gender, String dob,String mobile,String email,String pin) {
        mLoginProgress.show();


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(emailpartwithout[0]).child("FAMILY_MEMBER").child(uid);
        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("name", name);
        userMap.put("state", state);
        userMap.put("pin", pc);
        userMap.put("district", dist);
        userMap.put("age", age);
        userMap.put("gender", gender);
        userMap.put("dob", dob);
        userMap.put("mobile", mobile);
        userMap.put("pin", pin);
        userMap.put("aadhaar", uid);
        userMap.put("email", email);
        userMap.put("image", download_url);
        userMap.put("thumb_image", thumb_downloadUrl);



        if(Integer.parseInt(age)>=18){userMap.put("eligible","YES");}
        else{userMap.put("eligible","NO");}
        mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    setImagehere(thumb_downloadUrl);
                    mLoginProgress.dismiss();
                    Toast.makeText(getApplicationContext(),"Registered!!",Toast.LENGTH_LONG).show();

                }

            }
        });

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Users").child(emailpartwithout[0]).child("TOTAL_FAMILY_MEMBER");
        myRef.keepSynced(true);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                count=Integer.parseInt((value));
                count++;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });


        myRef.setValue(""+count).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    mLoginProgress.dismiss();
                    Toast.makeText(getApplicationContext(),"Registered!!",Toast.LENGTH_LONG).show();

                }

            }
        });
    }


    public void showDialog(Activity activity) {
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

                Intent i =new Intent(getApplicationContext(),MainActivity.class);
                i.putExtra("email",emailfinal);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            }});}
}
