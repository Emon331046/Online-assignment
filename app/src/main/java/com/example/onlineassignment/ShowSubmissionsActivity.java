package com.example.onlineassignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowSubmissionsActivity extends AppCompatActivity implements SubmissionAdapter.OnItemClickListener{

    private RecyclerView mRecyclerView;
    private SubmissionAdapter mAdapter;
   // private ProgressBar mProgressCircle;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private List<Upload> mUploads;

    static public Map<String,String> submissionMap = new HashMap<String, String>();;


    private String assignmentName=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_submissions);

        if (NetConnectionCheck.isConnected(getApplicationContext())){
            final LoadingDialog loadingDialog = new LoadingDialog(ShowSubmissionsActivity.this);
            loadingDialog.startLoading();

            // create the get Intent object
            Intent intent = getIntent();

            // receive the value by getStringExtra() method
            // and key must be same which is send by first activity

            final String currentImageName = intent.getStringExtra("currentImageName");
            assignmentName = currentImageName;

            mRecyclerView = findViewById(R.id.recycler_view);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            //  mProgressCircle = findViewById(R.id.progress_circle);
            mUploads = new ArrayList<>();
            mAdapter = new SubmissionAdapter(ShowSubmissionsActivity.this, mUploads);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener( ShowSubmissionsActivity.this);
            mStorage = FirebaseStorage.getInstance();

            Toast.makeText(getApplicationContext(),assignmentName,Toast.LENGTH_SHORT).show();
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("submissions/"+assignmentName);
            mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mUploads.clear();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Upload upload = postSnapshot.getValue(Upload.class);
                        upload.setKey(postSnapshot.getKey());
                        mUploads.add(upload);
                    }
                    mAdapter.notifyDataSetChanged();
                    // mProgressCircle.setVisibility(View.INVISIBLE);
                    loadingDialog.dismissLoading();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(ShowSubmissionsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    // mProgressCircle.setVisibility(View.INVISIBLE);
                    loadingDialog.dismissLoading();
                }
            });
        } else {
            Toast.makeText(ShowSubmissionsActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onItemClick(String currentImageUrl) {

        Toast.makeText(this, "Normal click" , Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(ShowSubmissionsActivity.this, SingleSubmissionActivity.class);

        intent.putExtra("currentSubmissionUrl",currentImageUrl);
        startActivity(intent);
    }
    @Override
    public void onWhatEverClick(int position) {
        Toast.makeText(this, "Whatever click at position: " + position, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onDeleteClick(int position) {
        Upload selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();
        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseRef.child(selectedKey).removeValue();
                Toast.makeText(ShowSubmissionsActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }
}
