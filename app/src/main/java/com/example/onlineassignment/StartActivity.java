package com.example.onlineassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {
    Button teacherButton;
    Button studentButton ;
    String userType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        teacherButton = (Button) findViewById(R.id.teacher);
        studentButton = (Button) findViewById(R.id.student);

        teacherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),TeacherLogin.class);
                startActivity(intent);

            }
        });

        studentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),StudentLogin.class);
                startActivity(intent);

            }
        });

        SharedPreferences sharedPref = getSharedPreferences("MY_DATA",this.MODE_PRIVATE);

        userType = sharedPref.getString(getString(R.string.user_type),null);

        if (userType != null){
            if (userType.equals(getString(R.string.teacher_type))){
                Intent intent = new Intent(getApplicationContext(),AssignmentActivity.class);
                startActivity(intent);
            } else if (userType.equals(getString(R.string.student_type))){
                Intent intent = new Intent(getApplicationContext(),AssignmentActivity.class);
                startActivity(intent);
            }
        }

    }
}