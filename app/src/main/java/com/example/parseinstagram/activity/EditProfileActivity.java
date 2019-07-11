package com.example.parseinstagram.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.parseinstagram.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class EditProfileActivity extends AppCompatActivity {

    private final static String TAG = "EditProfileActivity";
    Context context;

    ImageView ivProfilePhoto;
    Button btnChangePhoto;
    EditText etName;
    EditText etUsername;
    EditText etWebsite;
    EditText etBio;

    ParseObject user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);
        etName = findViewById(R.id.etName);
        etUsername = findViewById(R.id.etUsername);
        etWebsite = findViewById(R.id.etWebsite);
        etBio = findViewById(R.id.etBio);

        fillUserData();
    }


    private void fillUserData() {
        ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("_User");
        userQuery.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    user = object;

                    etName.setText(object.getString("fullname"));
                    etUsername.setText(object.getString("username"));
                    etWebsite.setText(object.getString("website"));
                    etBio.setText(object.getString("bio"));

                    ParseFile profileImage = object.getParseFile("profileImage");
                    if (profileImage != null) {
                        Glide.with(getApplicationContext())
                                .load(profileImage.getUrl())
                                .apply(RequestOptions.circleCropTransform())
                                .into(ivProfilePhoto);
                    }
                } else {
                    Log.e(TAG, "Error finding user.");
                    e.printStackTrace();
                }
            }
        });
    }
}
