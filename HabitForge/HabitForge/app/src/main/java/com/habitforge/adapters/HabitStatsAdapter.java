package com.habitforge.adapters;

import android.content.Context; import android.graphics.Color; import android.view.*; import android.widget.*;
import androidx.annotation.NonNull; import androidx.recyclerview.widget.RecyclerView;
import com.habitforge.R; import com.habitforge.database.DatabaseHelper; import com.habitforge.models.Habit;
import java.util.List;

public class HabitStatsAdapter extends RecyclerView.Adapter<HabitStatsAdapter.VH> {
    private Context ctx; private List<Habit> habits; private DatabaseHelper db;
    public HabitStatsAdapter(Context ctx, List<Habit> habits, DatabaseHelper db) { this.ctx=ctx; this.habits=habits; this.db=db; }
    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        return new VH(LayoutInflater.from(ctx).inflate(R.layout.item_habit_stats, p, false)); }
    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        Habit habit = habits.get(pos);
        h.tvRank.setText("#"+(pos+1)); h.tvName.setText(habit.getEmoji()+" "+habit.getName());
        h.tvStreak.setText("🔥 "+habit.getStreak()+" day streak"); h.tvTotal.setText(db.getTotalCompletions(habit.getId())+" completions");
        try { h.tvName.setTextColor(Color.parseColor(habit.getColor())); h.tvRank.setTextColor(Color.parseColor(habit.getColor())); } catch(Exception ignored){}
    }
    @Override public int getItemCount() { return habits.size(); }
    static class VH extends RecyclerView.ViewHolder {
        TextView tvRank, tvName, tvStreak, tvTotal;
        VH(View v) { super(v); tvRank=v.findViewById(R.id.tv_rank); tvName=v.findViewById(R.id.tv_stat_name); tvStreak=v.findViewById(R.id.tv_stat_streak); tvTotal=v.findViewById(R.id.tv_stat_total); }
    }
}
