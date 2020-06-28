package org.chowmein.reminders;

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
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddActivity extends AppCompatActivity {

    Button btn_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        try {
            this.getSupportActionBar().hide();
        } catch (Exception e) {}

        initViews();
    }

    private void initViews() {
        Button btn_add = findViewById(R.id.btn_submit);
        btn_add.setOnClickListener(e -> onAddButtonClicked());

        Date today = new Date();
        DateFormat format = new SimpleDateFormat("M/d/yyyy");
        String dateStr = format.format(today);
        btn_date = findViewById(R.id.btn_date);
        btn_date.setText(dateStr);
        btn_date.setOnClickListener(e -> onDateButtonClicked());

        UIFormatter.format(this, UIFormatter.ADD);
    }

    private void onAddButtonClicked() {
        Intent data = new Intent();

        String dateStr = ((Button)findViewById(R.id.btn_date)).getText().toString();
        String desc = ((TextView)findViewById(R.id.edt_desc)).getText().toString();
        String dbrStr = ((TextView)findViewById(R.id.edt_dbr)).getText().toString();

        if(!validateFields(desc, dbrStr)) return;

        int dbr = Integer.parseInt(dbrStr);

        DateFormat format = new SimpleDateFormat("M/d/yyyy");
        long time = 0;
        try {
            time = format.parse(dateStr).getTime();
        } catch (Exception e) {}

        data.putExtra("date", time);
        data.putExtra("desc", desc);
        data.putExtra("dbr", dbr);

        setResult(RESULT_OK, data);
        finish();
    }

    private void onDateButtonClicked() {
        Calendar cal = Calendar.getInstance();
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
        dpDialog.show();
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
