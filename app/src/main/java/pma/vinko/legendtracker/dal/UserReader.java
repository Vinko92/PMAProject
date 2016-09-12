package pma.vinko.legendtracker.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Vinko on 11.9.2016..
 */
public class UserReader extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReaderr.db";

    public UserReader(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserReaderContract.SQL_CREATE_ENTRIES);
        db.execSQL(UserReaderContract.SETTINGS_SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(UserReaderContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void insert(String id, String name){
        // Gets the data repository in write mode
        SQLiteDatabase db = getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(UserReaderContract.COLUMN_NAME_TITLE, name);
        values.put(UserReaderContract.COLUMN_NAME_ID, id);

// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(UserReaderContract.TABLE_NAME, null, values);
    }

    public long insertSettings(String id, String name){
        // Gets the data repository in write mode

        SQLiteDatabase db = getWritableDatabase();
// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(UserReaderContract.SETTINGS_COLUMN_NAME_TITLE, name);
        values.put(UserReaderContract.SETTINGS_COLUMN_NAME_ID, id);

// Insert the new row, returning the primary key value of the new row
       return db.insert(UserReaderContract.SETTINGS_TABLE_NAME, null, values);
    }

    public void update(String id, String name){
        SQLiteDatabase db = getReadableDatabase();

// New value for one column
        ContentValues values = new ContentValues();
        values.put(UserReaderContract.SETTINGS_COLUMN_NAME_TITLE, name);

// Which row to update, based on the title
        String selection = UserReaderContract.SETTINGS_COLUMN_NAME_ID + " LIKE ?";
        String[] selectionArgs = { id };

        int count = db.update(
                UserReaderContract.SETTINGS_TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    public Cursor getAll(){
        SQLiteDatabase db = getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                UserReaderContract._ID,
                UserReaderContract.COLUMN_NAME_TITLE,
                UserReaderContract.COLUMN_NAME_ID
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = UserReaderContract.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = { "My Title" };

        Cursor c = db.query(
                UserReaderContract.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        return c;
    }

    public String getIsImageVisible(){
        SQLiteDatabase db = getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                UserReaderContract.SETTINGS_COLUMN_NAME_TITLE,
                UserReaderContract.SETTINGS_COLUMN_NAME_ID
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = UserReaderContract.SETTINGS_COLUMN_NAME_TITLE + " = 'showImages'";

        Cursor c = db.query(
                UserReaderContract.SETTINGS_TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        String data = "";

        if(c.moveToFirst()){
            data = c.getString(c.getColumnIndex(UserReaderContract.SETTINGS_COLUMN_NAME_ID));
        }

        return data;
    }
}