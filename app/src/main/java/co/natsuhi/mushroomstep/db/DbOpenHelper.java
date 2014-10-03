package co.natsuhi.mushroomstep.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = DbOpenHelper.class.getSimpleName();

    private static final String DB_NAME = "mail_db";
    private static final int VERSION = 1;

    private static final String CREATE_SHORTCUT_PACKAGE_TABLE_SQL = "CREATE TABLE "
            + ShortcutPackages.TABLE_NAME + " ( "
            + ShortcutPackages._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + ShortcutPackages.COLUMN_PACKAGE_NAME + " TEXT NOT NULL,"
            + ShortcutPackages.COLUMN_ACTIVITY_NAME + " TEXT NOT NULL,"
            + ShortcutPackages.COLUMN_IS_UNINSTALLED + " INTEGER NOT NULL DEFAULT 0 CHECK(" + ShortcutPackages.COLUMN_IS_UNINSTALLED + " IN (0, 1))"
            + " )";

    private static DbOpenHelper sSingleton;

    private DbOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    public static DbOpenHelper getInstance(Context context) {
        if (sSingleton == null) {
            sSingleton = new DbOpenHelper(context);
        }
        return sSingleton;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SHORTCUT_PACKAGE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
