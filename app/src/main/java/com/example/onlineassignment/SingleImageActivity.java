package com.example.onlineassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

public class SingleImageActivity extends AppCompatActivity {
    private Button newSubmit;

    private Button showSubmission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_image);

        newSubmit = (Button) findViewById(R.id.new_submit);
        showSubmission = (Button) findViewById(R.id.show_submission);




        // create the get Intent object
        Intent intent = getIntent();

        // receive the value by getStringExtra() method
        // and key must be same which is send by first activity
        String currentImageUrl = intent.getStringExtra("currentImageUrl");
        final String currentImageName = intent.getStringExtra("currentImageName");
        if(currentImageUrl.isEmpty() ){
            Toast.makeText(getApplicationContext(),"error loading image",Toast.LENGTH_SHORT).show();
        } else {
            PhotoView photoView = (PhotoView) findViewById(R.id.photo_view);


            Picasso.with(getApplicationContext())
                    .load(currentImageUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .fit()
                    .centerInside()
                    .into(photoView);

        }


        newSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NetConnectionCheck.isConnected(getApplicationContext())){

                    SharedPreferences sharedPref = getSharedPreferences("MY_DATA",getApplicationContext().MODE_PRIVATE);

                    String userType = sharedPref.getString(getString(R.string.user_type),null);


                    //  Toast.makeText(getApplicationContext(),"student "+userType,Toast.LENGTH_SHORT).show();

                    if (userType !=null &&  userType.equals("student")){

                        Toast.makeText(getApplicationContext(),"student",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),SubmissionActivity.class);


                        intent.putExtra("currentImageName",currentImageName);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(),"you are not a student",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SingleImageActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }


            }
        });

        showSubmission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetConnectionCheck.isConnected(getApplicationContext())){


                    Intent intent = new Intent(SingleImageActivity.this, ShowSubmissionsActivity.class);
                    intent.putExtra("currentImageName",currentImageName);
                    startActivity(intent);
                } else {
                    Toast.makeText(SingleImageActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }
}