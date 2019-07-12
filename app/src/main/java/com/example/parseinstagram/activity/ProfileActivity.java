package com.example.parseinstagram.activity;

import android.app.ActionBar;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.parseinstagram.R;
import com.example.parseinstagram.fragments.ProfileFragment;

public class ProfileActivity extends AppCompatActivity {

    String username;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        username = getIntent().getStringExtra("username");
        userId = getIntent().getStringExtra("userId");

        final FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = new ProfileFragment();

        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        fragment.setArguments(bundle);

        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();

        setupActionBar();
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
        Button btnDone = view.findViewById(R.id.btnDone);
        TextView tvCancel = view.findViewById(R.id.tvCancel);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        tvCancel.setVisibility(View.INVISIBLE);
        btnCancel.setVisibility(View.VISIBLE);
        tvDone.setVisibility(View.INVISIBLE);
        btnDone.setVisibility(View.INVISIBLE);
        ivLeft.setVisibility(View.VISIBLE);
        ivCenter.setVisibility(View.INVISIBLE);
        ivRight.setVisibility(View.VISIBLE);
        tvCenter.setVisibility(View.VISIBLE);

        ivLeft.setImageResource(R.drawable.insta_back_arrow);
        tvCenter.setText(username);
        ivRight.setImageResource(R.drawable.ufi_more);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
