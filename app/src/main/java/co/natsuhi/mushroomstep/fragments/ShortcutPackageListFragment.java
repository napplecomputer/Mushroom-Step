package co.natsuhi.mushroomstep.fragments;


import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ComponentName;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import co.natsuhi.mushroomstep.R;
import co.natsuhi.mushroomstep.ShortcutListAdapter;
import co.natsuhi.mushroomstep.db.ShortcutPackages;

public class ShortcutPackageListFragment extends ListFragment {
    private static final String TAG = ShortcutPackageListFragment.class.getSimpleName();

    private ShortcutListAdapter mShortcutListAdapter;

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String[] projection = new String[]{
                    ShortcutPackages._ID, ShortcutPackages.COLUMN_PACKAGE_NAME, ShortcutPackages.COLUMN_ACTIVITY_NAME, ShortcutPackages.COLUMN_IS_UNINSTALLED, ShortcutPackages.COLUMN_LABEL
            };

            // TODO:並び順変えるならここやで
            return new CursorLoader(getActivity().getApplicationContext(), ShortcutPackages.COUNTENT_URI, projection, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mShortcutListAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mShortcutListAdapter.swapCursor(null);
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO:EmptyTextの追加
//        setEmptyText("hoge");

        String[] from = new String[]{ShortcutPackages.COLUMN_LABEL};
        int[] to = {R.id.text};
        int imageTo = R.id.icon;
        ShortcutListAdapter.ImageGenerator imageGenerator = new ShortcutListAdapter.ImageGenerator() {
            @Override
            public Drawable generateImage(Cursor cursor, PackageManager packageManager) {
                String packageName = cursor.getString(cursor.getColumnIndex(ShortcutPackages.COLUMN_PACKAGE_NAME));
                String activityName = cursor.getString(cursor.getColumnIndex(ShortcutPackages.COLUMN_ACTIVITY_NAME));
                ComponentName componentName = new ComponentName(packageName, activityName);
                try {
                    return packageManager.getActivityIcon(componentName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
        mShortcutListAdapter = new ShortcutListAdapter(getActivity().getApplicationContext(), R.layout.applist, null, from, to, imageTo, imageGenerator, 0);
        setListAdapter(mShortcutListAdapter);

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null, mLoaderCallbacks);
    }
}
