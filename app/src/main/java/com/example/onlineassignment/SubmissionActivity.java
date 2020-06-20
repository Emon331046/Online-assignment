package com.example.onlineassignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class SubmissionActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    private Uri mImageUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    private String assignmentName=null;
    private LoadingDialog loadingDialog =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission);
        mButtonChooseImage = findViewById(R.id.button_choose_image);
        mButtonUpload = findViewById(R.id.button_upload);
        mImageView = findViewById(R.id.image_view);
        mProgressBar = findViewById(R.id.progress_bar);

        loadingDialog = new LoadingDialog(SubmissionActivity.this);



        //############ASSIGNENTNAME###############
        // create the get Intent object
        Intent intent = getIntent();

        // receive the value by getStringExtra() method
        // and key must be same which is send by first activity
        final String currentImageName = intent.getStringExtra("currentImageName");
        assignmentName = currentImageName;
        //Toast.makeText(getApplicationContext(),assignmentName,Toast.LENGTH_SHORT).show();
        mStorageRef = FirebaseStorage.getInstance().getReference("submissions/"+assignmentName);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("submissions/"+assignmentName);

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(SubmissionActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {

                    if (NetConnectionCheck.isConnected(getApplicationContext())){


                        loadingDialog.startLoading();
                        uploadFile();
                    } else {
                        Toast.makeText(SubmissionActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.with(this).load(mImageUri).into(mImageView);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }




    private void uploadFile(){
        if (mImageUri != null) {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));


            fileReference.putFile(mImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
            {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                {

                    loadingDialog.dismissLoading();
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>()
            {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if (task.isSuccessful())
                    {
                        Uri downloadUri = task.getResult();

                        Log.e("TAG", "then: " + downloadUri.toString());

                        SharedPreferences sharedPref = getSharedPreferences("MY_DATA",getApplicationContext().MODE_PRIVATE);

                        String userType = sharedPref.getString(getString(R.string.user_type),null);

                        if (userType.equals("student")){
                           // Toast.makeText(getApplicationContext(),"i am here",Toast.LENGTH_SHORT).show();
                            String userReg = sharedPref.getString(getString(R.string.reg),null);
                            if (userReg!= null){
                                //Toast.makeText(getApplicationContext(),"i am here ",Toast.LENGTH_SHORT).show();

                                Upload upload = new Upload("submission("+userReg+")",
                                        downloadUri.toString());

                                mDatabaseRef.push().setValue(upload);
                            }
                        }
                        Intent intent = new Intent(SubmissionActivity.this, ShowSubmissionsActivity.class);
                        intent.putExtra("currentImageName",assignmentName);

                        startActivity(intent);



                    } else
                    {
                        Toast.makeText(SubmissionActivity.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            loadingDialog.dismissLoading();

        }
    }
}