package com.example.parseinstagram.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.parseinstagram.R;
import com.example.parseinstagram.activity.HomeActivity;
import com.example.parseinstagram.helper.BitmapScaler;
import com.example.parseinstagram.model.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class ComposeFragment extends Fragment {

    private final static String TAG = "ComposeFragment";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;

    public String photoFileName = "photo.jpg";
    private File photoFile;

    ProgressBar pb;
    private EditText etDescription;
    private Button btnCreate;
    private Button btnGetImage;
    private ImageView ivPreview;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pb = view.findViewById(R.id.pb);
        etDescription = view.findViewById(R.id.etDescription);
        btnCreate = view.findViewById(R.id.btnCreate);
        btnGetImage = view.findViewById(R.id.btnGetImage);
        ivPreview = view.findViewById(R.id.ivPreview);

        ivPreview.getLayoutParams().height = 0;

        setupButtons();
    }


    private void setupButtons() {
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (photoFile == null || ivPreview.getDrawable() == null) {
                    Toast.makeText(getContext(), "Need a photo to post", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Tried to post without a photo");
                } else {
                    final String description = etDescription.getText().toString();
                    final ParseUser user = ParseUser.getCurrentUser();

                    final ParseFile parseFile = new ParseFile(photoFile);

                    createPost(description, parseFile, user);
                }
            }
        });

        btnGetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });
    }

    private void createPost(String description, ParseFile imageFile, ParseUser user) {
        pb.setVisibility(View.VISIBLE);
        btnCreate.setEnabled(false);
        etDescription.setEnabled(false);
        btnGetImage.setEnabled(false);

        final Post newPost = new Post();
        newPost.setDescription(description);
        newPost.setImage(imageFile);
        newPost.setUser(user);

        newPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Post successfully saved");

                    etDescription.setText("");
                    ivPreview.setImageResource(0);
                    btnGetImage.setText("Camera");
                } else {
                    Log.e(TAG, "Error saving post");
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error creating post", Toast.LENGTH_SHORT).show();
                }

                pb.setVisibility(View.INVISIBLE);
                btnGetImage.setEnabled(true);
                etDescription.setEnabled(true);
                btnCreate.setEnabled(true);
            }
        });
    }


    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        photoFile = getPhotoFileUri(photoFileName);

        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }


    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

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

                ivPreview.setImageBitmap(takenImage);

                if (photoFile != null || ivPreview.getDrawable() != null) {
                    ivPreview.getLayoutParams().height = HomeActivity.screenWidth;
                    btnGetImage.setText("Replace Image");
                } else {
                    ivPreview.getLayoutParams().height = 0;
                    btnGetImage.setText("Camera");
                }

            } else {
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
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
