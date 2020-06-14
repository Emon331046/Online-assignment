package com.example.onlineassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

public class SingleSubmissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_submission);
        // create the get Intent object
        Intent intent = getIntent();
        PhotoView photoView = (PhotoView) findViewById(R.id.sub_view);

        // receive the value by getStringExtra() method
        // and key must be same which is send by first activity
        String currentImageUrl = intent.getStringExtra("currentSubmissionUrl");
        if(currentImageUrl.isEmpty() ){
            Toast.makeText(getApplicationContext(),"error loading image",Toast.LENGTH_SHORT).show();
        } else {


            Picasso.with(getApplicationContext())
                    .load(currentImageUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .fit()
                    .centerInside()
                    .into(photoView);

        }



    }
}