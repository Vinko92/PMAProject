package pma.vinko.legendtracker.dal;

import android.provider.BaseColumns;

/**
 * Created by Vinko on 11.9.2016..
 */
public final class UserReaderContract implements BaseColumns  {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private UserReaderContract() {}

    public static final String SETTINGS_TABLE_NAME = "settings";
    public static final String SETTINGS_COLUMN_NAME_TITLE = "title";
    public static final String SETTINGS_COLUMN_NAME_ID = "id";

        public static final String TABLE_NAME = "user";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_ID = "id";

        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP = ",";
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + UserReaderContract.TABLE_NAME + " (" +
                        UserReaderContract._ID + " INTEGER PRIMARY KEY," +
                        UserReaderContract.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                        UserReaderContract.COLUMN_NAME_ID + TEXT_TYPE + " )";

    public static final String SETTINGS_SQL_CREATE_ENTRIES =
            "CREATE TABLE " + UserReaderContract.SETTINGS_TABLE_NAME + " (" +
                    UserReaderContract._ID + " INTEGER PRIMARY KEY," +
                    UserReaderContract.SETTINGS_COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    UserReaderContract.SETTINGS_COLUMN_NAME_ID + TEXT_TYPE + " )";
    public static final String SETT_SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + UserReaderContract.SETTINGS_TABLE_NAME;
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + UserReaderContract.TABLE_NAME;

}
