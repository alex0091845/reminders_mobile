package org.chowmein.reminders;

/**
 * ---------------------------------------References------------------------------------------------
 * AlertDialog:
 * https://stackoverflow.com/questions/25560408/alert-dialogue-has-protected-access
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditActivity extends AppCompatActivity {

    Button btn_date;
    EditText edt_desc;
    EditText edt_dbr;
    Bundle bundle;
    int eventPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        try {
            this.getSupportActionBar().hide();
        } catch (Exception e) {}

        this.bundle = getIntent().getExtras();

        // gets the event position
        this.eventPosition = bundle.getInt("eventPos");

        initViews();

        populateViewData();
    }

    private void initViews() {
        // init the edit button
        Button btn_edit = findViewById(R.id.btn_submit_edit);
        btn_edit.setOnClickListener(e -> onEditButtonClicked());

        // init other instance vars
        btn_date = findViewById(R.id.btn_date_edit);
        edt_desc = findViewById(R.id.edt_desc_edit);
        edt_dbr = findViewById(R.id.edt_dbr_edit);

        // init the date button text and function
        String dateStr = this.bundle.getString("date");
        btn_date = findViewById(R.id.btn_date_edit);
        btn_date.setText(dateStr);
        btn_date.setOnClickListener(e -> onDateButtonClicked());

        // format the views to fit on the screen
        UIFormatter.format(this, UIFormatter.EDIT);
    }

    private void populateViewData() {
        // get the event properties
        String dateStr = this.bundle.getString("date");
        String desc = this.bundle.getString("desc");
        int dbr = this.bundle.getInt("dbr");

        // set each input field to have the corresponding values of those properties
        this.btn_date.setText(dateStr);
        this.edt_desc.setText(desc);
        this.edt_dbr.setText("" + dbr);
    }

    private void onDateButtonClicked() {
        // gets the date string from btn_date and parse it into a Date object
        DateFormat format = new SimpleDateFormat("M/d/yyyy");
        Calendar cal = Calendar.getInstance();

        // set the calendar's date to match that
        try {
            Date date = format.parse((String) btn_date.getText());
            cal.setTime(date);
        } catch (Exception e) {}

        // set the dialog's initial date to match that
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int dom = cal.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String dateStr = (month + 1) + "/" + day + "/" + year;
                btn_date.setText(dateStr);
            }
        },
                year, month, dom);

        // show the dialog for the user to choose a date from
        dpDialog.show();
    }

    private void onEditButtonClicked() {
        Intent data = new Intent();

        // get all the inputs
        String dateStr = ((Button)findViewById(R.id.btn_date_edit)).getText().toString();
        String desc = ((TextView)findViewById(R.id.edt_desc_edit)).getText().toString();
        String dbrStr = ((TextView)findViewById(R.id.edt_dbr_edit)).getText().toString();

        if(!validateFields(desc, dbrStr)) return;

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
        data.putExtra("eventPos", this.eventPosition);

        // finish up and return intent to calling Activity
        setResult(RESULT_OK, data);
        finish();
    }

    private boolean validateFields(String desc, String dbrStr) {
        if(desc == null || desc.isEmpty() || dbrStr.isEmpty() || Integer.parseInt(dbrStr) < 0) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                    .setTitle("Invalid inputs")
                    .setMessage("Your inputs for one or more fields is invalid")
                    .setPositiveButton("OK", null);

            AlertDialog dialog = dialogBuilder.show();
            return false;
        }

        return true;
    }
}
