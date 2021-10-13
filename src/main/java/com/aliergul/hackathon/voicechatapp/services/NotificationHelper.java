package com.aliergul.hackathon.voicechatapp.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;

import com.aliergul.hackathon.voicechatapp.R;
import com.aliergul.hackathon.voicechatapp.model.Post;


public class NotificationHelper extends ContextWrapper {
    private NotificationManager manager;
    public static final String PRIMARY_CHANNEL = "default";
    public static final String SECONDARY_CHANNEL = "second";

    /**
     * Daha sonra bireysel bildirimler tarafından kullanılabilecek bildirim kanallarını kaydeder.
     * @param base
     */

    public NotificationHelper(Context base) {
        super(base);
        NotificationChannel chan1 = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan1 = new NotificationChannel(PRIMARY_CHANNEL,
                    getString(R.string.noti_channel_default), NotificationManager.IMPORTANCE_DEFAULT);
            chan1.setLightColor(Color.GREEN);
            chan1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getManager().createNotificationChannel(chan1);

            NotificationChannel chan2 = new NotificationChannel(SECONDARY_CHANNEL,
                    getString(R.string.noti_channel_second), NotificationManager.IMPORTANCE_HIGH);
            chan2.setLightColor(Color.BLUE);
            chan2.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(chan2);
        }

    }

    /**
     *      Tip 1 bildirim alın
     *      Bildirim yerine oluşturucuyu, bildirim yapmak için yararlı olduğu kadar sağlayın
     *      değişir.
     * @param title
     * @param body
     * @return
     */

    public Notification.Builder getNotification1(String title, String body,int itemType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new Notification.Builder(getApplicationContext(), PRIMARY_CHANNEL)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(getSmallIcon(itemType))
                    .setAutoCancel(true);
        }
        return null;
    }
    public void notify(int id, Notification.Builder notification) {
        getManager().notify(id, notification.build());
    }
    private int getSmallIcon(int itemType) {
        if(itemType== Post.POST_TEXT)
        return R.drawable.ic_message;
        else return R.drawable.ic_music_note;
    }

    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }
}
