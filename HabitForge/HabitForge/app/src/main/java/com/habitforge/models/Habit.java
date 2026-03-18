package com.habitforge.models;

public class Habit {
    private int id;
    private String userId;
    private String name;
    private String description;
    private String frequency;
    private String color;
    private String emoji;
    private int streak;
    private long createdAt;
    private boolean completedToday;

    public Habit() {}

    public Habit(String userId, String name, String description,
                 String frequency, String color, String emoji) {
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.frequency = frequency;
        this.color = color;
        this.emoji = emoji;
        this.streak = 0;
        this.createdAt = System.currentTimeMillis();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }
    public int getStreak() { return streak; }
    public void setStreak(int streak) { this.streak = streak; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public boolean isCompletedToday() { return completedToday; }
    public void setCompletedToday(boolean completedToday) { this.completedToday = completedToday; }
}
