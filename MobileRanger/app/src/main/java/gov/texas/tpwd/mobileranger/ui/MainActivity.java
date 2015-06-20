package gov.texas.tpwd.mobileranger.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import gov.texas.tpwd.mobileranger.R;
import gov.texas.tpwd.mobileranger.TreeReport;
import gov.texas.tpwd.mobileranger.TreeReportWriter;
import gov.texas.tpwd.mobileranger.pdf.PdfWritable;


public class MainActivity extends AppCompatActivity {

    private static final int BEFORE_PHOTO_REQUEST_CODE = 1;
    private static final int AFTER_PHOTO_REQUEST_CODE = 1;

    private EditText reportingEmployeeText;
    private EditText locationText;
    private EditText detailsText;
    private EditText actionTakenText;
    private ImageView beforePhoto;
    private ImageView afterPhoto;
    private Button dateButton;
    private Button beforePhotoButton;
    private Button afterPhotoButton;

    private File beforePhotoFile;
    private File afterPhotoFile;

    private Calendar calendar;

    private DateFormat format = new SimpleDateFormat("MMMM d yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calendar = Calendar.getInstance();
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
        reportingEmployeeText = (EditText) findViewById(R.id.reportingEmployeeEdit);
        locationText = (EditText) findViewById(R.id.locationEdit);
        detailsText = (EditText) findViewById(R.id.detailText);
        actionTakenText = (EditText) findViewById(R.id.actionTakenText);
        beforePhoto = (ImageView) findViewById(R.id.beforeImage);
        afterPhoto = (ImageView) findViewById(R.id.afterImage);
        beforePhotoButton = (Button) findViewById(R.id.buttonBefore);
        beforePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beforePhotoFile = startCamera(BEFORE_PHOTO_REQUEST_CODE);
            }
        });
        afterPhotoButton = (Button) findViewById(R.id.buttonAfter);
        afterPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                afterPhotoFile = startCamera(AFTER_PHOTO_REQUEST_CODE);
            }
        });
    }

    private File startCamera(int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + System.currentTimeMillis() + ".png";
        File file = new File(path);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, requestCode);
        return file;
    }

    private final static String DATE_BUTTON_TEXT = "DateButtonText";
    private final static String BEFORE_PHOTO_PATH = "BeforePhotoPath";
    private final static String AFTER_PHOTO_PATH = "AfterPhotoPath";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(!dateButton.getText().equals(getString(R.string.button_date_picker))) {
            outState.putString(DATE_BUTTON_TEXT, dateButton.getText().toString());
        }
        outState.putSerializable(BEFORE_PHOTO_PATH, beforePhotoFile);
        outState.putSerializable(AFTER_PHOTO_PATH, afterPhotoFile);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.containsKey(DATE_BUTTON_TEXT) && dateButton != null) {
            dateButton.setText(savedInstanceState.getString(DATE_BUTTON_TEXT));
        }
        beforePhotoFile = (File) savedInstanceState.getSerializable(BEFORE_PHOTO_PATH);
        afterPhotoFile = (File) savedInstanceState.getSerializable(AFTER_PHOTO_PATH);
        loadBeforeImage();
        loadAfterImage();
    }

    private TreeReport getTreeReport() {
        TreeReport treeReport = new TreeReport();
        if(!dateButton.getText().equals(getString(R.string.button_date_picker))) {
            treeReport.setDate(dateButton.getText().toString());
        }
        treeReport.setReportingEmployee(reportingEmployeeText.getText().toString());
        treeReport.setLocation(locationText.getText().toString());
        treeReport.setDetails(detailsText.getText().toString());
        treeReport.setActionTaken(actionTakenText.getText().toString());

        if (beforePhotoFile != null) {
            treeReport.setBeforeImagePath(beforePhotoFile.getAbsolutePath());
        }

        if (afterPhotoFile != null) {
            treeReport.setAfterImagePath(afterPhotoFile.getAbsolutePath());
        }

        return treeReport;
    }

    private void loadBeforeImage() {
        if(beforePhotoFile != null && beforePhoto != null) {
            Glide.with(this).load(beforePhotoFile).into(beforePhoto);
        }
    }

    private void loadAfterImage() {
        if(afterPhotoFile != null && afterPhoto != null) {
            Glide.with(this).load(afterPhotoFile).into(afterPhoto);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            Log.d("MainActivity", "result ok");
            // Image captured and saved to fileUri specified in the Intent

                if (requestCode == BEFORE_PHOTO_REQUEST_CODE) {
                    loadBeforeImage();
                } else if (requestCode == AFTER_PHOTO_REQUEST_CODE) {
                    loadAfterImage();
                } else {
                    Log.d("MainActivity", "request code doesn't match");
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
        menu.add(Menu.NONE, 0, Menu.NONE, "Share").setIcon(R.drawable.ic_share_white_24dp)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            TreeReport report = getTreeReport();
            getExternalFilesDir("dfs");
            File externalDir = Environment.getExternalStorageDirectory();
            String filePath = externalDir.getAbsolutePath() + "/" + "pdf_" + System.currentTimeMillis() + ".pdf";
            PdfWritable treeReportWriter = new TreeReportWriter(report, filePath, getString(R.string.pdf_title));
            treeReportWriter.write();
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + filePath));
            shareIntent.setType("application/pdf");
            startActivity(Intent.createChooser(shareIntent, "Share PDF"));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DatePickerFragment extends DialogFragment
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
            dateButton.setText(format.format(calendar.getTime()));
        }
    }
}
