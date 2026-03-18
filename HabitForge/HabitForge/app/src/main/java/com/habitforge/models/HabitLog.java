package com.habitforge.models;

public class HabitLog {
    private int id;
    private int habitId;
    private long completedAt;
    private String note;

    public HabitLog() {}
    public HabitLog(int habitId, String note) {
        this.habitId = habitId;
        this.note = note;
        this.completedAt = System.currentTimeMillis();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getHabitId() { return habitId; }
    public void setHabitId(int habitId) { this.habitId = habitId; }
    public long getCompletedAt() { return completedAt; }
    public void setCompletedAt(long completedAt) { this.completedAt = completedAt; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
