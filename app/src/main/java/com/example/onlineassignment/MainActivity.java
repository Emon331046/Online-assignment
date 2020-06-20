package com.example.onlineassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button testButton;
    Button assignmentButton ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testButton = (Button) findViewById(R.id.test);
        assignmentButton = (Button) findViewById(R.id.assignment);

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetConnectionCheck.isConnected(getApplicationContext())){

                    Intent intent = new Intent(getApplicationContext(),TestActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        });

        assignmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NetConnectionCheck.isConnected(getApplicationContext())){

                    Intent intent = new Intent(getApplicationContext(),AssignmentActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
}