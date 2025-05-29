package com.example.emsismartpresence;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PlanningAdapter extends RecyclerView.Adapter<PlanningAdapter.PlanningViewHolder> {

    private final List<PlanningItem> planningList;

    public PlanningAdapter(List<PlanningItem> planningList) {
        this.planningList = planningList;
    }

    @NonNull
    @Override
    public PlanningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_planning, parent, false);
        return new PlanningViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanningViewHolder holder, int position) {
        PlanningItem item = planningList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return planningList.size();
    }

    static class PlanningViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvPlanningText;
        private final TextView tvTimestamp;

        public PlanningViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlanningText = itemView.findViewById(R.id.tvPlanningText);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }

        public void bind(PlanningItem item) {
            tvPlanningText.setText(item.getText());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String dateStr = sdf.format(new Date(item.getTimestamp()));
            tvTimestamp.setText(dateStr);
        }
    }
}