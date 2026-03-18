package com.habitforge.adapters;

import android.content.Context;
import android.view.*; import android.widget.*; import androidx.annotation.NonNull; import androidx.recyclerview.widget.RecyclerView;
import com.habitforge.R; import com.habitforge.models.HabitLog;
import java.text.SimpleDateFormat; import java.util.*; import java.util.Date;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.VH> {
    private Context ctx; private List<HabitLog> logs;
    private SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d yyyy  •  h:mm a", Locale.getDefault());
    public LogAdapter(Context ctx, List<HabitLog> logs) { this.ctx=ctx; this.logs=logs; }
    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        return new VH(LayoutInflater.from(ctx).inflate(R.layout.item_log, p, false)); }
    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        HabitLog log = logs.get(pos);
        h.tvDate.setText(sdf.format(new Date(log.getCompletedAt())));
        if (log.getNote()!=null && !log.getNote().isEmpty()) { h.tvNote.setVisibility(View.VISIBLE); h.tvNote.setText(log.getNote()); } else h.tvNote.setVisibility(View.GONE);
    }
    @Override public int getItemCount() { return logs.size(); }
    static class VH extends RecyclerView.ViewHolder {
        TextView tvDate, tvNote;
        VH(View v) { super(v); tvDate=v.findViewById(R.id.tv_log_date); tvNote=v.findViewById(R.id.tv_log_note); }
    }
}
