package com.example.onlineassignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;

public class TestUploadActivity extends AppCompatActivity {

    private Button mButtonUpload;
    private EditText testName;
    private EditText testLink;


    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_upload);



        mButtonUpload = findViewById(R.id.button_upload);
        testName = findViewById(R.id.test_name);
        testLink = findViewById(R.id.test_link);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("tests");



        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NetConnectionCheck.isConnected(getApplicationContext())){
                    if (TestActivity.testmap.get("test-"+testName.getText().toString().trim().toLowerCase())==null){
                       // loadingDialog.startLoading();
                        uploadFile();
                    } else {

                        Toast.makeText(TestUploadActivity.this, "Test name already exists " +"test-"+testName.getText().toString().trim().toLowerCase(), Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(TestUploadActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    private void uploadFile(){
        if (testLink.getText().toString().trim().isEmpty() || testName.getText().toString().isEmpty()){
            Toast.makeText(TestUploadActivity.this, " name or link is empty", Toast.LENGTH_SHORT).show();
        } else if (isValid(testLink.getText().toString().trim())){
            TestModel testModel = new TestModel("test-"+ testName.getText().toString().trim(),
                    testLink.getText().toString().trim());

            mDatabaseRef.push().setValue(testModel);
            Toast.makeText(TestUploadActivity.this, " test upload done !! ", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(TestUploadActivity.this, TestActivity.class);
            startActivity(intent);
        } else {

            Toast.makeText(TestUploadActivity.this, " the url is not valid   ", Toast.LENGTH_SHORT).show();
        }


    }
    private boolean isValid(String urlString) {
        try {
            URL url = new URL(urlString);
            return URLUtil.isValidUrl(String.valueOf(url)) && Patterns.WEB_URL.matcher(String.valueOf(url)).matches();
        } catch (MalformedURLException e) {

        }

        return false;
    }
}