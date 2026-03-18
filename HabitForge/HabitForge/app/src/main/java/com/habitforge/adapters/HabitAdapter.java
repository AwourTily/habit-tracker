package com.habitforge.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.habitforge.R;
import com.habitforge.models.Habit;
import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.VH> {
    public interface HabitClickListener {
        void onHabitCheck(Habit h, boolean checked);
        void onHabitClick(Habit h);
        void onHabitEdit(Habit h);
    }
    private Context ctx; private List<Habit> habits; private HabitClickListener listener;
    public HabitAdapter(Context ctx, List<Habit> habits, HabitClickListener l) { this.ctx=ctx; this.habits=habits; this.listener=l; }
    public void updateHabits(List<Habit> h) { this.habits=h; notifyDataSetChanged(); }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        return new VH(LayoutInflater.from(ctx).inflate(R.layout.item_habit, p, false)); }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        Habit habit = habits.get(pos);
        h.tvEmoji.setText(habit.getEmoji());
        h.tvName.setText(habit.getName());
        h.tvStreak.setText("🔥 " + habit.getStreak());
        h.tvFreq.setText(habit.getFrequency().toUpperCase());
        if (habit.getDescription()!=null && !habit.getDescription().isEmpty()) { h.tvDesc.setVisibility(View.VISIBLE); h.tvDesc.setText(habit.getDescription()); }
        else h.tvDesc.setVisibility(View.GONE);
        try { h.colorBar.setBackgroundColor(Color.parseColor(habit.getColor())); h.tvFreq.setTextColor(Color.parseColor(habit.getColor())); } catch(Exception ignored){}
        h.checkBox.setOnCheckedChangeListener(null);
        h.checkBox.setChecked(habit.isCompletedToday());
        h.card.setAlpha(habit.isCompletedToday() ? 0.65f : 1f);
        h.checkBox.setOnCheckedChangeListener((b, checked) -> listener.onHabitCheck(habit, checked));
        h.card.setOnClickListener(v -> listener.onHabitClick(habit));
        h.btnEdit.setOnClickListener(v -> listener.onHabitEdit(habit));
    }
    @Override public int getItemCount() { return habits.size(); }

    static class VH extends RecyclerView.ViewHolder {
        CardView card; View colorBar; TextView tvEmoji, tvName, tvDesc, tvStreak, tvFreq; CheckBox checkBox; ImageButton btnEdit;
        VH(View v) { super(v); card=v.findViewById(R.id.card_habit); colorBar=v.findViewById(R.id.view_color_bar);
            tvEmoji=v.findViewById(R.id.tv_emoji); tvName=v.findViewById(R.id.tv_habit_name); tvDesc=v.findViewById(R.id.tv_habit_desc);
            tvStreak=v.findViewById(R.id.tv_streak); tvFreq=v.findViewById(R.id.tv_frequency); checkBox=v.findViewById(R.id.checkbox_done); btnEdit=v.findViewById(R.id.btn_edit); }
    }
}
