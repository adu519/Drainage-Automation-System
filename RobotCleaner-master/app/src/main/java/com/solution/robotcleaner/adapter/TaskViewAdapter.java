package com.solution.robotcleaner.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.solution.robotcleaner.R;
import com.solution.robotcleaner.model.TaskModel;

import java.util.List;

public class TaskViewAdapter extends RecyclerView.Adapter<TaskViewAdapter.RowViewHolder> {
    List<TaskModel> tasks;

    public TaskViewAdapter(List<TaskModel> tasks) {
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public RowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_list_item, parent, false);
        return new RowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RowViewHolder holder, int position) {
        int row = holder.getAdapterPosition();
        if (row == 0) {
            holder.task.setBackgroundResource(R.drawable.table_header_cell_bg);
            holder.progress.setBackgroundResource(R.drawable.table_header_cell_bg);
            holder.dateTime.setBackgroundResource(R.drawable.table_header_cell_bg);
            holder.area.setBackgroundResource(R.drawable.table_header_cell_bg);

            holder.task.setTextColor(Color.rgb(255, 255, 255));
            holder.progress.setTextColor(Color.rgb(255, 255, 255));
            holder.dateTime.setTextColor(Color.rgb(255, 255, 255));
            holder.area.setTextColor(Color.rgb(255, 255, 255));

            holder.area.setText("Area");
            holder.progress.setText("Progress");
            holder.dateTime.setText("Assigned Date Time");
            holder.task.setText("Task");

        } else {
            TaskModel model = tasks.get(row - 1);

            holder.task.setBackgroundResource(R.drawable.table_content_cell_bg);
            holder.progress.setBackgroundResource(R.drawable.table_content_cell_bg);
            holder.dateTime.setBackgroundResource(R.drawable.table_content_cell_bg);
            holder.area.setBackgroundResource(R.drawable.table_content_cell_bg);

            holder.area.setText(model.getArea());
            holder.progress.setText(model.getProgress());
            holder.dateTime.setText(model.getDateTime());
            holder.task.setText(model.getTask());
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size() + 1;
    }

    class RowViewHolder extends RecyclerView.ViewHolder {
        TextView area;
        TextView progress;
        TextView dateTime;
        TextView task;

        public RowViewHolder(View itemView) {
            super(itemView);
            area = itemView.findViewById(R.id.area);
            progress = itemView.findViewById(R.id.progress);
            dateTime = itemView.findViewById(R.id.dateTime);
            task = itemView.findViewById(R.id.task);
        }
    }
}
