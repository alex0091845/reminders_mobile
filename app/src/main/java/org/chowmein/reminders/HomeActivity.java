package org.chowmein.reminders;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    final static int ADD_REQUEST_CODE = 0;
    final static int EDIT_REQUEST_CODE = 1;

    EventAdapter adapter;
    File saveFile;
    boolean selectMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        try {
            this.getSupportActionBar().hide();
        } catch (Exception e) {}

        initViews();

        saveFile = new File(this.getFilesDir(), "saveFile.json");

        this.adapter = new EventAdapter(this);
        this.adapter.addAll(JsonHelper.deserialize(saveFile));

        RecyclerView rv_reminders = findViewById(R.id.rv_reminders);
        rv_reminders.setHasFixedSize(true);
        rv_reminders.setLayoutManager(new LinearLayoutManager(this));
        rv_reminders.setAdapter(this.adapter);
    }

    private void initViews() {
        FloatingActionButton btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(e -> onBtnAddClicked());
        FloatingActionButton btn_del = findViewById(R.id.btn_delete);
        btn_del.setOnClickListener(e -> onBtnDelClicked());
    }

    private void onBtnAddClicked() {
        final Intent addActIntent = new Intent(HomeActivity.this, AddActivity.class);
        addActIntent.putExtra("requestCode", ADD_REQUEST_CODE);
        startActivityForResult(addActIntent, ADD_REQUEST_CODE);
    }

    private void onBtnDelClicked() {
        SortedList<Event> list = this.adapter.getEventList();
        int size = list.size();
        for(int i = 0; i < list.size(); i++) {
            System.out.println(i);
            if(list.get(i).isSelected()) {
                this.adapter.removeAtIndex(i);
                i--;
            }
        }
        this.toggleSelectMode();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JsonHelper.serialize(this.adapter.getEventList(), this.saveFile);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // might have to check for multiple requestCodes if this activity receives info from multiple
        // other activities. For now it's just the add
        if(resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String desc = bundle.getString("desc");
            int dbr = bundle.getInt("dbr");
            Date date = null;

            DateFormat format = new SimpleDateFormat("M/d/yyyy");
            try {
                date = format.parse(format.format(bundle.getLong("date")));
            } catch (Exception e) {}

            Event event = new Event(date, desc, dbr);
            if(requestCode == ADD_REQUEST_CODE) this.adapter.add(event);
            else if(requestCode == EDIT_REQUEST_CODE) {
                int eventPosition = bundle.getInt("eventPos");
                this.adapter.update(eventPosition, event);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(selectMode) {
            SortedList<Event> list = this.adapter.getEventList();

            // sets everything back to unselected when back pressed and was in select mode
            // and updates using the adapter so the screen will re-draw
            for(int i = 0; i < list.size(); i++) {
                Event event = list.get(i);
                event.setSelected(false);
                adapter.update(i, event);
            }
            this.toggleSelectMode();
        } else super.onBackPressed();
    }

    public void toggleSelectMode() {
        if(!this.selectMode) {
            this.selectMode = true;
            this.findViewById(R.id.btn_add).setVisibility(View.INVISIBLE);
            this.findViewById(R.id.btn_delete).setVisibility(View.VISIBLE);

            TextView title = this.findViewById(R.id.tv_title);
            title.setText("Delete reminders");
        } else {
            this.selectMode = false;
            this.findViewById(R.id.btn_add).setVisibility(View.VISIBLE);
            this.findViewById(R.id.btn_delete).setVisibility(View.INVISIBLE);

            TextView title = this.findViewById(R.id.tv_title);
            title.setText("Reminders");
        }
    }
}
