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

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;

import com.androidhiddencamera.HiddenCameraFragment;
import com.codefundo.votenew.R;
import com.google.firebase.auth.FirebaseAuth;

public class CamMainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Uri mImageUri = null;

    private static final  int GALLERY_REQUEST =1;
    private static final int CAMERA_REQUEST_CODE=1;
    private static final int GALLERY_PICK = 1;

    String emailintent,aadhaar;
    private HiddenCameraFragment mHiddenCameraFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maincam);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        mAuth = FirebaseAuth.getInstance();

        try{
            emailintent=getIntent().getExtras().get("email").toString().toLowerCase();
            aadhaar=getIntent().getExtras().get("aadhaar").toString().toLowerCase();
        }catch (Exception e ){e.printStackTrace();}


        mHiddenCameraFragment = new DemoCamFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, mHiddenCameraFragment)
                .commit();
    }
    @Override
    public void onBackPressed() {
        if (mHiddenCameraFragment != null) {    //Remove fragment from container if present
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(mHiddenCameraFragment)
                    .commit();
            mHiddenCameraFragment = null;
        }else { //Kill the activity
            super.onBackPressed();
        }
    }
}
