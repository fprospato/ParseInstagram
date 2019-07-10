package com.example.parseinstagram.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parseinstagram.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignup;
    private ProgressBar pb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkForCurrentUser();

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        pb = findViewById(R.id.pb);

        setupButtons();
    }

    private void checkForCurrentUser() {
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser != null) {
            goToHome();
        }
    }

    private void setupButtons() {
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasTextCheck()) {
                    pb.setVisibility(View.VISIBLE);
                    final String username = etUsername.getText().toString().trim();
                    final String password = etPassword.getText().toString().trim();
                    signup(username, password);
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasTextCheck()) {
                    pb.setVisibility(View.VISIBLE);
                    final String username = etUsername.getText().toString().trim();
                    final String password = etPassword.getText().toString().trim();
                    login(username, password);
                }
            }
        });
    }

    private boolean hasTextCheck() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.equals("") || password.equals("")) {
            Toast.makeText(this, "Please enter both a username and password", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    private void signup(String username, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                pb.setVisibility(View.INVISIBLE);

                if (e == null) {
                    Log.d(TAG, "Signup successful");

                    goToHome();
                } else {
                    Log.e(TAG, "Signup was not successful");
                    e.printStackTrace();
                }
            }
        });
    }


    private void login(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                pb.setVisibility(View.INVISIBLE);

                if (e == null) {
                    Log.d(TAG, "Login successful");

                    goToHome();
                } else {
                    Log.e(TAG, "Login was not successful");
                    e.printStackTrace();
                }
            }
        });
    }


    private void goToHome() {
        final Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);

        finish();
    }

}
