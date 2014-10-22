package co.natsuhi.mushroomstep.utils;

import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import co.natsuhi.mushroomstep.db.ShortcutPackages;

public class ShortcutPackageGeneretor {

    private Cursor mCursor;
    private String mPackageName;
    private ComponentName mComponentName;

    public ShortcutPackageGeneretor(Cursor cursor) {
        mCursor = cursor;
    }

    private void generateComponentNameExe() {
        mPackageName = mCursor.getString(mCursor.getColumnIndex(ShortcutPackages.COLUMN_PACKAGE_NAME));
        String activityName = mCursor.getString(mCursor.getColumnIndex(ShortcutPackages.COLUMN_ACTIVITY_NAME));
        mComponentName = new ComponentName(mPackageName, activityName);
    }

    public ComponentName generateComponentName() {
        if (mComponentName == null || mPackageName == null) {
            generateComponentNameExe();
        }

        return mComponentName;
    }

    public Intent generateIntent() {
        if (mComponentName == null || mPackageName == null) {
            generateComponentNameExe();
        }

        Uri uri = Uri.fromParts("package", mPackageName, null);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setAction(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.setData(uri);
        intent.setComponent(mComponentName);
        return intent;
    }
}
