package com.codefundo.votenew.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.codefundo.votenew.R;
import com.codefundo.votenew.ReaderActivity;
import com.codefundo.votenew.SnapActivity;
import com.codefundo.votenew.listeners.PictureCapturingListener;
import com.codefundo.votenew.services.APictureCapturingService;
import com.codefundo.votenew.services.PictureCapturingServiceImpl;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;



public class MainActivity extends AppCompatActivity implements PictureCapturingListener, ActivityCompat.OnRequestPermissionsResultCallback {
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

    // Storage Firebase
    private StorageReference mImageStorage;

    private ProgressDialog mProgressDialog;
    String aadhaar,emailfinal,email;
    String[] emailpartwithout;
    private static final String[] requiredPermissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
    };
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_CODE = 1;

    private ImageView uploadBackPhoto;
    private ImageView uploadFrontPhoto;
    
     //The capture service          
    private APictureCapturingService pictureService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maincam);
        checkPermissions();
        mLoginProgress = new ProgressDialog(this,R.style.dialog);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

       mAuth = FirebaseAuth.getInstance();
        mImageStorage = FirebaseStorage.getInstance().getReference();
        try{
            aadhaar = getIntent().getExtras().get("aadhaar").toString();
            email = getIntent().getExtras().get("email").toString();
    }catch (Exception e ){e.printStackTrace();}

        uploadBackPhoto = (ImageView) findViewById(R.id.backIV);
        uploadFrontPhoto = (ImageView) findViewById(R.id.frontIV);
        final Button btn = (Button) findViewById(R.id.startCaptureBtn);
        // getting instance of the Service from PictureCapturingServiceImpl
        pictureService = PictureCapturingServiceImpl.getInstance(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btn.performClick();
            }
        }, 2000);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("Starting capture!");
                pictureService.startCapturing(MainActivity.this);
            }
        });
    }
    
    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
    * We've finished taking pictures from all phone's cameras
    */    
    @Override
    public void onDoneCapturingAllPhotos(TreeMap<String, byte[]> picturesTaken) {
        if (picturesTaken != null && !picturesTaken.isEmpty()) {
            showToast("Done capturing all photos!");
            return;
        }
        showToast("No camera detected!");
    }

    /**
    * Displaying the pictures taken.
    */             
    @Override
    public void onCaptureDone(final String pictureUrl, final byte[] pictureData) {
        if (pictureData != null && pictureUrl != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Bitmap bitmap = BitmapFactory.decodeByteArray(pictureData, 0, pictureData.length);
                    final int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                    final Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    scaled.compress(Bitmap.CompressFormat.JPEG, 70, baos);

                    if (pictureUrl.contains("0_pic.jpg")) {
                        uploadBackPhoto.setImageBitmap(scaled);
                    } else if (pictureUrl.contains("1_pic.jpg")) {
                        uploadFrontPhoto.setImageBitmap(scaled);

                        uploadit(scaled,pictureUrl);


                    }
                }
            });
            showToast("Picture saved to " + pictureUrl);
        }
    }

    private void uploadit(Bitmap bitmap, String pictureUrl) {
        //Toast.makeText(this, "Step 1", Toast.LENGTH_SHORT).show();

        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setTitle("Uploading Image...");
        mProgressDialog.setMessage("Please wait while we upload and process the image.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();



        Uri resultUri = null;
            resultUri=Uri.parse(pictureUrl);

            File thumb_filePath = new File(Objects.requireNonNull(resultUri.getPath()));

          //  Toast.makeText(this, "Step 2", Toast.LENGTH_SHORT).show();
            Bitmap thumb_bitmap = bitmap;
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
            StorageReference filepath = mImageStorage.child("voter_images").child(aadhaar+".jpg");
            final StorageReference thumb_filepath = mImageStorage.child("voter_images").child("thumbs").child(aadhaar + ".jpg");


              // Toast.makeText(this, "Step 5", Toast.LENGTH_SHORT).show();
        Uri uploadUri = Uri.fromFile(new File(resultUri.toString()));
            filepath.putFile(uploadUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful()){
                         Toast.makeText(MainActivity.this, "Step 6", Toast.LENGTH_SHORT).show();
                        download_url = task.getResult().getDownloadUrl().toString();

                    } else {

                        Toast.makeText(MainActivity.this, "Error in uploading.", Toast.LENGTH_LONG).show();

                    }

                    UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                //            Toast.makeText(MainActivity.this, "Step 7", Toast.LENGTH_SHORT).show();
                            thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();

                            Toast.makeText(MainActivity.this, thumb_downloadUrl, Toast.LENGTH_SHORT).show();
                            if(thumb_task.isSuccessful()){
                                //    Toast.makeText(SnapActivity.this, "Step 8", Toast.LENGTH_SHORT).show();
                                mProgressDialog.dismiss();
                                Toast.makeText(MainActivity.this, "Success Uploading.", Toast.LENGTH_LONG).show();
                                Intent i =new Intent(getApplicationContext(), com.codefundo.votenew.MainActivity.class);
                                i.putExtra("email",email);
                                i.putExtra("aadhaar",aadhaar);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);


                                Toast.makeText(getApplicationContext(),"Registered!!",Toast.LENGTH_LONG).show();

                            } else {
                                //   Toast.makeText(SnapActivity.this, "Step 9", Toast.LENGTH_SHORT).show();
                                Toast.makeText(MainActivity.this, "Error in uploading thumbnail.", Toast.LENGTH_LONG).show();
                                mProgressDialog.dismiss();
                                Intent i=new Intent(getApplicationContext(), MainActivity.class);
                                i.putExtra("email",email);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }


                        }
                    });



                    mProgressDialog.dismiss();
                }
            });



        }


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
}

