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
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
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
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static java.lang.Thread.sleep;

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
    int fhour, fmin, shour, smin;

    private static final int GALLERY_PICK = 1;
    static int counted=0;

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

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(emailpartwithout[0]).child("familymember");
        mUserDatabase.keepSynced(true);
        mDisplayImage = (CircleImageView) findViewById(R.id.settings_image);
        mImageBtn = (Button) findViewById(R.id.settings_image_btn);
        mImageStorage = FirebaseStorage.getInstance().getReference();


        Button register=findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adddata(aadhaar);
                showDialog(SnapActivity.this);
                Intent i =new Intent(getApplicationContext(),MainActivity.class);
                i.putExtra("email",emailfinal);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            }
        });





        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


               Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

                /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photo));
                Uri imageUri = Uri.fromFile(photo);
                startActivityForResult(intent, 1);*/
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
                //Toast.makeText(this, "Step 1", Toast.LENGTH_SHORT).show();

                mProgressDialog = new ProgressDialog(SnapActivity.this);
                mProgressDialog.setTitle("Uploading Image...");
                mProgressDialog.setMessage("Please wait while we upload and process the image.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();


                Uri resultUri = result.getUri();

                File thumb_filePath = new File(resultUri.getPath());

                String current_user_id = mAuth.getCurrentUser().getUid();

                //Toast.makeText(this, "Step 2", Toast.LENGTH_SHORT).show();
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
                //Toast.makeText(this, "Step 3", Toast.LENGTH_SHORT).show();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                //Toast.makeText(this, "Step 4", Toast.LENGTH_SHORT).show();
                StorageReference filepath = mImageStorage.child("profile_images").child(aadhaar + ".jpg");
                final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(aadhaar + ".jpg");


             //   Toast.makeText(this, "Step 5", Toast.LENGTH_SHORT).show();
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){
                           // Toast.makeText(SnapActivity.this, "Step 6", Toast.LENGTH_SHORT).show();
                            download_url = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                //    Toast.makeText(SnapActivity.this, "Step 7", Toast.LENGTH_SHORT).show();
                                    thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();

                                 //   Toast.makeText(SnapActivity.this, thumb_downloadUrl, Toast.LENGTH_SHORT).show();
                                    if(thumb_task.isSuccessful()){
                                    //    Toast.makeText(SnapActivity.this, "Step 8", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(SnapActivity.this, "Success Uploading.", Toast.LENGTH_LONG).show();
                                        try {
                                            adddatatobase(aadhaar,name,state,pc,dist,age,gender,dob,mobile,email,pin);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }


                                        Toast.makeText(getApplicationContext(),"Registered!!",Toast.LENGTH_LONG).show();

                                    } else {
                                     //   Toast.makeText(SnapActivity.this, "Step 9", Toast.LENGTH_SHORT).show();
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


    private void adddata(String aadhaar) {
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = current_user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("voters");
        HashMap<String, String> userMap = new HashMap<>();
        userMap.put(aadhaar, aadhaar);
        mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                   // Toast.makeText(getApplicationContext(),"Registered2!!",Toast.LENGTH_LONG).show();

                }

            }
        });
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

    private void adddatatobase(String uid,String name, String state, String pc, String dist, String age, String gender, String dob,String mobile,String email,String pin) throws Exception {
        mLoginProgress.show();
        String encryptedpin=AESCrypt.encrypt(pin);

        String emailpartwithout[] =email.split("@",2);
        int before=emailpartwithout[0].length();
        int after=emailpartwithout[1].length();

        StringBuilder first = new StringBuilder(email.charAt(0));
        StringBuilder last =  new StringBuilder("");
        first.append(email.charAt(0));
        while(before>1){
            first.append("x"); before--;}
        first.append("@");
        while(after>1){
            last.append("x"); after--;}
        last.append(email.charAt(email.length() - 1));
        String encryptedemail=first.toString()+last.toString();


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(emailpartwithout[0]).child("familymember").child(uid);
        HashMap<String, String> userMap = new HashMap<>();
        Random rand = new Random();
         fhour = rand.nextInt(12)+8;
         fmin=rand.nextInt(12)*5;
         shour=fhour;
         smin=0;
        if(fmin+30>59){smin=30-(60-fmin); shour++;}else{smin=fmin+30;}
        userMap.put("name", name);
        userMap.put("state", state);
        userMap.put("pincode", pc);
        userMap.put("district", dist);
        userMap.put("age", age);
        userMap.put("gender", gender);
        userMap.put("dob", dob);
        userMap.put("mobile", mobile);
        userMap.put("pin", encryptedpin);
        userMap.put("aadhaar", uid);
        userMap.put("voted", "NO");
        userMap.put("email", email);
        userMap.put("hiddenemail", encryptedemail);
        userMap.put("slotstart", "18.08.2019, "+fhour+":"+fmin+":"+"00");
        //userMap.put("slotend", "18.08.2019, 10:35:35");
        userMap.put("slotend", "18.08.2019, "+shour+":"+smin+":"+"00");
        //userMap.put("slotstart", "18.08.2019, 10:05:36");
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
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        counted=count+1;
        myRef.setValue(""+counted).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    mLoginProgress.dismiss();
                    Toast.makeText(getApplicationContext(),"FAMILY MEMEBER ADDED!!",Toast.LENGTH_LONG).show();

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
