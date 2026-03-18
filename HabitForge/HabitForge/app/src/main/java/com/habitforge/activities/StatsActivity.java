package com.habitforge.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.habitforge.R;
import com.habitforge.adapters.HabitStatsAdapter;
import com.habitforge.database.DatabaseHelper;
import com.habitforge.models.Habit;
import com.habitforge.utils.SessionManager;

import java.util.List;

public class StatsActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private SessionManager session;
    private TextView tvHabits, tvCompletions, tvBest;
    private RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) { getSupportActionBar().setDisplayHomeAsUpEnabled(true); getSupportActionBar().setTitle("Statistics"); }
        db = DatabaseHelper.getInstance(this);
        session = new SessionManager(this);
        tvHabits = findViewById(R.id.tv_total_habits);
        tvCompletions = findViewById(R.id.tv_total_completions);
        tvBest = findViewById(R.id.tv_best_streak);
        recycler = findViewById(R.id.recycler_stats);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        loadStats();
    }

    private void loadStats() {
        List<Habit> habits = db.getHabitsForUser(session.getUserId());
        tvHabits.setText(String.valueOf(habits.size()));
        int total = 0, best = 0;
        for (Habit h : habits) { total += db.getTotalCompletions(h.getId()); if (h.getStreak() > best) best = h.getStreak(); }
        tvCompletions.setText(String.valueOf(total));
        tvBest.setText(best + " 🔥");
        habits.sort((a, b) -> b.getStreak() - a.getStreak());
        recycler.setAdapter(new HabitStatsAdapter(this, habits, db));
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
