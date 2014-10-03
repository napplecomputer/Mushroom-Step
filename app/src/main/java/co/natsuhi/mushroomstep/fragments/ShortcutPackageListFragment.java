package co.natsuhi.mushroomstep.fragments;


import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

import co.natsuhi.mushroomstep.db.ShortcutPackages;

public class ShortcutPackageListFragment extends ListFragment {
    private static final String TAG = ShortcutPackageListFragment.class.getSimpleName();

    private SimpleCursorAdapter mSimpleCursorAdapter;

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
            mSimpleCursorAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mSimpleCursorAdapter.swapCursor(null);
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO:EmptyTextの追加
//        setEmptyText("hoge");

        String[] from = new String[]{ShortcutPackages.COLUMN_LABEL};
        int[] to = {android.R.id.text1};
        mSimpleCursorAdapter = new SimpleCursorAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, null, from, to, 0);
        setListAdapter(mSimpleCursorAdapter);

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null, mLoaderCallbacks);
    }
}
