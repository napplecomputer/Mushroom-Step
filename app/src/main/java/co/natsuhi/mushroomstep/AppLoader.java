package co.natsuhi.mushroomstep;

import android.content.AsyncTaskLoader;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppLoader {
    public static class AppEntry {
        private final PackageManager mPackageManager;
        private final ActivityInfo mActivityInfo;

        private String mLabel;
        private Drawable mIcon;
        private String mPackageName;

        public AppEntry(PackageManager packageManager, ActivityInfo activityInfo) {
            mPackageManager = packageManager;
            mActivityInfo = activityInfo;
        }

        public String getLabel() {
            if (mLabel == null) {
                CharSequence label = mActivityInfo.loadLabel(mPackageManager);
                if (label == null) {
                    mLabel = mActivityInfo.packageName;
                } else {
                    mLabel = label.toString();
                }
            }
            return mLabel;
        }

        public Drawable getIcon() {
            if (mIcon == null) {
                mIcon = mActivityInfo.loadIcon(mPackageManager);
            }
            return mIcon;
        }

        @Override
        public String toString() {
            return getLabel();
        }

        public String getPackageName() {
            return mActivityInfo.packageName;
        }

        public String getActivityName() {
            return mActivityInfo.name;
        }

        public Intent getLaunchIntent() {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName componentName = new ComponentName(
                    mActivityInfo.applicationInfo.packageName,
                    mActivityInfo.name);
            intent.setComponent(componentName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return intent;
        }
    }

    public static final Comparator<AppEntry> ALPHA_COMPARATOR = new Comparator<AppEntry>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(AppEntry lhs, AppEntry rhs) {
            return sCollator.compare(lhs.getLabel(), rhs.getLabel());
        }
    };

    public static class PackageChangeReceiver extends BroadcastReceiver {
        final AppListLoader mAppListLoader;

        public PackageChangeReceiver(AppListLoader appListLoader) {
            mAppListLoader = appListLoader;

            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
            intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
            intentFilter.addDataScheme("package");
            mAppListLoader.getContext().registerReceiver(this, intentFilter);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            mAppListLoader.onContentChanged();
        }
    }

    public static class AppListLoader extends AsyncTaskLoader<List<AppEntry>> {
        final PackageManager mPackageManager;

        List<AppEntry> mAppList;
        PackageChangeReceiver mPackageChangeReceiver;

        public AppListLoader(Context context) {
            super(context);
            mPackageManager = getContext().getPackageManager();
        }

        @Override
        public List<AppEntry> loadInBackground() {
            String myPackageName = getContext().getPackageName();

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> infos = mPackageManager.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);


//            int flags = PackageManager.GET_UNINSTALLED_PACKAGES;
//            List<ApplicationInfo> appInfos = mPackageManager.getInstalledApplications(flags);

            if (infos == null) {
                infos = new ArrayList<ResolveInfo>();
            }

            // 自分の分引く
            List<AppEntry> entries = new ArrayList<AppEntry>(infos.size() - 1);

            for (int i = 0, size = infos.size(); i < size; i++) {
                ResolveInfo resolveInfo = infos.get(i);
                ActivityInfo activityInfo = resolveInfo.activityInfo;
                String packageName = activityInfo.packageName;
                if (myPackageName.equals(packageName)) {
                    continue;
                }
                AppEntry entry = new AppEntry(mPackageManager, activityInfo);
                entries.add(entry);
            }

            Collections.sort(entries, ALPHA_COMPARATOR);

            return entries;
        }

        @Override
        public void deliverResult(List<AppEntry> data) {
            if (isReset()) {
                if (data != null) {
                    onReleaseResources(data);
                }
                return;
            }

            List<AppEntry> oldApps = data;
            mAppList = data;

            if (isStarted()) {
                super.deliverResult(data);
            }

            if (oldApps != null && oldApps != data) {
                onReleaseResources(oldApps);
            }
        }

        @Override
        protected void onStartLoading() {
            if (mAppList != null) {
                deliverResult(mAppList);
            }

            if (mPackageChangeReceiver == null) {
                mPackageChangeReceiver = new PackageChangeReceiver(this);
            }

            if (takeContentChanged() || mAppList == null) {
                forceLoad();
            }
        }

        @Override
        protected void onStopLoading() {
            cancelLoad();
        }

        @Override
        public void onCanceled(List<AppEntry> data) {
            super.onCanceled(data);

            if (data != null) {
                onReleaseResources(data);
            }
        }

        @Override
        protected void onReset() {
            super.onReset();

            onStopLoading();

            if (mAppList != null) {
                onReleaseResources(mAppList);
                mAppList = null;
            }

            if (mPackageChangeReceiver != null) {
                getContext().unregisterReceiver(mPackageChangeReceiver);
                mPackageChangeReceiver = null;
            }
        }

        protected void onReleaseResources(List<AppEntry> apps) {
        }
    }
}
