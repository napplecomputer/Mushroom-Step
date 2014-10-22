package co.natsuhi.mushroomstep;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import co.natsuhi.mushroomstep.db.ShortcutPackages;
import co.natsuhi.mushroomstep.utils.ShortcutPackageGeneretor;


public class ShortcutActivity extends Activity {
    private static final String TAG = ShortcutActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntentForShortcut();
        startActivity(intent);
    }

    private Intent getIntentForShortcut() {

        String[] projection = {ShortcutPackages.COLUMN_PACKAGE_NAME, ShortcutPackages.COLUMN_ACTIVITY_NAME};
        Cursor cursor = getContentResolver().query(ShortcutPackages.COUNTENT_URI, projection, null, null, null);
        int rowNumber = cursor.getCount();
        if (rowNumber == 0) {
            return null;
        }

        cursor.moveToNext();
        ShortcutPackageGeneretor shortcutPackageGeneretor = new ShortcutPackageGeneretor(cursor);
        Intent intent = shortcutPackageGeneretor.generateIntent();
        return intent;
    }
}
