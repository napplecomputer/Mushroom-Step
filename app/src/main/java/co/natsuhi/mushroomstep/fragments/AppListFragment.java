package co.natsuhi.mushroomstep.fragments;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

import co.natsuhi.mushroomstep.AppListAdapter;
import co.natsuhi.mushroomstep.AppLoader;
import co.natsuhi.mushroomstep.db.ShortcutPackages;

public class AppListFragment extends ListFragment {
    private static final String TAG = AppListFragment.class.getSimpleName();
    AppListAdapter mAppListAdapter;


    LoaderManager.LoaderCallbacks<List<AppLoader.AppEntry>> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<List<AppLoader.AppEntry>>() {
        @Override
        public Loader<List<AppLoader.AppEntry>> onCreateLoader(int id, Bundle args) {
            return new AppLoader.AppListLoader(getActivity());
        }

        @Override
        public void onLoadFinished(Loader<List<AppLoader.AppEntry>> loader, List<AppLoader.AppEntry> data) {
            mAppListAdapter.setData(data);

            if (isResumed()) {
                setListShown(true);
            } else {
                setListShownNoAnimation(true);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<AppLoader.AppEntry>> loader) {
            mAppListAdapter.setData(null);
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText("Application is Not Found");
        mAppListAdapter = new AppListAdapter(getActivity());

        setListAdapter(mAppListAdapter);
        setListShown(false);

        getLoaderManager().initLoader(0, null, mLoaderCallbacks);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppLoader.AppEntry app = mAppListAdapter.getItem(position);
                Log.d(TAG, "onItemClick");
                Log.d(TAG, "app:" + app.getLabel());


                ContentValues contentValues = new ContentValues();
                contentValues.put(ShortcutPackages.COLUMN_ACTIVITY_NAME, app.getActivityName());
                contentValues.put(ShortcutPackages.COLUMN_PACKAGE_NAME, app.getPackageName());
                contentValues.put(ShortcutPackages.COLUMN_LABEL, app.getLabel());
                getActivity().getContentResolver().insert(ShortcutPackages.COUNTENT_URI, contentValues);
            }
        });

    }
}
