package com.example.studybuddy;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {

    Context context;
    ArrayList<MyTask> myTasks;

    public TaskAdapter(Context context, ArrayList<MyTask> myTasks){
        this.context = context;
        this.myTasks = myTasks;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_task,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.titleTask.setText(myTasks.get(position).getTitleTask());
        holder.descTask.setText(myTasks.get(position).getDescTask());
        holder.dateTask.setText(myTasks.get(position).getDateTask());
        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditTaskActivity.class);
                intent.putExtra("titleTask",myTasks.get(position).getTitleTask());
                intent.putExtra("descTask",myTasks.get(position).getDescTask());
                intent.putExtra("dateTask",myTasks.get(position).getDateTask());
                intent.putExtra("keyTask",myTasks.get(position).getKeyTask());
                context.startActivity(intent);
            }
        });*/
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //Toast.makeText(context,myTasks.get(position).getTitleTask()+" Task Completed.",Toast.LENGTH_SHORT).show();
                try {
                    MainActivity.db.delete("tasks", "titleTask=? AND dateTask=? AND descTask=? AND keyTask=?", new String[]{myTasks.get(position).getTitleTask(), myTasks.get(position).getDateTask(), myTasks.get(position).getDescTask(), myTasks.get(position).getKeyTask()});
                }catch (Exception e){
                    Toast.makeText(context, "Error occurred!", Toast.LENGTH_SHORT).show();
                    Log.i("ERROR", e.getMessage());
                }
                myTasks.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,myTasks.size());
                Toast.makeText(context,"Task Completed ✔", Toast.LENGTH_SHORT).show();

                //TasksActivity.taskFinished();
                if(myTasks.isEmpty()){
                    TasksActivity.displayEmpty();
                }
                return true;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditTaskActivity.class);
                intent.putExtra("titleTask",myTasks.get(position).getTitleTask());
                intent.putExtra("descTask",myTasks.get(position).getDescTask());
                intent.putExtra("dateTask",myTasks.get(position).getDateTask());
                intent.putExtra("keyTask",myTasks.get(position).getKeyTask());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myTasks.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView titleTask, descTask, dateTask;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTask = (TextView) itemView.findViewById(R.id.titleTask);
            dateTask = (TextView) itemView.findViewById(R.id.dateTask);
            descTask = (TextView) itemView.findViewById(R.id.descTask);
        }
    }
}
