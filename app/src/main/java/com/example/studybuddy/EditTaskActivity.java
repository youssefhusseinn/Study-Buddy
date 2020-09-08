package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditTaskActivity extends AppCompatActivity {

    EditText titleEditText, descEditText, dateEditText;
    //DatabaseReference reference;
    FirebaseAuth mAuth;
    private Firebase mRootRef;
    String oldTitle, oldDesc, oldDate, oldKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        Log.i("Test", "EditTaskActivity");
        
        if(!MainActivity.guest) {
            mAuth = FirebaseAuth.getInstance();
            /////reference = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("Tasks");
            mRootRef = new Firebase("https://study-buddy-e065d.firebaseio.com/users");
        }

        Intent intent = getIntent();

        titleEditText = findViewById(R.id.titleUpdate);
        descEditText = findViewById(R.id.descUpdate);
        dateEditText = findViewById(R.id.dateUpdate);

        oldTitle = intent.getStringExtra("titleTask");
        oldDesc = intent.getStringExtra("descTask");
        oldDate = intent.getStringExtra("dateTask");
        oldKey = intent.getStringExtra("keyTask");

        titleEditText.setText(oldTitle);
        dateEditText.setText(oldDate);
        descEditText.setText(oldDesc);
    }

    public void saveClicked(View view){
        final String newTitle = titleEditText.getText().toString();
        final String newDesc = descEditText.getText().toString();
        final String newDate = dateEditText.getText().toString();
        final String newKey = oldKey;

        //updateDatabase(newTitle, newDate, newDesc, newKey);
        if(!MainActivity.guest){
            //new UpdateFirebase().execute(new MyTask(newTitle,newDate,newDesc,newKey));
            HashMap<String, Object> map = new HashMap<>();
            map.put("titleTask", newTitle);
            map.put("dateTask", newDate);
            map.put("descTask", newDesc);
            map.put("keyTask", newKey);
            FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("Tasks").child("Task"+newKey).setValue(map);
            /*reference.child("Task"+newKey).removeValue();*/

            /////reference.child("Task"+newKey).setValue(map);
            //Firebase childRef = mRootRef.child(mAuth.getCurrentUser().getUid()).child("Tasks").child("Task"+newKey);
            //childRef.updateChildren(map);
            //TODO FIX SAVE FOR ACCOUNTS
            /*reference.child("Task"+newKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataSnapshot.getRef().child("titleTask").setValue(newTitle);
                    dataSnapshot.getRef().child("dateTask").setValue(newDate);
                    dataSnapshot.getRef().child("descTask").setValue(newDesc);
                    dataSnapshot.getRef().child("keyTask").setValue(newKey);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(EditTaskActivity.this,"Error occurred",Toast.LENGTH_SHORT).show();
                }
            });*/
        }else{
            Intent intent = new Intent(this, TasksActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public void deleteClicked(View view){
        if(!MainActivity.guest) {
            //TODO: IMPLEMENT DELETE FOR ACCOUNTS
            Intent intent = new Intent(this, TasksActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else{
            MainActivity.db.delete("tasks","titleTask=? AND dateTask=? AND descTask=? AND keyTask=?", new String[]{oldTitle, oldDate, oldDesc, oldKey});

            Intent intent = new Intent(this, TasksActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            //finish();
        }
    }

    private void updateDatabase(String title, String date, String desc, String key){
        ContentValues cv = new ContentValues();
        cv.put("titleTask", title); //These Fields should be your String values of actual column names
        cv.put("dateTask", date);
        cv.put("descTask", desc);
        cv.put("keyTask", key);
        try {
            MainActivity.db.update("tasks", cv, "titleTask=? AND dateTask=? AND descTask=? AND keyTask=?", new String[]{oldTitle, oldDate, oldDesc, oldKey});
        }catch (Exception e){
            Toast.makeText(EditTaskActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
            Log.d("Test",e.getMessage());

        }
    }

    public class UpdateFirebase extends AsyncTask<MyTask,Void,Void> {

        @Override
        protected Void doInBackground(MyTask... myTasks) {
            MyTask task = myTasks[0];
            /////reference.child("Task"+task.getKeyTask()).removeValue();
            /////reference.child("Task"+task.getKeyTask()).setValue(task);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(EditTaskActivity.this,"Done",Toast.LENGTH_SHORT).show();
        }
    }
}