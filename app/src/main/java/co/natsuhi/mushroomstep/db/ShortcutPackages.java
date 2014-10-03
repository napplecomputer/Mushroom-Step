package co.natsuhi.mushroomstep.db;

import android.net.Uri;
import android.provider.BaseColumns;

public class ShortcutPackages implements BaseColumns {
    public static final String AUTHORITY = "co.natsuhi.mushroomstep.db.shortcut_package_provider";
    public static final String CONTENTS_URI_PREFIX = "shortcut_packages";
    public static final String CONTENT_ITEM_URI_DIR = "shortcut_package/";
    public static final String CONTENT_ITEM_URI_PREFIX = CONTENT_ITEM_URI_DIR
            + "#";
    public static final Uri COUNTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + CONTENTS_URI_PREFIX);
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mushroomstep.shortcut_packages";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mushroomstep.shortcut_packages";

    private static final String CONTENT_ITEM_URI_FORMAT = "content://%s/%s%d";

    public static final String TABLE_NAME = "shortcut_packages";
    public static final String COLUMN_PACKAGE_NAME = "package_name";
    public static final String COLUMN_ACTIVITY_NAME = "activity_name";
    public static final String COLUMN_IS_UNINSTALLED = "is_uninstalled";
    public static final String COLUMN_LABEL = "label";

    public static Uri getContentsUri(long id) {
        return Uri.parse(String.format(CONTENT_ITEM_URI_FORMAT, AUTHORITY,
                CONTENT_ITEM_URI_DIR, id));
    }
}
