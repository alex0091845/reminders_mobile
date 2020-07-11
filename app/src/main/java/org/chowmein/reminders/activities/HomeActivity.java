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
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.chowmein.reminders.Event;
import org.chowmein.reminders.EventAdapter;
import org.chowmein.reminders.EventItemDecoration;
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
    File saveFile;
    public boolean selectMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) this.findViewById(R.id.tb_title);
        setSupportActionBar(toolbar);

        initViews();

        saveFile = new File(this.getFilesDir().getPath(), "saveFile.json");

        adapter = new EventAdapter(this);
        adapter.addAll(JsonHelper.deserialize(saveFile));

        Preferences.loadPreferences(this);

        // sets up the reminders recyclerview
        RecyclerView rv_reminders = findViewById(R.id.rv_reminders);
        rv_reminders.setHasFixedSize(true);
        rv_reminders.setLayoutManager(new LinearLayoutManager(this));
        rv_reminders.setAdapter(adapter);

        EventItemDecoration itemDeco = new EventItemDecoration(10);
        rv_reminders.addItemDecoration(itemDeco);

        rv_reminders.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                LinearLayoutManager llManager = (LinearLayoutManager) rv_reminders.getLayoutManager();
                int index = llManager.findFirstVisibleItemPosition();
                if(rv_reminders.getChildAdapterPosition(view) == index) {
                    setTvHomeYear(false);
                }
            }

            /* this method takes care of whenever a view becomes invisible (outside of the
            * RecyclerView)
             */
            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                LinearLayoutManager llManager = (LinearLayoutManager) rv_reminders.getLayoutManager();
                int index = llManager.findFirstCompletelyVisibleItemPosition() - 1;
                View firstView = rv_reminders.getChildAt(index);

                if(rv_reminders.getChildAdapterPosition(view) == index) {
                    setTvHomeYear(true);
                }
            }
        });

        if(adapter.getEventList().size() == 0) {
            findViewById(R.id.tv_home_year).setVisibility(View.GONE);
        } else {
            findViewById(R.id.tv_home_year).setVisibility(View.VISIBLE);
            setTvHomeYear(true);
        }
    }

    private void initViews() {
        FloatingActionButton btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(e -> onBtnAddClicked());

        FloatingActionButton btn_del = findViewById(R.id.btn_delete);
        btn_del.setOnClickListener(e -> onBtnDelClicked());

        UIFormatter.format(this, UIFormatter.HOME);
    }

    private void setTvHomeYear(boolean detach) {
        // get a reference to the year textview
        TextView tvHomeYear = (TextView) findViewById(R.id.tv_home_year);
        if(tvHomeYear.getVisibility() == View.GONE) {
            tvHomeYear.setVisibility(View.VISIBLE);
        }

        // get a reference to the RecyclerView's LinearLayoutManager
        RecyclerView rv = findViewById(R.id.rv_reminders);
        LinearLayoutManager llManager = (LinearLayoutManager) rv.getLayoutManager();

        // get year of the now new first visible item
        int firstPos;
        String year;

        // if the view is detaching, then it disappears out of sight, so just get the first
        // completely visible item's position
        if(detach) firstPos = llManager.findFirstCompletelyVisibleItemPosition();
        // else if the view is attaching, we find the first visible (not completely visible) view's
        // position
        else firstPos = llManager.findFirstVisibleItemPosition();

        // if can't find first item, the first must be visible
        if(firstPos < 0) firstPos = 0;
        year = adapter.get(firstPos).getYear();

        // set the tv_home_year's year
        tvHomeYear.setText(year);
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
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i).isSelected()) {
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

            if(requestCode == ADD_REQUEST_CODE) adapter.add(event);
            else if(requestCode == EDIT_REQUEST_CODE) {
                int eventPosition = bundle.getInt("eventPos");
                adapter.update(eventPosition, event);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(selectMode) {
            SortedList<Event> list = adapter.getEventList();

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
}
