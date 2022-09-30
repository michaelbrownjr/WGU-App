package alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.mbro.wguapp.R;


public class CourseAlarmReceiver extends BroadcastReceiver {
    public static final String ALARM_NOTIFICATION_TITLE;

    static {
        ALARM_NOTIFICATION_TITLE = "Alarm Title";
    }

    public static final String ALARM_NOTIFICATION_TEXT;

    static {
        ALARM_NOTIFICATION_TEXT = "Alarm Text";
    }

    public static final String COURSE_CHANNEL_ID_ALARMS = "CHANNEL_ID_COURSE_ALARMS";
    public static final int NOTIFY_ID_COURSE_ALARM = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context.getApplicationContext());

        String title = intent.getStringExtra(ALARM_NOTIFICATION_TITLE);
        String text = intent.getStringExtra(ALARM_NOTIFICATION_TEXT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), COURSE_CHANNEL_ID_ALARMS)
                .setSmallIcon(R.drawable.ic_add_white_24dp)
                .setContentTitle(title)
                .setContentText(title + " " + text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(NOTIFY_ID_COURSE_ALARM, builder.build());
    }
}
