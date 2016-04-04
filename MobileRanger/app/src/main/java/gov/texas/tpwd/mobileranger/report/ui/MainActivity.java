package gov.texas.tpwd.mobileranger.report.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import gov.texas.tpwd.mobileranger.AutoCompleteManager;
import gov.texas.tpwd.mobileranger.R;
import gov.texas.tpwd.mobileranger.report.data.TreeReport;
import gov.texas.tpwd.mobileranger.db.TreeReportManager;
import gov.texas.tpwd.mobileranger.report.writer.TreeReportWriter;
import gov.texas.tpwd.mobileranger.pdf.PdfWritable;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    protected static final int BEFORE_PHOTO_REQUEST_CODE = 1;
    protected static final int AFTER_PHOTO_REQUEST_CODE = 1;

    private AutoCompleteTextView reportingEmployeeText;
    private Button dateButton;

    private RecyclerView recyclerView;
    private TreeLocationAdapter adapter;

    private Calendar calendar;

    private DateFormat format = new SimpleDateFormat("MMMM d yyyy");

    private AutoCompleteManager autoCompleteManager;
    private EmployeeAutoCompleteAdapter employeeAutoCompleteAdapter;

    private TreeReport treeReport;
    private TreeReportManager treeReportManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calendar = Calendar.getInstance();
        autoCompleteManager = new AutoCompleteManager(getApplicationContext());
        employeeAutoCompleteAdapter = new EmployeeAutoCompleteAdapter();
        treeReportManager = new TreeReportManager(this);
        bindViews();
    }

    private void bindViews() {
        dateButton = (Button) findViewById(R.id.datePickerButton);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        reportingEmployeeText = (AutoCompleteTextView) findViewById(R.id.reportingEmployeeEdit);
        reportingEmployeeText.setAdapter(employeeAutoCompleteAdapter);
        reportingEmployeeText.setThreshold(2);

    }

    private void setupRecyclerView(int size) {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TreeLocationAdapter(this, size);
        recyclerView.setAdapter(adapter);
    }

    private final static String DATE_BUTTON_TEXT = "DateButtonText";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(!dateButton.getText().equals(getString(R.string.button_date_picker))) {
            outState.putString(DATE_BUTTON_TEXT, dateButton.getText().toString());
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.containsKey(DATE_BUTTON_TEXT) && dateButton != null) {
            dateButton.setText(savedInstanceState.getString(DATE_BUTTON_TEXT));
        }
    }

    private TreeReport updateTreeReportFromUi() {
        if(!dateButton.getText().equals(getString(R.string.button_date_picker))) {
            treeReport.setDate(dateButton.getText().toString());
        }
        treeReport.setReportingEmployee(reportingEmployeeText.getText().toString());

        treeReport.setLocations(adapter.getTreeLocations());

        return treeReport;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            Log.d("MainActivity", "result ok");
            // Image captured and saved to fileUri specified in the Intent
                if (requestCode == BEFORE_PHOTO_REQUEST_CODE) {
                    adapter.updateBeforeImage();
                } else if (requestCode == AFTER_PHOTO_REQUEST_CODE) {
                    adapter.updateAfterImage();
                }


        } else if (resultCode == RESULT_CANCELED) {
            // User cancelled the image capture
            Log.d("MainActivity", "cancelled");
        } else {
            Log.d("MainActivity", "something terrible");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(Menu.NONE, 0, Menu.NONE, "Share")
                .setIcon(R.drawable.ic_picture_as_pdf_white_24dp)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(Menu.NONE, 1, Menu.NONE, "Add")
                .setIcon(R.drawable.ic_add_white_24dp)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            (new GeneratePdfTask()).execute();
            return true;
        } else if(item.getItemId() == 1) {
            adapter.incrementSize();
        }

        return super.onOptionsItemSelected(item);
    }

    private String generatePdf() {
        getExternalFilesDir("dfs");
        File externalDir = Environment.getExternalStorageDirectory();
        String filePath = externalDir.getAbsolutePath() + "/" + "pdf_" + System.currentTimeMillis() + ".pdf";
        PdfWritable treeReportWriter = new TreeReportWriter(treeReport, filePath, getString(R.string.pdf_title));
        treeReportWriter.write();
        return filePath;
    }

    private void onPdfGenerated(String filePath) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + filePath));
        shareIntent.setType("application/pdf");
        startActivity(Intent.createChooser(shareIntent, "Share PDF"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!TextUtils.isEmpty(reportingEmployeeText.getText())) {
            autoCompleteManager.createOrUpdateAsync("employee", reportingEmployeeText.getText().toString());
        }
        adapter.onPause();
        updateTreeReportFromUi();
        treeReportManager.insertOrUpdateTreeReport(treeReport);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(treeReport == null) {
            treeReport = treeReportManager.getTreeReport();

            int locationSize = 1;
            boolean haveLocations = false;
            if(treeReport.getLocations() != null && treeReport.getLocations().size() > 0) {
                locationSize = treeReport.getLocations().size();
                haveLocations = true;
            }
            setupRecyclerView(locationSize);
            if(haveLocations) {
                adapter.setTreeLocations(treeReport.getLocations());
            }

            if(treeReport.getDate() != null) {
                dateButton.setText(treeReport.getDate());
            }

            if(treeReport.getReportingEmployee() != null) {
                reportingEmployeeText.setText(treeReport.getReportingEmployee());
            }
        }

    }

    public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            String date = format.format(calendar.getTime());
            dateButton.setText(date);
            treeReport.setDate(date);
        }
    }

    private class EmployeeAutoCompleteAdapter extends CursorAdapter {
        private LayoutInflater inflater;

        public EmployeeAutoCompleteAdapter() {
            super(MainActivity.this, null, 0);
            this.inflater = LayoutInflater.from(MainActivity.this);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            TextView textView = (TextView) inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
            return textView;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView textView = (TextView) view;
            textView.setText(cursor.getString(1));
        }

        @Override
        public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
            Log.d("####", "query " + constraint);
            return autoCompleteManager.getValuesCursor("employee", constraint != null ? constraint.toString() : null);
        }

        @Override
        public CharSequence convertToString(Cursor cursor) {
            return cursor.getString(1);
        }
    }

    private class GeneratePdfTask extends AsyncTask<Void, Void, String> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(MainActivity.this, "",
                    "Generating PDF", true, false);
        }

        @Override
        protected String doInBackground(Void... params) {
            return generatePdf();
        }

        @Override
        protected void onPostExecute(String filePath) {
            super.onPostExecute(filePath);
            dialog.dismiss();
            if(filePath != null) {
                onPdfGenerated(filePath);
            } else {
                Toast.makeText(MainActivity.this, "There was a problem generating your PDF", Toast.LENGTH_LONG).show();
            }
        }
    }
 }