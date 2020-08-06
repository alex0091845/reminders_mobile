package org.chowmein.reminders.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ------------------------------------------References---------------------------------------------
 *  * ItemDecoration (setting each item's margins):
 *  https://medium.com/mobile-app-development-publication/right-way-of-setting-margin-on-recycler-views-cell-319da259b641
 *  * Settings/Preferences:
 *  https://stackoverflow.com/questions/39439039/how-to-add-overflow-menu-to-toolbar
 *  https://alvinalexander.com/android/android-tutorial-preferencescreen-preferenceactivity-preferencefragment/
 *
 * The main displaying activity. Displays the reminders/events in a RecyclerView, where there is
 * a year text display at the top showing the current year (because the events themselves only show
 * the date, not the year). Has an add button and a delete button (which only shows up when the user
 * wants to select and delete certain events). Has a overflow options menu leading to the
 * Settings Activity.
 */
public class HomeActivity extends AppCompatActivity {

    public final static int ADD_REQUEST_CODE = 0;
    public final static int EDIT_REQUEST_CODE = 1;

    public final static String HOME_YEAR_KEY = "HOME_YEAR";
    public final static String DATE_KEY = "date";
    public final static String EVENT_POS_KEY = "eventPos";
    public final static String REQUEST_CODE_KEY = "requestCode";
    public final static String SAVE_FILE_NAME = "saveFile.json";

    static EventAdapter adapter;
    LinearLayoutManager layoutManager;
    File saveFile;
    public boolean selectMode;

    RecyclerView rv_reminders;
    TextView tvHomeYear;
    FloatingActionButton btnAdd;
    FloatingActionButton btnDelete;
    Toolbar toolbar;

    SortedList<Event> eventSortedList;

    /**
     * A callback method triggered when the activity is starting.
     * @param savedInstanceState bundle of information of last seen activity if this
     *                           it is being re-initialized, null otherwise
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Preferences.loadPreferences(this);

        // deserialize from the save file and populate the RecyclerView adapter and SortedList
        saveFile = new File(this.getFilesDir().getPath(), SAVE_FILE_NAME);
        adapter = new EventAdapter(this);
        adapter.addAll(JsonHelper.deserialize(saveFile));
        eventSortedList = adapter.getEventList();

        // includes setting font size (formatting the home activity)
        initViews(savedInstanceState);
    }

    /**
     * A helper method to initialize all the views and their implementations properly.
     * @param savedInstanceState the saved instance state, required to set the year text.
     */
    private void initViews(Bundle savedInstanceState) {
        // initialize all views first
        this.rv_reminders = findViewById(R.id.rv_reminders);
        this.toolbar = findViewById(R.id.tb_title);
        this.tvHomeYear = findViewById(R.id.tv_home_year);
        this.btnAdd = findViewById(R.id.btn_add);
        this.btnDelete = findViewById(R.id.btn_delete);

        // set up the views that require more detailed implementations
        setSupportActionBar(toolbar);
        initRecyclerView();
        initTvHomeYear(savedInstanceState);

        btnAdd.setOnClickListener(e -> onBtnAddClicked());
        btnDelete.setOnClickListener(e -> onBtnDelClicked());

        UIFormatter.format(this, UIFormatter.HOME);
    }

    /**
     * Helper method to set up the RecyclerView displaying data. Includes setting the LayoutManager,
     * the ItemDecoration, and the onScrollListener (that detects when the user scrolls so that the
     * year text at the top can display properly.
     */
    private void initRecyclerView() {
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
             * @param recyclerView the recyclerView
             * @param dx change in x
             * @param dy change in y
             */
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int firstVisPos = layoutManager.findFirstVisibleItemPosition();
                Event firstVisEvent = adapter.get(firstVisPos);
                int firstCompVisPos = layoutManager.findFirstCompletelyVisibleItemPosition();
                Event firstCompVisEvent = adapter.get(firstCompVisPos);

                // scrolls downwards || scrolls upwards
                if((dy > 0 && firstVisEvent.isYearTop()) || (dy < 0 && firstCompVisEvent.isYearTop())) {
                    tvHomeYear.setText(firstVisEvent.getYear());
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    /**
     * Initializes the year text at the top of the reminder-displaying RecyclerView. Uses the
     * savedInstanceState to initialize in case of a configuration change.
     * @param savedInstanceState the savedInstanceState
     */
    private void initTvHomeYear(Bundle savedInstanceState) {
        // if there are no events, make the year text invisible
        if (adapter.getEventList().size() == 0) {
            tvHomeYear.setVisibility(View.GONE);
        }

        // sets the year's text to either the first event in the adapter or the year seen in the
        // last configuration
        if(savedInstanceState != null) {
            String homeYear = savedInstanceState.getString(HOME_YEAR_KEY);
            tvHomeYear.setText(homeYear);
        } else {
            tvHomeYear.setText(adapter.get(0).getYear());
        }
    }

    /**
     * A callback method triggered when the options menu is created.
     * @param menu menu
     * @return return true for the menu to be displayed; return false and it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    /**
     * A callback method triggered when an item in the options menu is selected. Start the settings
     * activity here.
     * @param item the selected item
     * @return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
                Intent settingsActIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(settingsActIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * The callback method triggered when the "add" floating action button is clicked.
     */
    private void onBtnAddClicked() {
        final Intent addActIntent = new Intent(HomeActivity.this, EventFormActivity.class);
        addActIntent.putExtra(REQUEST_CODE_KEY, ADD_REQUEST_CODE);
        startActivityForResult(addActIntent, ADD_REQUEST_CODE);
    }

    /**
     * The callback method triggered when the "delete" floating action button is clicked.
     */
    private void onBtnDelClicked() {
        int size = eventSortedList.size();

        // goes over the list and delete; the index i is dynamically decremented to prevent
        // under-deletion (e.g., when an item gets deleted, the next item replaces it at the index
        // i, but i++ will skip over that next item, so i-- compensates for that movement).
        for (int i = 0; i < size; i++) {
            if (eventSortedList.get(i).isSelected()) {
                adapter.removeAtIndex(i);
                i--;
            }
        }

        this.toggleSelectMode();
    }

    /**
     * A callback method triggered as part of the activity lifecycle when the user no longer
     * actively interacts with the activity, but it is still visible on screen. Overridden to save.
     */
    @Override
    protected void onPause() {
        super.onPause();
        JsonHelper.serialize(eventSortedList, this.saveFile);
    }

    /**
     * A callback method triggered when this activity are no longer visible to the user. Overridden
     * to save.
     */
    @Override
    protected void onStop() {
        super.onStop();
        JsonHelper.serialize(eventSortedList, this.saveFile);
    }

    /**
     * A callback method triggered after onRestoreInstanceState(Bundle), onRestart(), or onPause(),
     * for this activity to start interacting with the user. Overridden to see if the user changed
     * the preferences. If so, display a Toast to remind them to restart the app to see changes.
     */
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

    /**
     * A callback method triggered when the instance state is about to be saved
     * @param outState the reference to the state to save
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        String homeYear = (String) ((TextView)findViewById(R.id.tv_home_year)).getText();
        outState.putString(HOME_YEAR_KEY, homeYear);

        super.onSaveInstanceState(outState);
    }

    /**
     * A callback method triggered when an activity started for result returns. In this use case,
     * we receive from either the "add" or the "edit" version of the activity. Both require some
     * amount of similar code (construct a new Event), so the check is done at the end to perform
     * different operations.
     * @param requestCode the request code
     * @param resultCode the result code
     * @param data the bundle containing information from the returning activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bundle bundle = data.getExtras();

        if(bundle != null) {
            if (resultCode == RESULT_OK) {
                // parse all necessary fields
                String desc = bundle.getString("desc");

                int dbr = bundle.getInt("dbr");
                Date date = null;

                DateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");
                try {
                    date = dateFormat.parse(dateFormat.format(bundle.getLong(DATE_KEY)));
                } catch (Exception e) {
                    System.out.println("Error formatting date in HomeActivity");
                }

                // construct an Event based on the parsed data
                Event event = new Event(date, desc, dbr);

                // check if the activity returned was the "add" or the "edit" version
                if (requestCode == ADD_REQUEST_CODE) adapter.add(event);
                else if (requestCode == EDIT_REQUEST_CODE) {
                    int eventPosition = bundle.getInt(EVENT_POS_KEY);
                    adapter.update(eventPosition, event);
                }
            }
        }
    }

    /**
     * A callback method triggered when the back button is pressed. Overridden because there is a
     * select mode available when users want to delete reminders. This allows the user to go back
     * to the normal, non-selection mode when they want to cancel the select/delete operation.
     */
    @Override
    public void onBackPressed() {
        if (selectMode) {
            // sets everything back to unselected when back pressed and was in select mode
            // and updates using the adapter so the screen will re-draw
            for (int i = 0; i < eventSortedList.size(); i++) {
                Event event = eventSortedList.get(i);
                event.setSelected(false);
                adapter.update(i, event);
            }
            this.toggleSelectMode();
        } else super.onBackPressed();
    }

    /**
     * A helper method to toggle between normal, non-selection mode, and selection mode (where the
     * user is able to select multiple reminders/events and delete them.
     */
    public void toggleSelectMode() {
        if (!this.selectMode) {
            this.selectMode = true;
            btnAdd.setVisibility(View.INVISIBLE);
            btnDelete.setVisibility(View.VISIBLE);

            toolbar.setTitle("Delete reminders");
        } else {
            this.selectMode = false;
            btnAdd.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.INVISIBLE);

            toolbar.setTitle("Reminders");
        }
    }

    /**
     * Getter for the RecyclerView adapter
     * @return adapter
     */
    public static EventAdapter getAdapter() {
        return adapter;
    }
}
