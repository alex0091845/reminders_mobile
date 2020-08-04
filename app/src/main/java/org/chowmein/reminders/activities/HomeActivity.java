package org.chowmein.reminders.activities;

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
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.chowmein.reminders.Event;
import org.chowmein.reminders.EventAdapter;
import org.chowmein.reminders.EventItemDecoration;
import org.chowmein.reminders.EventManager;
import org.chowmein.reminders.JsonHelper;
import org.chowmein.reminders.Preferences;
import org.chowmein.reminders.R;
import org.chowmein.reminders.UIFormatter;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    public final static int ADD_REQUEST_CODE = 0;
    public final static int EDIT_REQUEST_CODE = 1;

    public static EventAdapter getAdapter() {
        return adapter;
    }

    static EventAdapter adapter;
    LinearLayoutManager layoutManager;
    File saveFile;
    public boolean selectMode;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        String homeYear = (String) ((TextView)findViewById(R.id.tv_home_year)).getText();
        outState.putString("HOME_YEAR", homeYear);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) this.findViewById(R.id.tb_title);
        setSupportActionBar(toolbar);

        Preferences.loadPreferences(this);

        // includes setting font size (formatting the home activity)
        initViews();

        saveFile = new File(this.getFilesDir().getPath(), "saveFile.json");

        adapter = new EventAdapter(this);
        adapter.addAll(JsonHelper.deserialize(saveFile));

        // sets up the reminders recyclerview
        RecyclerView rv_reminders = findViewById(R.id.rv_reminders);
        rv_reminders.setHasFixedSize(true);
        rv_reminders.setLayoutManager(new LinearLayoutManager(this));
        rv_reminders.setAdapter(adapter);
        layoutManager = (LinearLayoutManager) rv_reminders.getLayoutManager();

        EventItemDecoration itemDeco = new EventItemDecoration(10);
        rv_reminders.addItemDecoration(itemDeco);

        rv_reminders.addOnScrollListener(new RecyclerView.OnScrollListener() {
            /** just for override purposes. No real implementations here */
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            /**
             * Callback method for while the user is scrolling.
             * @param recyclerView
             * @param dx
             * @param dy
             */
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int firstVisPos = layoutManager.findFirstVisibleItemPosition();
                Event firstVisEvent = adapter.get(firstVisPos);
                int firstCompVisPos = layoutManager.findFirstCompletelyVisibleItemPosition();
                Event firstCompVisEvent = adapter.get(firstCompVisPos);

                // scrolls downwards || scrolls upwards
                if((dy > 0 && firstVisEvent.isYearTop()) || (dy < 0 && firstCompVisEvent.isYearTop())) {
                    TextView tvHomeYear = HomeActivity.this.findViewById(R.id.tv_home_year);
                    tvHomeYear.setText(firstVisEvent.getYear());
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });

        if (adapter.getEventList().size() == 0) {
            findViewById(R.id.tv_home_year).setVisibility(View.GONE);
        }

        TextView tvHomeYear = HomeActivity.this.findViewById(R.id.tv_home_year);
        if(savedInstanceState != null) {
            String homeYear = savedInstanceState.getString("HOME_YEAR");
            tvHomeYear.setText(homeYear);
        } else {
            tvHomeYear.setText(adapter.get(0).getYear());
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    private void initViews() {
        FloatingActionButton btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(e -> onBtnAddClicked());

        FloatingActionButton btn_del = findViewById(R.id.btn_delete);
        btn_del.setOnClickListener(e -> onBtnDelClicked());

        UIFormatter.format(this, UIFormatter.HOME);
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
        SortedList<Event> list = adapter.getEventList();
        int size = list.size();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isSelected()) {
                adapter.removeAtIndex(i);
                i--;
            }
        }
        this.toggleSelectMode();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JsonHelper.serialize(adapter.getEventList(), this.saveFile);
    }

    @Override
    protected void onStop() {
        super.onStop();
        JsonHelper.serialize(adapter.getEventList(), this.saveFile);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // if preferences have changed, then
        if (Preferences.prefsChanged) {
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
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String desc = bundle.getString("desc");
            int dbr = bundle.getInt("dbr");
            Date date = null;

            DateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");
            try {
                date = dateFormat.parse(dateFormat.format(bundle.getLong("date")));
            } catch (Exception e) {
            }

            Event event = new Event(date, desc, dbr);

            if (requestCode == ADD_REQUEST_CODE) adapter.add(event);
            else if (requestCode == EDIT_REQUEST_CODE) {
                int eventPosition = bundle.getInt("eventPos");
                adapter.update(eventPosition, event);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (selectMode) {
            SortedList<Event> list = adapter.getEventList();

            // sets everything back to unselected when back pressed and was in select mode
            // and updates using the adapter so the screen will re-draw
            for (int i = 0; i < list.size(); i++) {
                Event event = list.get(i);
                event.setSelected(false);
                adapter.update(i, event);
            }
            this.toggleSelectMode();
        } else super.onBackPressed();
    }

    public void toggleSelectMode() {
        if (!this.selectMode) {
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
}
