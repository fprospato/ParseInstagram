package com.example.parseinstagram.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.parseinstagram.R;
import com.example.parseinstagram.fragments.ComposeFragment;
import com.example.parseinstagram.fragments.PostsFragment;
import com.example.parseinstagram.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class HomeActivity extends AppCompatActivity {

    private final static String TAG = "HomeActivity";
    private static final String imagePath = "";

    private BottomNavigationView bottomNavigationView;

    public static int screenWidth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        getScreenWidth();

        setupNavigationBar();

        setupActionBar();
    }


    /*
     * gets screen width to use for the height of ivPostImage in item_post
     */
    private void getScreenWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
    }


    private void setupActionBar() {
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.insta_grey)));

        setHomeBarDesign();
    }

    private void setHomeBarDesign() {
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
        Button btnDone = view.findViewById(R.id.btnDone);

        tvDone.setVisibility(View.INVISIBLE);
        btnDone.setVisibility(View.INVISIBLE);
        ivLeft.setVisibility(View.VISIBLE);
        ivCenter.setVisibility(View.VISIBLE);
        ivRight.setVisibility(View.VISIBLE);
        tvCenter.setVisibility(View.INVISIBLE);

        ivRight.setImageResource(R.drawable.direct);
    }

    private void setComposeBarDesign() {
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
        Button btnDone = view.findViewById(R.id.btnDone);

        tvDone.setVisibility(View.INVISIBLE);
        btnDone.setVisibility(View.INVISIBLE);
        ivLeft.setVisibility(View.INVISIBLE);
        ivCenter.setVisibility(View.INVISIBLE);
        ivRight.setVisibility(View.INVISIBLE);
        tvCenter.setVisibility(View.VISIBLE);

        tvCenter.setText("Photo");
    }

    private void setProfileBarDesign() {
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.insta_action_bar);
        getSupportActionBar().setElevation(0);

        View view = getSupportActionBar().getCustomView();
        ImageView ivLeft = view.findViewById(R.id.ivLeft);
        ImageView ivCenter = view.findViewById(R.id.ivCenter);
        ImageView ivRight = view.findViewById(R.id.ivRight);
        TextView tvCenter = view.findViewById(R.id.tvCenter);
        TextView tvDone = view.findViewById(R.id.tvDone);
        Button btnDone = view.findViewById(R.id.btnDone);

        tvDone.setVisibility(View.INVISIBLE);
        btnDone.setVisibility(View.INVISIBLE);
        ivLeft.setVisibility(View.INVISIBLE);
        ivCenter.setVisibility(View.INVISIBLE);
        ivRight.setVisibility(View.VISIBLE);
        tvCenter.setVisibility(View.VISIBLE);

        tvCenter.setText(ParseUser.getCurrentUser().getUsername());

        ivRight.setImageResource(R.drawable.insta_logout);

        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();

                final Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupNavigationBar() {

        final FragmentManager fragmentManager = getSupportFragmentManager();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = new ComposeFragment();

                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        setHomeBarDesign();
                        fragment = new PostsFragment();
                        break;
                    case R.id.action_compose:
                        setComposeBarDesign();
                        fragment = new ComposeFragment();
                        break;
                    case R.id.action_profile:
                        setProfileBarDesign();
                        fragment = new ProfileFragment();
                        break;
                    default:
                        break;
                }

                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }
}
