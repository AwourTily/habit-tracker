package com.habitforge.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.habitforge.R;
import com.habitforge.adapters.LogAdapter;
import com.habitforge.database.DatabaseHelper;
import com.habitforge.models.Habit;
import com.habitforge.models.HabitLog;

import java.util.List;

public class HabitDetailActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private Habit habit;
    private int habitId;
    private TextView tvName, tvDesc, tvStreak, tvTotal, tvFreq, tvStatus;
    private Button btnToggle, btnEdit;
    private RecyclerView recyclerLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) { getSupportActionBar().setDisplayHomeAsUpEnabled(true); getSupportActionBar().setTitle("Detail"); }

        db = DatabaseHelper.getInstance(this);
        habitId = getIntent().getIntExtra("habit_id", -1);

        tvName = findViewById(R.id.tv_name); tvDesc = findViewById(R.id.tv_desc);
        tvStreak = findViewById(R.id.tv_streak); tvTotal = findViewById(R.id.tv_total);
        tvFreq = findViewById(R.id.tv_freq); tvStatus = findViewById(R.id.tv_status);
        btnToggle = findViewById(R.id.btn_toggle); btnEdit = findViewById(R.id.btn_edit);
        recyclerLogs = findViewById(R.id.recycler_logs);
        recyclerLogs.setLayoutManager(new LinearLayoutManager(this));

        btnEdit.setOnClickListener(v -> {
            Intent i = new Intent(this, AddEditHabitActivity.class);
            i.putExtra("habit_id", habitId); startActivity(i);
        });
        loadData();
    }

    @Override protected void onResume() { super.onResume(); loadData(); }

    private void loadData() {
        if (habitId == -1) { finish(); return; }
        habit = db.getHabitById(habitId);
        if (habit == null) { finish(); return; }

        tvName.setText(habit.getEmoji() + "  " + habit.getName());
        tvDesc.setText(habit.getDescription() != null && !habit.getDescription().isEmpty() ? habit.getDescription() : "No description");
        tvStreak.setText("🔥 " + habit.getStreak() + " day streak");
        tvTotal.setText(db.getTotalCompletions(habitId) + " total completions");
        tvFreq.setText(habit.getFrequency().substring(0,1).toUpperCase() + habit.getFrequency().substring(1));
        try { tvStreak.setTextColor(Color.parseColor(habit.getColor())); } catch (Exception ignored) {}

        if (habit.isCompletedToday()) {
            tvStatus.setText("✅  Completed today!");
            tvStatus.setTextColor(Color.parseColor("#4CAF50"));
            btnToggle.setText("Undo Today");
        } else {
            tvStatus.setText("⬜  Not done yet");
            tvStatus.setTextColor(Color.parseColor("#9E9E9E"));
            btnToggle.setText("Mark Done");
        }
        btnToggle.setOnClickListener(v -> {
            if (habit.isCompletedToday()) db.removeCompletion(habitId);
            else db.logCompletion(habitId, "");
            loadData();
        });
        List<HabitLog> logs = db.getLogsForHabit(habitId);
        recyclerLogs.setAdapter(new LogAdapter(this, logs));
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
