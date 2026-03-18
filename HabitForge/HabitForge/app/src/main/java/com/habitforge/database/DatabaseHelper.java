package com.habitforge.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.habitforge.models.Habit;
import com.habitforge.models.HabitLog;
import com.habitforge.models.User;
import com.habitforge.utils.PasswordUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "HabitForge.db";
    private static final int DB_VERSION = 1;

    // Users table
    public static final String TABLE_USERS = "users";
    public static final String U_ID = "id";
    public static final String U_USERNAME = "username";
    public static final String U_EMAIL = "email";
    public static final String U_PASSWORD = "password_hash";
    public static final String U_AVATAR_COLOR = "avatar_color";
    public static final String U_CREATED_AT = "created_at";

    // Habits table
    public static final String TABLE_HABITS = "habits";
    public static final String H_ID = "id";
    public static final String H_USER_ID = "user_id";
    public static final String H_NAME = "name";
    public static final String H_DESC = "description";
    public static final String H_FREQUENCY = "frequency";
    public static final String H_COLOR = "color";
    public static final String H_EMOJI = "emoji";
    public static final String H_STREAK = "streak";
    public static final String H_CREATED_AT = "created_at";

    // Logs table
    public static final String TABLE_LOGS = "habit_logs";
    public static final String L_ID = "id";
    public static final String L_HABIT_ID = "habit_id";
    public static final String L_COMPLETED_AT = "completed_at";
    public static final String L_NOTE = "note";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context ctx) {
        if (instance == null) instance = new DatabaseHelper(ctx.getApplicationContext());
        return instance;
    }

    private DatabaseHelper(Context context) { super(context, DB_NAME, null, DB_VERSION); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                U_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                U_USERNAME + " TEXT NOT NULL, " +
                U_EMAIL + " TEXT UNIQUE NOT NULL, " +
                U_PASSWORD + " TEXT NOT NULL, " +
                U_AVATAR_COLOR + " TEXT DEFAULT '#4CAF50', " +
                U_CREATED_AT + " INTEGER)");

        db.execSQL("CREATE TABLE " + TABLE_HABITS + " (" +
                H_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                H_USER_ID + " INTEGER NOT NULL, " +
                H_NAME + " TEXT NOT NULL, " +
                H_DESC + " TEXT, " +
                H_FREQUENCY + " TEXT DEFAULT 'daily', " +
                H_COLOR + " TEXT DEFAULT '#4CAF50', " +
                H_EMOJI + " TEXT DEFAULT '⭐', " +
                H_STREAK + " INTEGER DEFAULT 0, " +
                H_CREATED_AT + " INTEGER, " +
                "FOREIGN KEY(" + H_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + U_ID + "))");

        db.execSQL("CREATE TABLE " + TABLE_LOGS + " (" +
                L_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                L_HABIT_ID + " INTEGER NOT NULL, " +
                L_COMPLETED_AT + " INTEGER, " +
                L_NOTE + " TEXT, " +
                "FOREIGN KEY(" + L_HABIT_ID + ") REFERENCES " + TABLE_HABITS + "(" + H_ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int o, int n) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HABITS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // ── USER ─────────────────────────────────────────────────────────────────────

    public long registerUser(String username, String email, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(U_USERNAME, username);
        cv.put(U_EMAIL, email.toLowerCase().trim());
        cv.put(U_PASSWORD, PasswordUtils.hashPassword(password));
        cv.put(U_CREATED_AT, System.currentTimeMillis());
        try { return db.insertOrThrow(TABLE_USERS, null, cv); }
        catch (Exception e) { return -1; }
    }

    public User loginUser(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_USERS, null,
                U_EMAIL + "=?", new String[]{email.toLowerCase().trim()},
                null, null, null);
        User user = null;
        if (c.moveToFirst()) {
            String stored = c.getString(c.getColumnIndexOrThrow(U_PASSWORD));
            if (PasswordUtils.verifyPassword(password, stored)) {
                user = new User();
                user.setId(c.getInt(c.getColumnIndexOrThrow(U_ID)));
                user.setUsername(c.getString(c.getColumnIndexOrThrow(U_USERNAME)));
                user.setEmail(c.getString(c.getColumnIndexOrThrow(U_EMAIL)));
                user.setAvatarColor(c.getString(c.getColumnIndexOrThrow(U_AVATAR_COLOR)));
                user.setCreatedAt(c.getLong(c.getColumnIndexOrThrow(U_CREATED_AT)));
            }
        }
        c.close();
        return user;
    }

    public boolean isEmailTaken(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_USERS, new String[]{U_ID},
                U_EMAIL + "=?", new String[]{email.toLowerCase().trim()}, null, null, null);
        boolean taken = c.getCount() > 0;
        c.close();
        return taken;
    }

    // ── HABITS ───────────────────────────────────────────────────────────────────

    public long insertHabit(Habit h) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(H_USER_ID, Integer.parseInt(h.getUserId()));
        cv.put(H_NAME, h.getName());
        cv.put(H_DESC, h.getDescription());
        cv.put(H_FREQUENCY, h.getFrequency());
        cv.put(H_COLOR, h.getColor());
        cv.put(H_EMOJI, h.getEmoji());
        cv.put(H_STREAK, 0);
        cv.put(H_CREATED_AT, System.currentTimeMillis());
        return db.insert(TABLE_HABITS, null, cv);
    }

    public List<Habit> getHabitsForUser(int userId) {
        List<Habit> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_HABITS, null,
                H_USER_ID + "=?", new String[]{String.valueOf(userId)},
                null, null, H_NAME + " ASC");
        if (c.moveToFirst()) do {
            Habit h = cursorToHabit(c);
            h.setCompletedToday(isCompletedToday(h.getId()));
            list.add(h);
        } while (c.moveToNext());
        c.close();
        return list;
    }

    public Habit getHabitById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_HABITS, null,
                H_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        Habit h = null;
        if (c.moveToFirst()) {
            h = cursorToHabit(c);
            h.setCompletedToday(isCompletedToday(id));
        }
        c.close();
        return h;
    }

    public int updateHabit(Habit h) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(H_NAME, h.getName());
        cv.put(H_DESC, h.getDescription());
        cv.put(H_FREQUENCY, h.getFrequency());
        cv.put(H_COLOR, h.getColor());
        cv.put(H_EMOJI, h.getEmoji());
        cv.put(H_STREAK, h.getStreak());
        return db.update(TABLE_HABITS, cv, H_ID + "=?", new String[]{String.valueOf(h.getId())});
    }

    public void deleteHabit(int habitId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_LOGS, L_HABIT_ID + "=?", new String[]{String.valueOf(habitId)});
        db.delete(TABLE_HABITS, H_ID + "=?", new String[]{String.valueOf(habitId)});
    }

    // ── LOGS ─────────────────────────────────────────────────────────────────────

    public long logCompletion(int habitId, String note) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(L_HABIT_ID, habitId);
        cv.put(L_COMPLETED_AT, System.currentTimeMillis());
        cv.put(L_NOTE, note);
        long r = db.insert(TABLE_LOGS, null, cv);
        if (r != -1) recalcStreak(habitId);
        return r;
    }

    public void removeCompletion(int habitId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_LOGS,
                L_HABIT_ID + "=? AND " + L_COMPLETED_AT + ">=?",
                new String[]{String.valueOf(habitId), String.valueOf(todayStart())});
        recalcStreak(habitId);
    }

    public boolean isCompletedToday(int habitId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_LOGS, new String[]{L_ID},
                L_HABIT_ID + "=? AND " + L_COMPLETED_AT + ">=?",
                new String[]{String.valueOf(habitId), String.valueOf(todayStart())},
                null, null, null);
        boolean done = c.getCount() > 0;
        c.close();
        return done;
    }

    public List<HabitLog> getLogsForHabit(int habitId) {
        List<HabitLog> logs = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_LOGS, null,
                L_HABIT_ID + "=?", new String[]{String.valueOf(habitId)},
                null, null, L_COMPLETED_AT + " DESC", "30");
        if (c.moveToFirst()) do {
            HabitLog log = new HabitLog();
            log.setId(c.getInt(c.getColumnIndexOrThrow(L_ID)));
            log.setHabitId(c.getInt(c.getColumnIndexOrThrow(L_HABIT_ID)));
            log.setCompletedAt(c.getLong(c.getColumnIndexOrThrow(L_COMPLETED_AT)));
            log.setNote(c.getString(c.getColumnIndexOrThrow(L_NOTE)));
            logs.add(log);
        } while (c.moveToNext());
        c.close();
        return logs;
    }

    public int getTotalCompletions(int habitId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_LOGS +
                " WHERE " + L_HABIT_ID + "=?", new String[]{String.valueOf(habitId)});
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    // ── HELPERS ──────────────────────────────────────────────────────────────────

    private Habit cursorToHabit(Cursor c) {
        Habit h = new Habit();
        h.setId(c.getInt(c.getColumnIndexOrThrow(H_ID)));
        h.setUserId(String.valueOf(c.getInt(c.getColumnIndexOrThrow(H_USER_ID))));
        h.setName(c.getString(c.getColumnIndexOrThrow(H_NAME)));
        h.setDescription(c.getString(c.getColumnIndexOrThrow(H_DESC)));
        h.setFrequency(c.getString(c.getColumnIndexOrThrow(H_FREQUENCY)));
        h.setColor(c.getString(c.getColumnIndexOrThrow(H_COLOR)));
        h.setEmoji(c.getString(c.getColumnIndexOrThrow(H_EMOJI)));
        h.setStreak(c.getInt(c.getColumnIndexOrThrow(H_STREAK)));
        h.setCreatedAt(c.getLong(c.getColumnIndexOrThrow(H_CREATED_AT)));
        return h;
    }

    private long todayStart() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    private void recalcStreak(int habitId) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery(
                "SELECT date(" + L_COMPLETED_AT + "/1000,'unixepoch','localtime') as day " +
                        "FROM " + TABLE_LOGS + " WHERE " + L_HABIT_ID + "=? " +
                        "GROUP BY day ORDER BY day DESC",
                new String[]{String.valueOf(habitId)});
        int streak = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String expected = sdf.format(new Date());
        if (c.moveToFirst()) do {
            String day = c.getString(0);
            if (day.equals(expected)) {
                streak++;
                Calendar cal = Calendar.getInstance();
                try {
                    cal.setTime(sdf.parse(expected));
                    cal.add(Calendar.DAY_OF_YEAR, -1);
                    expected = sdf.format(cal.getTime());
                } catch (Exception e) { break; }
            } else break;
        } while (c.moveToNext());
        c.close();
        ContentValues cv = new ContentValues();
        cv.put(H_STREAK, streak);
        db.update(TABLE_HABITS, cv, H_ID + "=?", new String[]{String.valueOf(habitId)});
    }
}
