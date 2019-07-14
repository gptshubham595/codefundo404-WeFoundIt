package com.codefundo.vote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codefundo.vote.R;


public class MainActivity2 extends AppCompatActivity {
    Button scan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainf);
        EditText e=findViewById(R.id.num);
        Button b=findViewById(R.id.numbtn);

        final String ad =e.getText().toString();

        scan = (Button) findViewById(R.id.scan);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent rIntent = new Intent(MainActivity2.this, ReaderActivity.class);
                startActivity(rIntent);
            }
        });

    }

}