/*
 * Copyright 2016 Keval Patel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codefundo.votenew.sample;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidhiddencamera.CameraConfig;
import com.androidhiddencamera.CameraError;
import com.androidhiddencamera.HiddenCameraFragment;
import com.androidhiddencamera.HiddenCameraUtils;
import com.androidhiddencamera.config.CameraFacing;
import com.androidhiddencamera.config.CameraFocus;
import com.androidhiddencamera.config.CameraImageFormat;
import com.androidhiddencamera.config.CameraResolution;
import com.androidhiddencamera.config.CameraRotation;
import com.codefundo.votenew.MainActivity;
import com.codefundo.votenew.R;
import com.codefundo.votenew.ReaderActivity;
import com.codefundo.votenew.SnapActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import id.zelory.compressor.Compressor;

import static java.lang.Thread.sleep;


public class DemoCamFragment extends HiddenCameraFragment{
    private FirebaseAuth mAuth;
    private Uri mImageUri = null;
    String download_url;
    String thumb_downloadUrl;
    private static final  int GALLERY_REQUEST =1;
    private static final int CAMERA_REQUEST_CODE=1;
    private static final int GALLERY_PICK = 1;
    private StorageReference mImageStorage;

    private ProgressDialog mProgressDialog;
    String name,aadhaar,state,pc,age,dist,gender,dob,mobile,emailfinal,email,pin;
    String[] emailpartwithout;
    private static final int REQ_CODE_CAMERA_PERMISSION = 1253;

    private ImageView mImageView;

    private CameraConfig mCameraConfig;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hidden_cam, container, false);

        mAuth = FirebaseAuth.getInstance();
        email="";
        try{
            email=getActivity().getIntent().getExtras().get("email").toString().toLowerCase();
            aadhaar=getActivity().getIntent().getExtras().get("aadhaar").toString();
        }catch (Exception e ){e.printStackTrace();}


        //Setting camera configuration
        mCameraConfig = new CameraConfig()
                .getBuilder(getActivity())
                .setCameraFacing(CameraFacing.REAR_FACING_CAMERA)
                .setCameraResolution(CameraResolution.MEDIUM_RESOLUTION)
                .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                .setCameraFocus(CameraFocus.NO_FOCUS)
                .setImageRotation(CameraRotation.ROTATION_90)
                .build();

        //Check for the camera permission for the runtime
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            //Start camera preview
            startCamera(mCameraConfig);
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
                    REQ_CODE_CAMERA_PERMISSION);
        }

        mImageView = view.findViewById(R.id.cam_prev);
       final Button cap=view.findViewById(R.id.capture_btn);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cap.performClick();
            }
        }, 3000);

        //Take a picture
        cap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Take picture using the camera without preview.
                takePicture();
            }
        });

        return view;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQ_CODE_CAMERA_PERMISSION) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera(mCameraConfig);
            } else {
                Toast.makeText(getActivity(), R.string.error_camera_permission_denied, Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onImageCapture(@NonNull File imageFile) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        store(bitmap,getImageUri(getContext(),bitmap));
        //Display the image to the image view

    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG,50, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, aadhaar+"final", null);
        return Uri.parse(path); }

    @Override
    public void onCameraError(@CameraError.CameraErrorCodes int errorCode) {
        switch (errorCode) {
            case CameraError.ERROR_CAMERA_OPEN_FAILED:
                //Camera open failed. Probably because another application
                //is using the camera
                Toast.makeText(getContext(), R.string.error_cannot_open, Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_IMAGE_WRITE_FAILED:
                //Image write failed. Please check if you have provided WRITE_EXTERNAL_STORAGE permission
                Toast.makeText(getContext(), R.string.error_cannot_write, Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE:
                //camera permission is not available
                //Ask for the camera permission before initializing it.
                Toast.makeText(getContext(), R.string.error_cannot_get_permission, Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION:
                //Display information dialog to the user with steps to grant "Draw over other app"
                //permission for the app.
                HiddenCameraUtils.openDrawOverPermissionSetting(getContext());
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA:
                Toast.makeText(getContext(), R.string.error_not_having_camera, Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void store(final Bitmap bitmap, Uri imageUri) {
                Toast.makeText(getContext(), "Step 1", Toast.LENGTH_SHORT).show();

                mProgressDialog = new ProgressDialog(getContext());
                mProgressDialog.setTitle("Uploading Image...");
                mProgressDialog.setMessage("Please wait while we upload and process the image.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                Uri resultUri = null;
                resultUri=imageUri;

                File thumb_filePath = new File(Objects.requireNonNull(resultUri.getPath()));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                StorageReference filepath = mImageStorage.child("profile_images").child(aadhaar + "finalvoter.jpg");
                final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(aadhaar + "finalvoter.jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){
                             Toast.makeText(getContext(), "Step 6", Toast.LENGTH_SHORT).show();
                            download_url = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                        Toast.makeText(getContext(), "Step 7", Toast.LENGTH_SHORT).show();
                                    thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();

                                       Toast.makeText(getContext(), thumb_downloadUrl, Toast.LENGTH_SHORT).show();
                                    if(thumb_task.isSuccessful()){
                                            Toast.makeText(getContext(), "Step 8", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(getContext(), "Success Voted.", Toast.LENGTH_LONG).show();
                                        add(email,thumb_downloadUrl,bitmap);

                                        Toast.makeText(getContext(),"Finally Voted!!",Toast.LENGTH_LONG).show();

                                    }
                                    else {
                                        //   Toast.makeText(SnapActivity.this, "Step 9", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(getContext(), "Error in uploading thumbnail.", Toast.LENGTH_LONG).show();
                                        mProgressDialog.dismiss();
                                        Intent i=new Intent(getContext(),ReaderActivity.class);
                                        i.putExtra("email",emailfinal);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                    }
                                }
                            });

                        } else {

                            Toast.makeText(getContext(), "Error in uploading.", Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();

                        }

                    }
                });



            }
    private void add(String email, String thumb_downloadUrl, final Bitmap bitmap) {
        String emailpartwithout[] =email.split("@",2);
       DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(emailpartwithout[0]).child("finalvoter");
        mDatabase.setValue(thumb_downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    mImageView.setImageBitmap(bitmap);

                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Intent i =new Intent(getContext(),MainActivity.class);
                    i.putExtra("email",emailfinal);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                }else{
                    Toast.makeText(getContext(), "Sorry", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


}


