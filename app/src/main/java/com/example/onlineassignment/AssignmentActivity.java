package com.example.onlineassignment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class AssignmentActivity  extends AppCompatActivity implements ImageAdapter.OnItemClickListener {
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
   // private ProgressBar mProgressCircle;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private List<Upload> mUploads;
    private Button newAssignButton;
    static public Map<String,String> assignMap = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);



        newAssignButton = (Button) findViewById(R.id.new_assign);
        SharedPreferences sharedPref = getSharedPreferences("MY_DATA",this.MODE_PRIVATE);

        String userType = sharedPref.getString(getString(R.string.user_type),null);

      //  Toast.makeText(getApplicationContext(),"student "+userType,Toast.LENGTH_SHORT).show();

        if (userType !=null &&  userType.equals("student")){

            Toast.makeText(getApplicationContext(),"student",Toast.LENGTH_SHORT).show();
            newAssignButton.setVisibility(View.GONE);
        }
        if (NetConnectionCheck.isConnected(getApplicationContext())){
            final LoadingDialog loadingDialog = new LoadingDialog(AssignmentActivity.this);
            loadingDialog.startLoading();

            newAssignButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AssignmentActivity.this, AssignmentUploadActivity.class);
                    startActivity(intent);
                }
            });

            mRecyclerView = findViewById(R.id.recycler_view);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            ///mProgressCircle = findViewById(R.id.progress_circle);
            mUploads = new ArrayList<>();
            mAdapter = new ImageAdapter(AssignmentActivity.this, mUploads);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(AssignmentActivity.this);
            mStorage = FirebaseStorage.getInstance();
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("assign");
            mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mUploads.clear();
                    assignMap.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Upload upload = postSnapshot.getValue(Upload.class);
                        assignMap.put(upload.getName().toLowerCase().toString(),upload.getImageUrl().toString());

                        upload.setKey(postSnapshot.getKey());
                        mUploads.add(upload);
                    }
                    mAdapter.notifyDataSetChanged();
                    loadingDialog.dismissLoading();
                    //mProgressCircle.setVisibility(View.INVISIBLE);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(AssignmentActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismissLoading();
                    //mProgressCircle.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            Toast.makeText(AssignmentActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }



    }
    @Override
    public void onItemClick(String currentImageUrl,String currentImageName) {

       // Toast.makeText(this, "Normal click at position: + position", Toast.LENGTH_SHORT).show();

        if (NetConnectionCheck.isConnected(getApplicationContext())){

              Intent intent = new Intent(AssignmentActivity.this, SingleImageActivity.class);

            intent.putExtra("currentImageUrl",currentImageUrl);
            intent.putExtra("currentImageName",currentImageName);
            startActivity(intent);
        } else {
            Toast.makeText(AssignmentActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onDeleteClick(int position) {
        SharedPreferences sharedPref = getSharedPreferences("MY_DATA", this.MODE_PRIVATE);

        String userType = sharedPref.getString(getString(R.string.user_type), null);

        if (userType.equals("teacher")) {
            final Upload selectedItem = mUploads.get(position);
            final String selectedKey = selectedItem.getKey();
            StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mDatabaseRef.child(selectedKey).removeValue();
                    assignMap.remove(selectedItem.getName().toLowerCase().toString());

                    //assignMap.put(selectedItem.getName().toLowerCase().toString(),selectedItem.getImageUrl().toString());
                    Toast.makeText(AssignmentActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                }
            });
        } else {

            Toast.makeText(AssignmentActivity.this, "usertype isn't  teacher.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }
}
