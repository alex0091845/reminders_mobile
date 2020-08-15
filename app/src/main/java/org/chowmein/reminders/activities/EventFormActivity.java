package org.chowmein.reminders.activities;

/**
 * Resources: Baeldung -- getting year, month, dayOfMonth from Date API
 *            How to manage startActivityForResult on Android:
 * https://stackoverflow.com/questions/10407159/how-to-manage-startactivityforresult-on-android#:~:text=First%20you%20use%20startActivityForResult(),()%20method%20in%20first%20Activity%20.
 *            Date Time picker dialog:
 * https://www.journaldev.com/9976/android-date-time-picker-dialog
 *
 */

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import org.chowmein.reminders.model.Event;
import org.chowmein.reminders.R;
import org.chowmein.reminders.helpers.UIFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EventFormActivity extends AppCompatActivity {

    Button btn_date;
    TextView tv_title;
    EditText edt_desc;
    EditText edt_dbr;
    Bundle bundle;
    int eventPosition;
    int requestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_form);

        this.bundle = getIntent().getExtras();
        this.requestCode = this.bundle.getInt("requestCode");

        try {
            this.getSupportActionBar().hide();
        } catch (Exception e) {}

        initViews();

        // gets the event position if on edit mode
        // also set the fields to match the data of the event
        if(this.requestCode == HomeActivity.EDIT_REQUEST_CODE) {
            this.eventPosition = bundle.getInt("eventPos");
            populateViewData();
        }
    }

    private void initViews() {
        // Here, the onSubmitButtonClicked() method acts for both add and edit
        // buttons. Their code only differed by 1 line, so I used the requestCode
        // to differentiate them.

        // init the submit (add or edit) button
        Button btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(e -> onSubmitButtonClicked());

        // init other instance vars
        tv_title = findViewById(R.id.tv_title_event_form);
        btn_date = findViewById(R.id.btn_date);
        edt_desc = findViewById(R.id.edt_desc);
        edt_dbr = findViewById(R.id.edt_dbr);

        String dateStr;

        if(requestCode == HomeActivity.ADD_REQUEST_CODE) {
            Date today = new Date();
            DateFormat format = new SimpleDateFormat("M/d/yyyy");
            dateStr = format.format(today);
        } else {
            dateStr = this.bundle.getString("date");

            // set title to appear as editing a reminder
            this.tv_title.setText("Edit a reminder");
            // set it to appear as the EDIT button
            btn_submit.setText("Edit");
        }

        // common to both edit and add
        btn_date.setText(dateStr);
        btn_date.setOnClickListener(e -> onDateButtonClicked());

        UIFormatter.format(this, UIFormatter.ADDEDIT);
    }

    private void populateViewData() {
        // get the event properties
        String dateStr = this.bundle.getString("date");
        String desc = this.bundle.getString("desc");
        int dbr = this.bundle.getInt("dbr");

        // set each input field to have the corresponding values of those properties
        this.btn_date.setText(dateStr);
        this.edt_desc.setText(desc);
        this.edt_dbr.setText(dbr + "");
    }

    private void onSubmitButtonClicked() {
        Intent data = new Intent();

        // get all the inputs
        String dateStr = ((Button)findViewById(R.id.btn_date)).getText().toString();
        String desc = ((TextView)findViewById(R.id.edt_desc)).getText().toString();
        String dbrStr = ((TextView)findViewById(R.id.edt_dbr)).getText().toString();

        if(!validateFields(desc, dbrStr)) return;
        if(duplicate(dateStr, desc, dbrStr)) return;

        int dbr = Integer.parseInt(dbrStr);

        // parse the date string into a long (time in milliseconds)
        DateFormat format = new SimpleDateFormat("M/d/yyyy");
        long time = 0;

        try {
            time = format.parse(dateStr).getTime();
        } catch (Exception e) {}

        // put the info into the data intent
        data.putExtra("date", time);
        data.putExtra("desc", desc);
        data.putExtra("dbr", dbr);
        if(requestCode == HomeActivity.EDIT_REQUEST_CODE) {
            data.putExtra("eventPos", this.eventPosition);
        }

        setResult(RESULT_OK, data);
        finish();
    }

    private boolean duplicate(String dateStr, String desc, String dbrStr) {
        DateFormat format = new SimpleDateFormat("M/d/yyyy");
        Date date = new Date();
        try {
            date = format.parse(dateStr);
        } catch (Exception e) {};

        int dbr = Integer.parseInt(dbrStr);

        // in edit, maybe remove the event first and then add it?
        Event event = new Event(date, desc, dbr);
        int eventIndex = HomeActivity.getAdapter().getEventList().indexOf(event);
        if(eventIndex > -1) {
            alertDuplicateEvent();
            return true;
        }
        return false;
    }

    private void onDateButtonClicked() {
        Calendar cal = Calendar.getInstance();

        if(requestCode == HomeActivity.EDIT_REQUEST_CODE) {
            // gets the date string from btn_date and parse it into a Date object
            DateFormat format = new SimpleDateFormat("M/d/yyyy");

            // set the calendar's date to match that
            try {
                Date date = format.parse((String) btn_date.getText());
                cal.setTime(date);
            } catch (Exception e) {}
        }

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int dom = cal.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String dateStr = (month + 1) + "/" + day + "/" + year;
                btn_date.setText(dateStr);
            }
        },
                year, month, dom);
        dpDialog.show();
    }

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

    private void alertInvalidFields() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                .setTitle("Invalid inputs")
                .setMessage("Your inputs for one or more fields is invalid")
                .setPositiveButton("OK", null);

        AlertDialog dialog = dialogBuilder.show();
    }

    private void alertDuplicateEvent() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                .setTitle("Duplicate event")
                .setMessage("Your already have this event!")
                .setPositiveButton("OK", null);

        AlertDialog dialog = dialogBuilder.show();
    }
}
