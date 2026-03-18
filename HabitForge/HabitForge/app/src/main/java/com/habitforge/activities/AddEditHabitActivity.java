package com.habitforge.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.habitforge.R;
import com.habitforge.database.DatabaseHelper;
import com.habitforge.models.Habit;
import com.habitforge.utils.SessionManager;

public class AddEditHabitActivity extends AppCompatActivity {

    private EditText etName, etDescription;
    private RadioGroup rgFrequency;
    private ChipGroup cgColor, cgEmoji;
    private Button btnSave, btnDelete;
    private DatabaseHelper db;
    private SessionManager session;
    private int habitId = -1;
    private Habit existing;

    private static final String[] COLORS = {"#4CAF50","#2196F3","#F44336","#FF9800","#9C27B0","#00BCD4","#FF5722","#607D8B"};
    private static final String[] EMOJIS = {"⭐","💪","📚","💧","😴","🧘","🏃","🎯","🥗","💊","🎸","✍️"};

    private String selectedColor = "#4CAF50";
    private String selectedEmoji = "⭐";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_habit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = DatabaseHelper.getInstance(this);
        session = new SessionManager(this);

        etName       = findViewById(R.id.et_name);
        etDescription= findViewById(R.id.et_description);
        rgFrequency  = findViewById(R.id.rg_frequency);
        cgColor      = findViewById(R.id.cg_color);
        cgEmoji      = findViewById(R.id.cg_emoji);
        btnSave      = findViewById(R.id.btn_save);
        btnDelete    = findViewById(R.id.btn_delete);

        buildColorChips();
        buildEmojiChips();

        habitId = getIntent().getIntExtra("habit_id", -1);
        if (habitId != -1) {
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Edit Habit");
            btnDelete.setVisibility(View.VISIBLE);
            existing = db.getHabitById(habitId);
            populateFields();
        } else {
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("New Habit");
        }

        btnSave.setOnClickListener(v -> save());
        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void buildColorChips() {
        String[] names = {"Green","Blue","Red","Orange","Purple","Cyan","Deep Orange","Grey"};
        for (int i = 0; i < COLORS.length; i++) {
            Chip chip = new Chip(this);
            chip.setText(names[i]);
            chip.setCheckable(true);
            final String c = COLORS[i];
            chip.setChipBackgroundColorResource(android.R.color.transparent);
            chip.setCheckedIconVisible(true);
            chip.setTag(c);
            if (i == 0) { chip.setChecked(true); }
            cgColor.addView(chip);
        }
        cgColor.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                View v = group.findViewById(checkedIds.get(0));
                if (v != null && v.getTag() != null) selectedColor = (String) v.getTag();
            }
        });
    }

    private void buildEmojiChips() {
        for (String emoji : EMOJIS) {
            Chip chip = new Chip(this);
            chip.setText(emoji);
            chip.setCheckable(true);
            chip.setTag(emoji);
            if (emoji.equals("⭐")) chip.setChecked(true);
            cgEmoji.addView(chip);
        }
        cgEmoji.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                View v = group.findViewById(checkedIds.get(0));
                if (v != null && v.getTag() != null) selectedEmoji = (String) v.getTag();
            }
        });
    }

    private void populateFields() {
        etName.setText(existing.getName());
        etDescription.setText(existing.getDescription());
        if ("weekly".equals(existing.getFrequency())) rgFrequency.check(R.id.rb_weekly);
        selectedColor = existing.getColor();
        selectedEmoji = existing.getEmoji();
    }

    private void save() {
        String name = etName.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        if (TextUtils.isEmpty(name)) { etName.setError("Name required"); return; }
        String freq = rgFrequency.getCheckedRadioButtonId() == R.id.rb_weekly ? "weekly" : "daily";

        if (habitId == -1) {
            Habit h = new Habit(String.valueOf(session.getUserId()), name, desc, freq, selectedColor, selectedEmoji);
            if (db.insertHabit(h) != -1) { Toast.makeText(this, "Habit created! 🎯", Toast.LENGTH_SHORT).show(); finish(); }
        } else {
            existing.setName(name); existing.setDescription(desc); existing.setFrequency(freq);
            existing.setColor(selectedColor); existing.setEmoji(selectedEmoji);
            db.updateHabit(existing);
            Toast.makeText(this, "Habit updated!", Toast.LENGTH_SHORT).show(); finish();
        }
    }

    private void confirmDelete() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Habit")
                .setMessage("Delete \"" + existing.getName() + "\"? All history will be lost.")
                .setPositiveButton("Delete", (d, w) -> { db.deleteHabit(habitId); finish(); })
                .setNegativeButton("Cancel", null).show();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
