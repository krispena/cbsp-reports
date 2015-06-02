package gov.texas.tpwd.mobileranger.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;

import gov.texas.tpwd.mobileranger.R;
import gov.texas.tpwd.mobileranger.TreeReport;
import gov.texas.tpwd.mobileranger.TreeReportWriter;
import gov.texas.tpwd.mobileranger.pdf.PdfWritable;


public class MainActivity extends AppCompatActivity {

    private static final int BEFORE_PHOTO_REQUEST_CODE = 1;

    private EditText dateText;
    private EditText reportingEmployeeText;
    private EditText locationText;
    private EditText detailsText;
    private EditText actionTakenText;
    private ImageView beforePhoto;
    private ImageView afterPhoto;
    private Button beforePhotoButton;
    private Button afterPhotoButton;

    private String mBeforePhotoPath;
    private String mAfterPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
    }

    private void bindViews() {
        dateText = (EditText) findViewById(R.id.dateEdit);
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
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mBeforePhotoPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + System.currentTimeMillis() + ".png";
                Log.d("MainActivity", "Before photo path: " + mBeforePhotoPath);
                File file = new File(mBeforePhotoPath);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent, BEFORE_PHOTO_REQUEST_CODE);
            }
        });
        afterPhotoButton = (Button) findViewById(R.id.buttonAfter);
    }

    private TreeReport getTreeReport() {
        TreeReport treeReport = new TreeReport();
        treeReport.setDate(dateText.getText().toString());
        treeReport.setReportingEmployee(reportingEmployeeText.getText().toString());
        treeReport.setLocation(locationText.getText().toString());
        treeReport.setDetails(detailsText.getText().toString());
        treeReport.setActionTaken(actionTakenText.getText().toString());

        if(mBeforePhotoPath != null) {
            Log.d("MainActivity", "setting before path");
            treeReport.setBeforeImagePath(mBeforePhotoPath);
        }

        if(mAfterPhotoPath != null) {
            treeReport.setAfterImagePath(mAfterPhotoPath);
        }

        return treeReport;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BEFORE_PHOTO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                if (data != null) {
                    mBeforePhotoPath = data.getData().toString();
                }
                Log.d("MainActivity", "photo path:" + mBeforePhotoPath);
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
                Log.d("MainActivity", "cancelled");
            } else {
                Log.d("MainActivity", "something terrible");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(Menu.NONE, 0, Menu.NONE, "Save");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == 0) {
            Log.d("MainActivity", "start writing");
            TreeReport report = getTreeReport();
            getExternalFilesDir("dfs");
            File externalDir = Environment.getExternalStorageDirectory();
            String filePath = externalDir.getAbsolutePath() + "/" + "pdf_" + System.currentTimeMillis() + ".pdf";
            Log.d("MainActivity", "File:" + filePath);
            PdfWritable treeReportWriter = new TreeReportWriter(report,filePath);
            treeReportWriter.write();
            Log.d("MainActivity", "done writing");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
