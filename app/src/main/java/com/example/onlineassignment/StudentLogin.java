package com.example.onlineassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StudentLogin extends AppCompatActivity {

    EditText regEditText;

    String reg=null;
    Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);
        nextButton = (Button) findViewById(R.id.next);
        regEditText = (EditText)  findViewById(R.id.reg_number);


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reg = regEditText.getText().toString();
                if (reg != null){


                    SharedPreferences sharedPref = getSharedPreferences("MY_DATA",getApplicationContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.user_type),"student");
                    editor.putString(getString(R.string.user_name),null);
                    editor.putString(getString(R.string.reg),reg);
                    editor.commit();
                    Toast.makeText(getApplicationContext(),"student "+reg,Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }
            }
        });

    }
}