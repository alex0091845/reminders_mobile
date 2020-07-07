package org.chowmein.reminders;

/**
 * ------------------------------------------References---------------------------------------------
 * ItemDecoration (setting each item's margins)
 * https://medium.com/mobile-app-development-publication/right-way-of-setting-margin-on-recycler-views-cell-319da259b641
 * Settings/Preferences:
 * https://stackoverflow.com/questions/39439039/how-to-add-overflow-menu-to-toolbar
 * https://alvinalexander.com/android/android-tutorial-preferencescreen-preferenceactivity-preferencefragment/
 */

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    final static int ADD_REQUEST_CODE = 0;
    final static int EDIT_REQUEST_CODE = 1;

    public EventAdapter getAdapter() {
        return adapter;
    }

    EventAdapter adapter;
    File saveFile;
    boolean selectMode;
    private static Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        HomeActivity.ctx = this;

        Toolbar toolbar = (Toolbar) this.findViewById(R.id.tb_title);
        setSupportActionBar(toolbar);

        initViews();

        saveFile = new File(this.getFilesDir().getPath(), "saveFile.json");

        this.adapter = new EventAdapter(this);
        this.adapter.addAll(JsonHelper.deserialize(saveFile));

        Preferences.loadPreferences(this);

        // sets up the reminders recyclerview
        RecyclerView rv_reminders = findViewById(R.id.rv_reminders);
        rv_reminders.setHasFixedSize(true);
        rv_reminders.setLayoutManager(new LinearLayoutManager(this));
        rv_reminders.setAdapter(this.adapter);

        EventItemDecoration itemDeco = new EventItemDecoration(10);
        rv_reminders.addItemDecoration(itemDeco);
    }

    private void initViews() {
        FloatingActionButton btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(e -> onBtnAddClicked());
        FloatingActionButton btn_del = findViewById(R.id.btn_delete);
        btn_del.setOnClickListener(e -> onBtnDelClicked());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: {
                Intent settingsActIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(settingsActIntent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onBtnAddClicked() {
        final Intent addActIntent = new Intent(HomeActivity.this, EventFormActivity.class);
        addActIntent.putExtra("requestCode", ADD_REQUEST_CODE);
        startActivityForResult(addActIntent, ADD_REQUEST_CODE);
    }

    private void onBtnDelClicked() {
        SortedList<Event> list = this.adapter.getEventList();
        int size = list.size();
        for(int i = 0; i < list.size(); i++) {
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
    protected void onStop() {
        super.onStop();
        JsonHelper.serialize(this.adapter.getEventList(), this.saveFile);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // if preferences have changed, then
        if(Preferences.prefsChanged) {
            // reset it so we don't detect it again
            Preferences.prefsChanged = false;

            // notify the user that to re-open the app to see the changes
            Toast.makeText(this,
                    "Please close and re-open the app to see the changes.",
                    Toast.LENGTH_LONG).show();
        }
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

            DateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");
            try {
                date = dateFormat.parse(dateFormat.format(bundle.getLong("date")));
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

            Toolbar title = this.findViewById(R.id.tb_title);
            title.setTitle("Delete reminders");
        } else {
            this.selectMode = false;
            this.findViewById(R.id.btn_add).setVisibility(View.VISIBLE);
            this.findViewById(R.id.btn_delete).setVisibility(View.INVISIBLE);

            Toolbar title = this.findViewById(R.id.tb_title);
            title.setTitle("Reminders");
        }
    }

    public static Context getContext() {
        return ctx;
    }
}
