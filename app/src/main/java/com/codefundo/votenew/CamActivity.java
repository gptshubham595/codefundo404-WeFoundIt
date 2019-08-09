package com.codefundo.votenew;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class CamActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;
    ImageView mimageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam);


            mimageView = (ImageView) this.findViewById(R.id.image_from_camera);
            Button button = (Button) this.findViewById(R.id.take_image_from_camera);
        }

        public void takeImageFromCamera(View view) {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }

        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
                Bitmap mphoto = (Bitmap) data.getExtras().get("data");
                mimageView.setImageBitmap(mphoto);
            }
        }
    }