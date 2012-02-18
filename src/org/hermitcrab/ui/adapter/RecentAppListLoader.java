package org.hermitcrab.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;

public class RecentAppListLoader extends AppListLoader {

	final ActivityManager mAm;

	public RecentAppListLoader(Context context) {
		super(context);
		mAm = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
	}

	@Override
	public List<AppEntry> loadInBackground() {
		List<RecentTaskInfo> tasks = mAm.getRecentTasks(15,
				ActivityManager.RECENT_IGNORE_UNAVAILABLE);

		List<ApplicationInfo> apps = null;
		if (apps == null) {
			apps = new ArrayList<ApplicationInfo>();
		}

		if (tasks != null) {
			for (RecentTaskInfo recentTask : tasks) {
				try {
					ApplicationInfo applicationInfo = null;
					ComponentName origActivity = recentTask.origActivity;
					if (origActivity != null) {
						ActivityInfo activityInfo = mPm.getActivityInfo(
								origActivity, 0);
						applicationInfo = activityInfo == null ? null
								: activityInfo.applicationInfo;
					} else if (recentTask.baseIntent != null) {
						ResolveInfo resolveInfo = mPm.resolveActivity(
								recentTask.baseIntent,
								PackageManager.MATCH_DEFAULT_ONLY);
						if (resolveInfo.resolvePackageName != null) {
							applicationInfo = mPm.getApplicationInfo(
									resolveInfo.resolvePackageName, 0);
						} else if (resolveInfo.activityInfo != null) {
							applicationInfo = resolveInfo.activityInfo.applicationInfo;
						}
					}
					if (applicationInfo != null) {
						apps.add(applicationInfo);
					}
				} catch (NameNotFoundException e) {
				}
			}
		}

		final Context context = getContext();

		// Create corresponding array of entries and load their labels.
		List<AppEntry> entries = new ArrayList<AppEntry>(apps.size());
		for (int i = 0; i < apps.size(); i++) {
			ApplicationInfo info = apps.get(i);
			AppEntry entry = new AppEntry(info);
			entry.loadLabel(context);
			entries.add(entry);
		}

		// Sort the list.
		// Collections.sort(entries, AppEntry.ALPHA_COMPARATOR);

		// Done!
		return entries;
	}
}
