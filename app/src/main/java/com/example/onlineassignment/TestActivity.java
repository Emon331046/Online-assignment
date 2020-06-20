package com.example.onlineassignment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestActivity extends AppCompatActivity implements testAdapter.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private testAdapter mAdapter;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private List<TestModel> tests;

    static public Map<String,String> testmap = new HashMap<String, String>();
    private Button AddTestButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        AddTestButton = (Button) findViewById(R.id.new_test);

        SharedPreferences sharedPref = getSharedPreferences("MY_DATA",this.MODE_PRIVATE);

        String userType = sharedPref.getString(getString(R.string.user_type),null);

        final LoadingDialog loadingDialog = new LoadingDialog(TestActivity.this);
        loadingDialog.startLoading();


          Toast.makeText(getApplicationContext(),"Usertype : "+userType,Toast.LENGTH_SHORT).show();

        if (userType !=null &&  userType.equals("student")){

           // Toast.makeText(getApplicationContext(),"student",Toast.LENGTH_SHORT).show();
            AddTestButton.setVisibility(View.GONE);
        }



        AddTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestActivity.this, TestUploadActivity.class);
                startActivity(intent);
            }
        });

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ///mProgressCircle = findViewById(R.id.progress_circle);
        tests = new ArrayList<>();
        mAdapter = new testAdapter(TestActivity.this, tests);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(TestActivity.this);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("tests");
        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tests.clear();
                testmap.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    TestModel testModel = postSnapshot.getValue(TestModel.class);
                    testModel.setKey(postSnapshot.getKey());
                    testmap.put(testModel.getTestName().toLowerCase().toString().trim(),testModel.getTestLink().toString().trim());

                    tests.add(testModel);
                }
                mAdapter.notifyDataSetChanged();
                loadingDialog.dismissLoading();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(TestActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismissLoading();
            }
        });
    }

    @Override
    public void onItemClick(String currentImageUrl) {
        if (isValid(currentImageUrl)){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentImageUrl));

            startActivity(intent);
        } else
        {
            Toast.makeText(this, "The url is not valid", Toast.LENGTH_SHORT).show();
        }

//        Toast.makeText(this, "Normal click at position: + position", Toast.LENGTH_SHORT).show();
//

    }

    @Override
    public void onDeleteClick(int position) {
        SharedPreferences sharedPref = getSharedPreferences("MY_DATA",this.MODE_PRIVATE);

        String userType = sharedPref.getString(getString(R.string.user_type),null);

        if (userType.equals("teacher")){
            TestModel selectedItem = tests.get(position);
            final String selectedKey = selectedItem.getKey();

            mDatabaseRef.child(selectedKey).removeValue();
            testmap.remove(selectedItem.getTestName().toLowerCase().toString());

            // mAdapter.notifyDataSetChanged();
            Toast.makeText(TestActivity.this, "Item deleted "+selectedKey, Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(TestActivity.this, "usertype isn't  teacher.", Toast.LENGTH_SHORT).show();
        }


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mDatabaseRef.removeEventListener(mDBListener);
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