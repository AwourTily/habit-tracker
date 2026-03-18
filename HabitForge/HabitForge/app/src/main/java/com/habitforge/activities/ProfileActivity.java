package com.habitforge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.habitforge.R;
import com.habitforge.database.DatabaseHelper;
import com.habitforge.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private SessionManager session;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Profile");
        }

        session = new SessionManager(this);
        db = DatabaseHelper.getInstance(this);

        TextView tvUsername = findViewById(R.id.tv_username);
        TextView tvEmail = findViewById(R.id.tv_email);
        TextView tvMemberSince = findViewById(R.id.tv_member_since);
        TextView tvTotalHabits = findViewById(R.id.tv_total_habits);
        TextView tvTotalCompletions = findViewById(R.id.tv_total_completions);
        Button btnLogout = findViewById(R.id.btn_logout);

        tvUsername.setText(session.getUsername());
        tvEmail.setText(session.getEmail());

        com.habitforge.models.User user = db.getUserById(session.getUserId());
        if (user != null) {
            String since = new SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                    .format(new Date(user.getCreatedAt()));
            tvMemberSince.setText("Member since " + since);
        }

        int totalHabits = db.getHabitsForUser(session.getUserId()).size();
        int totalCompletions = db.getTotalCompletionsForUser(session.getUserId());
        tvTotalHabits.setText(totalHabits + " habits");
        tvTotalCompletions.setText(totalCompletions + " completions");

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Logout", (d, w) -> {
                        session.logout();
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
