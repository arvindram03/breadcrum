package helpers.notification;

import com.example.android.geofence.R;

import models.MessageLog;
import activities.MainActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class NotificationHelper {

	public static void sendMessageNotification(Context context, MessageLog messageLog){
		Intent notificationIntent =
                new Intent(context,MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_action_email)
               .setContentTitle(
                       context.getString(R.string.notification_message_title,
                               messageLog.getReceiverName()))
               .setContentText(messageLog.getContent())
               .setContentIntent(notificationPendingIntent)
               .setAutoCancel(true);
        NotificationManager mNotificationManager =
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());
	}
	
	public static void sendLocationNotification(Context context, String transitionType, String ids) {

        Intent notificationIntent =
                new Intent(context,MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_notification)
               .setContentTitle(
                       context.getString(R.string.geofence_transition_notification_title,
                               transitionType, ids))
               .setContentText(context.getString(R.string.geofence_transition_notification_text))
               .setContentIntent(notificationPendingIntent)
               .setAutoCancel(true);
        NotificationManager mNotificationManager =
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());
    }
}
