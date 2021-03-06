package gov.texas.tpwd.mobileranger.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import gov.texas.tpwd.mobileranger.report.data.TreeLocation;
import gov.texas.tpwd.mobileranger.report.data.TreeReport;

public class TreeReportManager {

    private static final String TAG = TreeReportManager.class.getSimpleName();

    private static final String TABLE_TREE_LOCATION = "TreeLocation";
    private static final String TABLE_TREE_REPORT = "TreeReport";

    private static final String COL_ID = "_id";

    private static final String COL_DATE = "date";
    private static final String COL_REPORTING_EMPLOYEE = "reporting_employee";
    private static final String[] REPORT_COLS = new String[]{COL_ID, COL_DATE, COL_REPORTING_EMPLOYEE};

    private static final String COL_TREE_REPORT_ID = "tree_report_id";
    private static final String TREE_REPORT_ID_WHERE_CLAUSE = COL_TREE_REPORT_ID + "=?";
    private static final String ID_WHERE_CLAUSE = COL_ID + "=?";

    private static final String COL_LOCATION = "location";
    private static final String COL_DETAILS = "details";
    private static final String COL_ACTION_TAKEN = "action_taken";
    private static final String COL_BEFORE_IMAGE_PATH = "before_image_path";
    private static final String COL_AFTER_IMAGE_PATH = "after_image_path";
    private static final String[] LOCATION_COLS = new String[]{COL_ID, COL_TREE_REPORT_ID, COL_LOCATION, COL_DETAILS, COL_ACTION_TAKEN, COL_BEFORE_IMAGE_PATH, COL_AFTER_IMAGE_PATH};


    private Context context;
    private MySQLiteOpenHelper helper;
    private static TreeReportManager instance;

    public static TreeReportManager getInstance(Context context) {
        if(instance == null) {
            instance = new TreeReportManager(context);
        }
        return instance;
    }

    private TreeReportManager(Context context) {
        this.context = context;
        this.helper = new MySQLiteOpenHelper();
    }

//    public void createOrUpdateAsync(String type, String value) {
//        new CreateOrUpdateTask(type, value).execute();
//    }

    public long insertOrUpdateTreeReport(TreeReport report) {
        SQLiteDatabase db = helper.getWritableDatabase();
        long treeReportId = -1;
        try {
            db.beginTransaction();

            ContentValues values = new ContentValues();
            if(report.getId() > 0) {
                values.put(COL_ID, report.getId());
            }
            values.put(COL_DATE, report.getDate());
            values.put(COL_REPORTING_EMPLOYEE, report.getReportingEmployee());
            treeReportId = db.insertWithOnConflict(TABLE_TREE_REPORT, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            Log.d("TreeReportManager", "insert treeReport " + treeReportId);
            for(TreeLocation location:report.getLocations()) {
                ContentValues locationValues = new ContentValues();
                if(location.getId() > 0) {
                    locationValues.put(COL_ID, location.getId());
                }
                locationValues.put(COL_TREE_REPORT_ID, treeReportId);
                locationValues.put(COL_LOCATION, location.getLocation());
                locationValues.put(COL_DETAILS, location.getDetails());
                locationValues.put(COL_ACTION_TAKEN, location.getActionTaken());
                locationValues.put(COL_BEFORE_IMAGE_PATH, location.getBeforeImagePath());
                locationValues.put(COL_AFTER_IMAGE_PATH, location.getAfterImagePath());
                long locationId = db.insertWithOnConflict(TABLE_TREE_LOCATION, null, locationValues, SQLiteDatabase.CONFLICT_REPLACE);
                Log.d("TreeReportManager", "insert " + locationId + " with report id " + treeReportId + " location name " + location.getLocation());
                if(locationId != -1) {
                    location.setId(locationId);
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return treeReportId;
    }

    public void deleteTreeReport(TreeReport treeReport) {
        if(treeReport == null) {
            return;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.beginTransaction();
            String deleteId = Long.toString(treeReport.getId());
            db.delete(TABLE_TREE_REPORT, ID_WHERE_CLAUSE, new String[]{deleteId});
            db.delete(TABLE_TREE_LOCATION, TREE_REPORT_ID_WHERE_CLAUSE, new String[]{deleteId});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

    public List<TreeReport> getTreeReports() {
        List<TreeReport> reports = new ArrayList<>();
        long treeReportId = -1;
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query(TABLE_TREE_REPORT, REPORT_COLS, null, null, null, null, null);
        while(cursor.moveToNext()) { //just do first report for now, should only be 1
            TreeReport treeReport = new TreeReport();
            setTreeReport(treeReport, cursor);
            reports.add(treeReport);
        }
        cursor.close();


        return reports;
    }

    public TreeReport getTreeReport(long id) {
        TreeReport treeReport = new TreeReport();
        long treeReportId = -1;
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor;
        if(id > 0) {
            cursor = db.query(TABLE_TREE_REPORT, REPORT_COLS, ID_WHERE_CLAUSE, new String[]{Long.toString(id)}, null, null, null);
        } else {
            cursor = db.query(TABLE_TREE_REPORT, REPORT_COLS, null, null, null, null, null);
        }
        if(cursor.moveToNext()) { //just do first report for now, should only be 1
            treeReportId = setTreeReport(treeReport, cursor);
        }
        cursor.close();
        if(treeReportId > 0) {
            addTreeLocations(db, treeReport);
        } else {
            Log.d(TAG, "no report to add locations to");
        }

        return treeReport;
    }

    private long setTreeReport(TreeReport treeReport, Cursor cursor) {
        long treeReportId = cursor.getLong(cursor.getColumnIndex(COL_ID));
        treeReport.setId(treeReportId);
        treeReport.setDate(cursor.getString(cursor.getColumnIndex(COL_DATE)));
        treeReport.setReportingEmployee(cursor.getString(cursor.getColumnIndex(COL_REPORTING_EMPLOYEE)));
        return treeReportId;
    }

    private void addTreeLocations(SQLiteDatabase db, TreeReport treeReport) {
        String reportId = Long.toString(treeReport.getId());
        Cursor cursor = db.query(TABLE_TREE_LOCATION, LOCATION_COLS, TREE_REPORT_ID_WHERE_CLAUSE, new String[]{reportId}, null, null, null);
        while(cursor.moveToNext()) {
            TreeLocation location = new TreeLocation();
            location.setId(cursor.getLong(cursor.getColumnIndex(COL_ID)));
            location.setTreeReportId(cursor.getLong(cursor.getColumnIndex(COL_TREE_REPORT_ID)));
            location.setLocation(cursor.getString(cursor.getColumnIndex(COL_LOCATION)));
            location.setDetails(cursor.getString(cursor.getColumnIndex(COL_DETAILS)));
            location.setActionTaken(cursor.getString(cursor.getColumnIndex(COL_ACTION_TAKEN)));
            location.setBeforeImagePath(cursor.getString(cursor.getColumnIndex(COL_BEFORE_IMAGE_PATH)));
            location.setAfterImagePath(cursor.getString(cursor.getColumnIndex(COL_AFTER_IMAGE_PATH)));
            treeReport.addLocation(location);
            Log.d(TAG, "added tree report with location:" + location.getLocation());
        }
    }

//    private class CreateOrUpdateTask extends AsyncTask<Void, Void, Void> {
//        private String type;
//        private String value;
//
//        public CreateOrUpdateTask(String type, String value) {
//            this.type = type;
//            this.value = value;
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            createOrUpdate(type, value);
//            return null;
//        }
//    }

    private class MySQLiteOpenHelper extends SQLiteOpenHelper {
        private static final String NAME = "treeReport.db";
        private static final int VERSION = 1;

        public MySQLiteOpenHelper() {
            super(context, NAME, null, VERSION, new MyDatabaseErrorHandler());
            Log.d(TAG, "database constructor");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            onUpgrade(db, 0, VERSION);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if(oldVersion < 1) {
                db.execSQL("CREATE TABLE TreeLocation (_id INTEGER PRIMARY KEY, tree_report_id INTEGER, location TEXT, details TEXT, action_taken TEXT, before_image_path TEXT, after_image_path TEXT);");
                db.execSQL("CREATE TABLE TreeReport (_id INTEGER PRIMARY KEY, date TEXT, reporting_employee TEXT);");
            }
        }
    }

    private class MyDatabaseErrorHandler implements DatabaseErrorHandler {

        @Override
        public void onCorruption(SQLiteDatabase dbObj) {
            Toast.makeText(context, "Error: Database Corrupt.", Toast.LENGTH_LONG).show();
        }
    }
}
