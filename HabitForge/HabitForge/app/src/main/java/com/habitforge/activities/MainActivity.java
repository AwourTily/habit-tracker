package com.habitforge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.habitforge.R;
import com.habitforge.adapters.HabitAdapter;
import com.habitforge.database.DatabaseHelper;
import com.habitforge.models.Habit;
import com.habitforge.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements HabitAdapter.HabitClickListener {

    private RecyclerView recycler;
    private HabitAdapter adapter;
    private DatabaseHelper db;
    private SessionManager session;
    private TextView tvDate, tvProgress, tvGreeting, tvEmpty;
    private List<Habit> habits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = DatabaseHelper.getInstance(this);
        session = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("HabitForge");

        tvDate     = findViewById(R.id.tv_date);
        tvProgress = findViewById(R.id.tv_progress);
        tvGreeting = findViewById(R.id.tv_greeting);
        tvEmpty    = findViewById(R.id.tv_empty);
        recycler   = findViewById(R.id.recycler_habits);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        tvGreeting.setText("Hello, " + session.getUsername() + " 👋");
        tvDate.setText(new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(new Date()));

        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> startActivity(new Intent(this, AddEditHabitActivity.class)));

        loadHabits();
    }

    @Override protected void onResume() { super.onResume(); loadHabits(); }

    private void loadHabits() {
        habits = db.getHabitsForUser(session.getUserId());
        if (habits.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
            tvProgress.setText("Add your first habit below 🌱");
        } else {
            tvEmpty.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);
            long done = 0;
            for (Habit h : habits) if (h.isCompletedToday()) done++;
            tvProgress.setText(done + " / " + habits.size() + " done today");
            if (adapter == null) {
                adapter = new HabitAdapter(this, habits, this);
                recycler.setAdapter(adapter);
            } else { adapter.updateHabits(habits); }
        }
    }

    @Override public void onHabitCheck(Habit h, boolean checked) {
        if (checked) db.logCompletion(h.getId(), "");
        else db.removeCompletion(h.getId());
        loadHabits();
    }
    @Override public void onHabitClick(Habit h) {
        Intent i = new Intent(this, HabitDetailActivity.class);
        i.putExtra("habit_id", h.getId()); startActivity(i);
    }
    @Override public void onHabitEdit(Habit h) {
        Intent i = new Intent(this, AddEditHabitActivity.class);
        i.putExtra("habit_id", h.getId()); startActivity(i);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu); return true;
    }
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_stats) { startActivity(new Intent(this, StatsActivity.class)); return true; }
        if (id == R.id.action_logout) {
            session.logout();
            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i); return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
