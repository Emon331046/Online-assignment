package com.example.onlineassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TeacherLogin extends AppCompatActivity {
    EditText emailEditText;
    EditText passwordEditText;
    String email=null;
    String password=null;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login);

        loginButton = (Button) findViewById(R.id.login);
        emailEditText = findViewById(R.id.email);
        passwordEditText =  findViewById(R.id.password);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();
                if (email.equals("kamalsir") && password.equals("kamalsir")){

                    SharedPreferences sharedPref = getSharedPreferences("MY_DATA",getApplicationContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.user_type),"teacher");
                    editor.putString(getString(R.string.user_name),"kamal sir");
                    editor.putString(getString(R.string.reg),null);
                    editor.commit();

                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }
            }
        });



    }
}