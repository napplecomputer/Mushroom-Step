package co.natsuhi.mushroomstep.db;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

import co.natsuhi.mushroomstep.utils.LogUtil;

public class ShortcutPackagesProvider extends ContentProvider {
    private static final String TAG = ShortcutPackagesProvider.class.getSimpleName();

    private static final int CODE_SHORTCUT_PACKAGES = 0;
    private static final int CODE_SHORTCUT_PACKAGE_ID = 1;

    private static UriMatcher mUriMatcher;
    private static HashMap<String, String> mProjectionMap;

    private DbOpenHelper mDbOpenHelper;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(ShortcutPackages.AUTHORITY,
                ShortcutPackages.CONTENTS_URI_PREFIX, CODE_SHORTCUT_PACKAGES);
        mUriMatcher.addURI(ShortcutPackages.AUTHORITY,
                ShortcutPackages.CONTENT_ITEM_URI_PREFIX, CODE_SHORTCUT_PACKAGE_ID);


        mProjectionMap = new HashMap<String, String>();
        mProjectionMap.put(ShortcutPackages._ID, ShortcutPackages._ID);
        mProjectionMap.put(ShortcutPackages.COLUMN_ACTIVITY_NAME, ShortcutPackages.COLUMN_ACTIVITY_NAME);
        mProjectionMap.put(ShortcutPackages.COLUMN_PACKAGE_NAME, ShortcutPackages.COLUMN_PACKAGE_NAME);
        mProjectionMap.put(ShortcutPackages.COLUMN_IS_UNINSTALLED, ShortcutPackages.COLUMN_IS_UNINSTALLED);
    }

    @Override
    public boolean onCreate() {
        mDbOpenHelper = DbOpenHelper.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        LogUtil.d(TAG, "query");
        SQLiteDatabase sqLiteDatabase = mDbOpenHelper.getReadableDatabase();

        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(ShortcutPackages.TABLE_NAME);
        sqLiteQueryBuilder.setProjectionMap(mProjectionMap);

        String limit = null;
        switch (mUriMatcher.match(uri)) {
            case CODE_SHORTCUT_PACKAGES:
                limit = uri.getQueryParameter("limit");
                break;
            case CODE_SHORTCUT_PACKAGE_ID:
                long id = ContentUris.parseId(uri);
                sqLiteQueryBuilder.appendWhere(ShortcutPackages._ID + "=" + id);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        Cursor cursor = sqLiteQueryBuilder.query(sqLiteDatabase, projection, selection, selectionArgs, null, null, null, limit);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case CODE_SHORTCUT_PACKAGES:
                return ShortcutPackages.CONTENT_TYPE;
            case CODE_SHORTCUT_PACKAGE_ID:
                return ShortcutPackages.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        LogUtil.d(TAG, "insert");
        if (mUriMatcher.match(uri) != CODE_SHORTCUT_PACKAGES) {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase sqLiteDatabase = mDbOpenHelper.getWritableDatabase();
        long rowId = sqLiteDatabase.insert(ShortcutPackages.TABLE_NAME, null, values);
        if (rowId <= 0) {
            throw new SQLException("Failed to insert row into: " + uri);
        }

        Uri shortcutPackageUri = ContentUris.withAppendedId(ShortcutPackages.COUNTENT_URI, rowId);
        getContext().getContentResolver().notifyChange(ShortcutPackages.COUNTENT_URI, null);

        return shortcutPackageUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        LogUtil.d(TAG, "delete");
        SQLiteDatabase sqLiteDatabase = mDbOpenHelper.getWritableDatabase();
        int changed = 0;
        switch (mUriMatcher.match(uri)) {
            case CODE_SHORTCUT_PACKAGES:
                changed = sqLiteDatabase.delete(ShortcutPackages.TABLE_NAME, selection, selectionArgs);
                break;
            case CODE_SHORTCUT_PACKAGE_ID:
                String selectionWithId = generateSelectionWithId(selection, uri);
                changed = sqLiteDatabase.delete(ShortcutPackages.TABLE_NAME, selectionWithId, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(ShortcutPackages.COUNTENT_URI, null);
        return changed;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        LogUtil.d(TAG, "update");
        SQLiteDatabase sqLiteDatabase = mDbOpenHelper.getWritableDatabase();
        int changed = 0;
        switch (mUriMatcher.match(uri)) {
            case CODE_SHORTCUT_PACKAGES:
                changed = sqLiteDatabase.update(ShortcutPackages.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CODE_SHORTCUT_PACKAGE_ID:
                String selectionWithId = generateSelectionWithId(selection, uri);
                changed = sqLiteDatabase.update(ShortcutPackages.TABLE_NAME, values, selectionWithId, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(ShortcutPackages.COUNTENT_URI, null);
        return changed;
    }

    private String generateSelectionWithId(String selection, Uri uri) {
        long id = ContentUris.parseId(uri);
        return (TextUtils.isEmpty(selection)) ? String
                .format("%s=%d", ShortcutPackages._ID, id) : String
                .format("%s=%d AND (%s)", ShortcutPackages._ID, id,
                        selection);
    }
}
