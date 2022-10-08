package alarms;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.mbro.wguapp.R;

public class AssessmentAlarmReceiver extends BroadcastReceiver {
    public static final String ALARM_NOTIFICATION_TITLE =
            "com.mbro.wguapp.alarms.ALARM_NOTIFICATION_TITLE";
    public static final String ALARM_NOTIFICATION_TEXT =
            "com.mbro.wguapp.alarms.ALARM_NOTIFICATION_TEXT";
    public static final String ALARM_NOTIFICATION_ASSESSMENT_TYPE =
            "com.mbro.wguapp.alarms.ALARM_NOTIFICATION_ASSESSMENT_TYPE";
    public static final String ALARM_NOTIFICATION_START=
            "com.mbro.wguapp.alarms.ALARM_NOTIFICATION_START";

    public static final String ASSESSMENT_CHANNEL_ID_ALARMS = "CHANNEL_ID_COURSE_ALARMS";
    public static final int NOTIFY_ID_ASSESSMENT_ALARM = 1;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel (Context context, String CHANNEL_ID){
        CharSequence name = "notification channel";
        String description = "notification channel description";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context.getApplicationContext());

        String title = intent.getStringExtra(ALARM_NOTIFICATION_TITLE);
        String type = intent.getStringExtra(ALARM_NOTIFICATION_ASSESSMENT_TYPE);
        String goalDate = intent.getStringExtra(ALARM_NOTIFICATION_TEXT);
        String startDate = intent.getStringExtra(ALARM_NOTIFICATION_START);

        createNotificationChannel(context, ASSESSMENT_CHANNEL_ID_ALARMS);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), ASSESSMENT_CHANNEL_ID_ALARMS)
                .setSmallIcon(R.drawable.ic_add_white_24dp)
                .setContentTitle(intent.getStringExtra(ALARM_NOTIFICATION_TITLE))
                .setContentText(title + " " + type + " assessment is almost here on " + goalDate)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);



        notificationManager.notify(NOTIFY_ID_ASSESSMENT_ALARM, builder.build());
    }
}
