/*
 * Copyright 2013 Dirk Vranckaert
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.vranckaert.worktime.utils.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import eu.vranckaert.worktime.activities.account.AccountSyncService;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.model.SyncHistory;
import eu.vranckaert.worktime.utils.context.ContextUtils;
import eu.vranckaert.worktime.utils.preferences.Preferences;

import java.util.Date;

/**
 * User: Dirk Vranckaert
 * Date: 16/01/13
 * Time: 13:10
 */
public class AlarmUtil {
    private static final String LOG_TAG = AlarmUtil.class.getSimpleName();

    private static AlarmManager getAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    private static PendingIntent getSyncOperation(Context context) {
        Intent intent = new Intent(context, AccountSyncService.class);
        PendingIntent operation = PendingIntent.getService(context, Constants.IntentRequestCodes.ALARM_SYNC_REPEAT, intent, 0);
        return operation;
    }

    /**
     * Remove all planned synchronization alarms.
     * @param context The context.
     */
    public static void removeAllSyncAlarms(Context context) {
        getAlarmManager(context).cancel(getSyncOperation(context));
        Log.i(LOG_TAG, "The alarm sync cycle has been removed");
    }

    /**
     * Schedule the alarms for the automated synchronization process. If the last sync history object is null the next
     * synchronization will happen in 5 minutes. Otherwise the synchronization will check if (according to the settings)
     * it needs to synchronize within five minutes or if the interval can be determined from the last end date of the
     * last synchronization.
     * @param context         The context.
     * @param lastSyncHistory The last sync history, if none null.
     * @param syncInterval    The synchronization interval in milliseconds.
     */
    public static void setAlarmSyncCycle(Context context, SyncHistory lastSyncHistory, long syncInterval) {
        removeAllSyncAlarms(context);

        long fiveMinutes = 5 * 60000;

        // Only use this for debugging purpose
//        if (!ContextUtils.isStableBuild(context)) {
//            syncInterval = 60000; // Every minute...
//            fiveMinutes = 30000; // 30 seconds...
//        }

        long nextSync = 1;
        if (lastSyncHistory == null) {
            nextSync = fiveMinutes;
        } else {
            long intervalFromLastSync = (new Date()).getTime() - lastSyncHistory.getStarted().getTime();
            if (intervalFromLastSync >= syncInterval) {
                nextSync = fiveMinutes;
            } else {
                nextSync = syncInterval - intervalFromLastSync;
            }
        }

        Log.i(LOG_TAG, "Alarm scheduled to go off in " + nextSync + " milliseconds and to be repeated in " + syncInterval + " milliseconds.");

        nextSync = (new Date().getTime()) + nextSync;

        getAlarmManager(context).setRepeating(AlarmManager.RTC_WAKEUP, nextSync, syncInterval, getSyncOperation(context));
    }
}