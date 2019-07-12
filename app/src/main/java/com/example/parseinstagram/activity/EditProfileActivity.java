package com.example.parseinstagram.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.parseinstagram.R;
import com.example.parseinstagram.helper.BitmapScaler;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity {

    private final static String TAG = "EditProfileActivity";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    Context context;

    ImageView ivProfilePhoto;
    Button btnChangePhoto;
    EditText etName;
    EditText etUsername;
    EditText etWebsite;
    EditText etBio;
    Button btnDone;
    Button btnCancel;
    ProgressBar pb;

    ParseUser user;

    private String photoFileName = "photo.jpg";
    private File photoFile;
    private boolean didChangeProfileImage = false;


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
        pb = findViewById(R.id.pb);

        fillUserData();

        setupActionBar();

        btnChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });
    }

    private void setupActionBar() {
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.insta_grey)));

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.insta_action_bar);
        getSupportActionBar().setElevation(2);

        View view = getSupportActionBar().getCustomView();
        ImageView ivLeft = view.findViewById(R.id.ivLeft);
        ImageView ivCenter = view.findViewById(R.id.ivCenter);
        ImageView ivRight = view.findViewById(R.id.ivRight);
        TextView tvCenter = view.findViewById(R.id.tvCenter);
        TextView tvDone = view.findViewById(R.id.tvDone);
        btnDone = view.findViewById(R.id.btnDone);
        TextView tvCancel = view.findViewById(R.id.tvCancel);
        btnCancel = view.findViewById(R.id.btnCancel);

        tvCancel.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.VISIBLE);
        ivLeft.setVisibility(View.INVISIBLE);
        ivCenter.setVisibility(View.INVISIBLE);
        ivRight.setVisibility(View.INVISIBLE);
        tvCenter.setVisibility(View.VISIBLE);
        tvDone.setVisibility(View.VISIBLE);
        btnDone.setVisibility(View.VISIBLE);

        tvCenter.setText("Edit Profile");
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProfile();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void saveProfile() {
        btnDone.setEnabled(false);
        pb.setVisibility(View.INVISIBLE);

        user.setUsername(String.valueOf(etUsername.getText()));
        ParseUser.getCurrentUser().setUsername(String.valueOf(etUsername.getText()));

        user.put("fullname", String.valueOf(etName.getText()));
        user.put("website", String.valueOf(etWebsite.getText()));
        user.put("bio", String.valueOf(etBio.getText()));

        if (didChangeProfileImage) {
            final ParseFile parseFile = new ParseFile(photoFile);
            user.put("profileImage", parseFile);
        }

        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                btnDone.setEnabled(true);
                pb.setVisibility(View.VISIBLE);

                if (e == null) {
                    final Intent intent = new Intent(EditProfileActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e(TAG, "Error finding user.");
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error saving profile", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void fillUserData() {
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
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


    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        photoFile = getPhotoFileUri(photoFileName);

        Uri fileProvider = FileProvider.getUriForFile(getApplicationContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }


    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                didChangeProfileImage = true;

                //Code for this if statement was told to only copy and not fully understand what's going on
                Bitmap takenImage = rotateBitmapOrientation(photoFile.getAbsolutePath());

                File takenPhotoUri = getPhotoFileUri(photoFileName);
                Bitmap rawTakenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());

                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(rawTakenImage, HomeActivity.screenWidth);

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

                try {
                    File resizedFile = getPhotoFileUri(photoFileName + "_resized");
                    resizedFile.createNewFile();
                    FileOutputStream fos = new FileOutputStream(resizedFile);

                    fos.write(bytes.toByteArray());
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ivProfilePhoto.setImageBitmap(takenImage);

            } else {
                Toast.makeText(getApplicationContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /*
     * Code for this if statement was told to only copy and not fully understand what's going on
     */
    public Bitmap rotateBitmapOrientation(String photoFilePath) {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);

        return rotatedBitmap;
    }
}
