package mobi.roshka.farmapy.util;

import mobi.roshka.farmapy.FarmaPYApplication;
import mobi.roshka.farmapy.R;
import mobi.roshka.farmapy.activity.SanitaryActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

public class FarmaPYNotificationManager {
	
	public static final String TAG = "NotificationManager";

	public static enum NotificationType {
		ALERT_NEW_INFO,
		ALERT_CRITICAL
	}
	
	private void doSendNotiticationNow(Context context, Intent notificationIntent, String message, int icon)
	{
        long when = System.currentTimeMillis();
        
        Notification notification = new Notification(icon, message, when);
        notification.setLatestEventInfo(context, "FarmaPY", message,
                        PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT));
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        SharedPreferences settings = context.getSharedPreferences("userdetails", Context.MODE_PRIVATE);
        int notificatonID = settings.getInt("notificationID", 0);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(notificatonID, notification);

        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("notificationID", ++notificatonID % 32);
        editor.commit();
        
        /*
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setContentTitle("FarmaPY")
                            .setContentText(message).setSmallIcon(icon).setWhen(when)
                            .setContentIntent(PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT))
                            .setAutoCancel(true);
            
            SharedPreferences settings = context.getSharedPreferences("userdetails", Context.MODE_PRIVATE);
            int notificatonID = settings.getInt("notificationID", 0);
            
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(notificatonID, builder.getNotification());
            
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("notificationID", ++notificatonID % 32);
            editor.commit();
         */

	}
	
	public void handleMessage(Intent intent)
	{
		Context context = FarmaPYApplication.getContext(); 
		NotificationType notificationType = NotificationType.valueOf(intent.getStringExtra("type"));
		Intent notificationIntent = new Intent(context, SanitaryActivity.class);
		String message = intent.getStringExtra("message");
		if (notificationType == NotificationType.ALERT_NEW_INFO) {
            int icon = R.drawable.farma_py_alert_info;
            doSendNotiticationNow(context, notificationIntent, message, icon);
		} else if (notificationType == NotificationType.ALERT_CRITICAL) {
            int icon = R.drawable.farma_py_alert_critical;
            doSendNotiticationNow(context, notificationIntent, message, icon);
            MediaPlayer mPlayer = MediaPlayer.create(context, R.raw.not);
            mPlayer.start();

		}
	}
	
}
