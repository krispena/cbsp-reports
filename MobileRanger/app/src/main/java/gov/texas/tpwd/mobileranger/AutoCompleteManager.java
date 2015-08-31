package gov.texas.tpwd.mobileranger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class AutoCompleteManager {

    private static final String TABLE_TEXT = "text";
    private static final String COL_ID = "_id";
    private static final String COL_TYPE = "type";
    private static final String COL_VALUE = "value";
    private static final String COL_CREATE_DATE = "create_date";
    private static final String COL_UPDATE_DATE = "update_date";
    private String[] COLS = new String[]{COL_ID, COL_TYPE, COL_VALUE, COL_CREATE_DATE, COL_UPDATE_DATE};

    private Context context;
    private MySQLiteOpenHelper helper;

    public AutoCompleteManager(Context context) {
        this.context = context;
        this.helper = new MySQLiteOpenHelper();
    }

    public void createOrUpdateAsync(String type, String value) {
        new CreateOrUpdateTask(type, value).execute();
    }

    public void createOrUpdate(String type, String value) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues(4);
            cv.put(COL_TYPE, type);
            cv.put(COL_VALUE, value);
            cv.put(COL_UPDATE_DATE, System.currentTimeMillis());
            db.beginTransaction();
            int rows = db.update(TABLE_TEXT, cv, "type=? AND value=?", new String[]{type, value});
            if (rows == 0) {
                cv.put(COL_CREATE_DATE, System.currentTimeMillis());
                long id = db.insert(TABLE_TEXT, null, cv);
                Log.d("####", "insert " + id);
            } else {
                Log.d("####", "updated " + rows);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getValuesCursor(String type, String filter) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Log.d("####", "cursor " + filter);
        if (TextUtils.isEmpty(filter)) {
            return db.query(TABLE_TEXT, new String[]{COL_ID, COL_VALUE}, "type=?", new String[]{type}, null, null, "update_date DESC");
        } else {
            return db.query(TABLE_TEXT, new String[]{COL_ID, COL_VALUE}, "type=? AND value LIKE ?", new String[]{type, "%" + filter + "%"}, null, null, "update_date DESC");
        }
    }

    private class CreateOrUpdateTask extends AsyncTask<Void, Void, Void> {
        private String type;
        private String value;

        public CreateOrUpdateTask(String type, String value) {
            this.type = type;
            this.value = value;
        }

        @Override
        protected Void doInBackground(Void... params) {
            createOrUpdate(type, value);
            return null;
        }
    }

    private class MySQLiteOpenHelper extends SQLiteOpenHelper {
        private static final String NAME = "autocomplete.db";
        private static final int VERSION = 1;

        public MySQLiteOpenHelper() {
            super(context, NAME, null, VERSION, new MyDatabaseErrorHandler());
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            onUpgrade(db, 0, VERSION);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("CREATE TABLE text (_id INTEGER PRIMARY KEY, type TEXT, value TEXT, create_date INTEGER, update_date INTEGER);");
        }
    }

    private class MyDatabaseErrorHandler implements DatabaseErrorHandler {

        @Override
        public void onCorruption(SQLiteDatabase dbObj) {
            Toast.makeText(context, "Error: Database Corrupt.", Toast.LENGTH_LONG).show();
        }
    }
}
