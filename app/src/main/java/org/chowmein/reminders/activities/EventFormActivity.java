package org.chowmein.reminders.activities;

/*
 * Resources: Baeldung -- getting year, month, dayOfMonth from Date API
 *            How to manage startActivityForResult on Android:
 * https://stackoverflow.com/questions/10407159/how-to-manage-startactivityforresult-on-android#:~:text=First%20you%20use%20startActivityForResult(),()%20method%20in%20first%20Activity%20.
 *            Date Time picker dialog:
 * https://www.journaldev.com/9976/android-date-time-picker-dialog
 *
 */

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.chowmein.reminders.R;
import org.chowmein.reminders.helpers.ThemeHelper;
import org.chowmein.reminders.managers.DatesManager;
import org.chowmein.reminders.managers.UIFormatter;
import org.chowmein.reminders.model.Event;

import java.util.Calendar;
import java.util.Date;

/**
 * The Activity for editing an event and adding an event.
 */
public class EventFormActivity extends AppCompatActivity {

    Button btn_date;
    Toolbar tb_title;
    EditText edt_desc;
    EditText edt_dbr;
    Bundle bundle;
    int eventPosition;
    int requestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeHelper.getThemeStyle());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_form);

        this.bundle = getIntent().getExtras();
        this.requestCode = this.bundle.getInt(HomeActivity.REQUEST_CODE_KEY);

        try {
            this.getSupportActionBar().hide();
        } catch (Exception e) {
            System.out.println("Support Action bar hiding error");
        }

        initViews();

        // gets the event position if on edit mode
        // also set the fields to match the data of the event
        if(this.requestCode == HomeActivity.EDIT_REQUEST_CODE) {
            this.eventPosition = bundle.getInt(HomeActivity.EVENT_POS_KEY);
            populateViewData();
        }
    }

    /**
     * A helper method to initialize the instance variables.
     */
    private void initViews() {
        // Here, the onSubmitButtonClicked() method acts for both add and edit
        // buttons. Their code only differed by 1 line, so I used the requestCode
        // to differentiate them.

        // init the submit (add or edit) button
        Button btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(e -> onSubmitButtonClicked());

        Button btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(e -> onCancelButtonClicked());

        // init other instance vars
        tb_title = findViewById(R.id.tb_title_event_form);
        btn_date = findViewById(R.id.btn_date);
        edt_desc = findViewById(R.id.edt_desc);
        edt_dbr = findViewById(R.id.edt_dbr);

        String dateStr;

        if(requestCode == HomeActivity.ADD_REQUEST_CODE) {
            Date today = new Date();
            dateStr = DatesManager.formatDate(today, DatesManager.DATE_PTRN);
        } else {
            dateStr = this.bundle.getString(Event.DATE_KEY);

            // set title to appear as editing a reminder
            this.tb_title.setTitle("Edit a reminder");
            // set it to appear as the EDIT button
            btn_submit.setText(R.string.edit_btn_text);
        }

        // common to both edit and add
        btn_date.setText(dateStr);
        btn_date.setOnClickListener(e -> onDateButtonClicked());

        UIFormatter.format(this, UIFormatter.ADDEDIT);
        UIFormatter.colorHeader(this, tb_title);
    }

    private void onCancelButtonClicked() {
        finish();
    }

    /**
     * A helper method to populate the Event's data onto the corresponding fields when the user
     * wants to edit it.
     */
    private void populateViewData() {
        // get the event properties
        String dateStr = this.bundle.getString(Event.DATE_KEY);
        String desc = this.bundle.getString(Event.DESC_KEY);
        int dbr = this.bundle.getInt(Event.DBR_KEY);
        String dbrStr = String.valueOf(dbr);

        // set each input field to have the corresponding values of those properties
        this.btn_date.setText(dateStr);
        this.edt_desc.setText(desc);
        this.edt_dbr.setText(dbrStr);
    }

    /**
     * The callback method for when the "submit" (add/edit) button is clicked, differentiated
     * by the request code.
     */
    private void onSubmitButtonClicked() {
        Intent data = new Intent();

        // get all the inputs
        String dateStr = ((Button)findViewById(R.id.btn_date)).getText().toString();
        String desc = ((TextView)findViewById(R.id.edt_desc)).getText().toString();
        String dbrStr = ((TextView)findViewById(R.id.edt_dbr)).getText().toString();

        if(!validateFields(desc, dbrStr)) return;
        if(requestCode == HomeActivity.ADD_REQUEST_CODE && duplicate(dateStr, desc, dbrStr)) return;

        int dbr = Integer.parseInt(dbrStr);

        // parse the date string into a long (time in milliseconds)
        long time = 0;
        try {
            time = DatesManager.parseDate(dateStr, DatesManager.DATE_PTRN).getTime();
        } catch (Exception e) {
            System.out.println("Date parsing error in onSubmitButtonClicked(), EventFormActivity");
        }

        // put the info into the data intent
        data.putExtra(Event.DATE_KEY, time);
        data.putExtra(Event.DESC_KEY, desc);
        data.putExtra(Event.DBR_KEY, dbr);
        if(requestCode == HomeActivity.EDIT_REQUEST_CODE) {
            data.putExtra(HomeActivity.EVENT_POS_KEY, this.eventPosition);
        }

        setResult(RESULT_OK, data);
        finish();
    }

    /**
     * A helper method to determine whether this Event is a duplicate.
     * @param dateStr the Event's date as String
     * @param desc the Event's description
     * @param dbrStr the Event's dbr as String
     * Returns true if there's a duplicate. False otherwise.
     */
    private boolean duplicate(String dateStr, String desc, String dbrStr) {
        Date date = new Date();
        try {
            date = DatesManager.parseDate(dateStr, DatesManager.DATE_PTRN);
        } catch (Exception e) {
            System.out.println("Date parsing error in duplicate(), EventFormActivity");
        }

        int dbr = Integer.parseInt(dbrStr);

        Event event = new Event(date, desc, dbr);

        // try to find the Event in HomeActivity through the adapter
        int eventIndex = HomeActivity.getAdapter().getEventList().indexOf(event);

        // if the event is there already, it will return an index greater than -1.
        if(eventIndex > -1) {
            alertDuplicateEvent();
            return true;
        }
        return false;
    }

    /**
     * The callback method for when the date button is clicked. It will trigger the DatePicker
     * dialog box and allow the user to choose a date.
     */
    private void onDateButtonClicked() {
        Calendar cal = Calendar.getInstance();

        if(requestCode == HomeActivity.EDIT_REQUEST_CODE) {
            // gets the date string from btn_date and parse it into a Date object and
            // set the calendar's date to match that
            try {
                Date date = DatesManager.parseDate((String) btn_date.getText(),
                        DatesManager.DATE_PTRN);
                cal.setTime(date);
            } catch (Exception e) {
                System.out.println("Date parsing error in onDateButtonClicked(), " +
                        "EventFormActivity");
            }
        }

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int dom = cal.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpDialog = new DatePickerDialog(this,
                (datePicker, year1, month1, day) -> {
                    String dateStr = (month1 + 1) + "/" + day + "/" + year1;
                    btn_date.setText(dateStr);
                },
                year, month, dom);
        dpDialog.show();
    }

    /**
     * A helper method to validate that the description and the dbr inputs and are not empty
     * or ill-formatted. A warning dialog will show up telling the user that they had filled out
     * badly formatted values or they missed something.
     * @param desc the description input
     * @param dbrStr the dbr input
     * Returns true if both are valid. False otherwise.
     */
    private boolean validateFields(String desc, String dbrStr) {
        try {
            Integer.parseInt(dbrStr);
        } catch (Exception e) {
            this.alertInvalidFields();
            return false;
        }

        if(desc == null || desc.isEmpty() || dbrStr.isEmpty()) {
            this.alertInvalidFields();
            return false;
        }
        return true;
    }

    /**
     * A helper method to show an AlertDialog telling the user the inputs are invalid.
     */
    private void alertInvalidFields() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                .setTitle("Invalid inputs")
                .setMessage("Your inputs for one or more fields is invalid")
                .setPositiveButton("OK", null);

        dialogBuilder.show();
    }

    /**
     * A helper method to show an AlertDialog telling the user that they already have the same
     * exact Event.
     */
    private void alertDuplicateEvent() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                .setTitle("Duplicate event")
                .setMessage("Your already have this event!")
                .setPositiveButton("OK", null);

        dialogBuilder.show();
    }
}
