package com.mzw.flutter_demo_notification_bar;
import android.os.Bundle;
import io.flutter.app.FlutterActivity;
import io.flutter.plugins.GeneratedPluginRegistrant;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.app.PendingIntent;
//import android.support.v7.app.NotificationCompat;
import androidx.core.app.NotificationCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.Integer;
import android.widget.RemoteViews;
import android.view.View;
import android.support.v4.media.session.MediaSessionCompat;

public class MainActivity extends FlutterActivity{
  TextView title;
 // MediaSessionCompat mediaSession;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GeneratedPluginRegistrant.registerWith(this);

    new MethodChannel(getFlutterView(), "notification_bar.flutter.io/notificationBar").setMethodCallHandler(
            new MethodCallHandler() {
              @Override
              public void onMethodCall(MethodCall call, Result result) {

                if (call.method.equals("content")) {
                  String contentTitle = call.argument("contentTitle");
                  String contentText = call.argument("contentText");
                    String dataTitle = call.argument("dataTitle");
                    String dataContent = call.argument("dataContent");
                //  title.setText(contentTitle);

                  sendChatMsg(contentTitle,contentText,dataTitle,dataContent);
                  if (true) {
                    result.success("success");
                  } else {
                    result.error("error", "failure", null);
                  }
                } else {
                  result.notImplemented();
                }
              }
            }
    );
    initNotificationManager();
  }

    private void removeFirebaseOrigianlNotificaitons() {

        //check notificationManager is available
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null )
            return;

        //check api level for getActiveNotifications()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //if your Build version is less than android 6.0
            //we can remove all notifications instead.
            //notificationManager.cancelAll();
            return;
        }

        //check there are notifications
        StatusBarNotification[] activeNotifications =
                notificationManager.getActiveNotifications();
        if (activeNotifications == null)
            return;

        //remove all notification created by library(super.handleIntent(intent))
        for (StatusBarNotification tmp : activeNotifications) {
            Log.d("FCM StatusBarNotification",
                    "tag/id: " + tmp.getTag() + " / " + tmp.getId());
            String tag = tmp.getTag();
            int id = tmp.getId();

            //trace the library source code, follow the rule to remove it.
            if (tag != null && tag.contains("FCM-Notification"))
                notificationManager.cancel(tag, id);
        }
    }


  private void initNotificationManager(){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      String channelId = "chat";
      String channelName = "notification";
      int importance = NotificationManager.IMPORTANCE_HIGH;
      createNotificationChannel(channelId, channelName, importance);
    }
  }

  @TargetApi(Build.VERSION_CODES.O)
  private void createNotificationChannel(String channelId, String channelName, int importance) {
    NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
    channel.setShowBadge(true);
    channel.setBypassDnd(true);
    channel.canBypassDnd();
    channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);


    channel.enableVibration(true);



    channel.enableLights(true);


    NotificationManager notificationManager = (NotificationManager) getSystemService( NOTIFICATION_SERVICE);
    notificationManager.createNotificationChannel(channel);
  }
  public void sendChatMsg(String contentTitle,String contentText,String dataTitle,String dataContent ) {
    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_view);
      RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.expanded_notification);

      expandedView.setTextViewText(R.id.content,contentTitle);
      expandedView.setTextViewText(R.id.content1,contentText);
      expandedView.setTextViewText(R.id.content2,dataTitle);
      expandedView.setTextViewText(R.id.content3,dataContent);

      notificationLayout.setTextViewText(R.id.content,contentTitle);
      notificationLayout.setTextViewText(R.id.content1,contentText);
   // mediaSession = new MediaSessionCompat(this, "tag");
//    Bitmap bitmap = new BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
//    String message="Hello Notification with image";

//    Bitmap picture = BitmapFactory.decodeResource(getResources(), R.drawable.nature);


      Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
      PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

      removeFirebaseOrigianlNotificaitons();

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel = manager.getNotificationChannel("chat");
      if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
        Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
        startActivity(intent);
       // Toast.makeText(this, "Notification", Toast.LENGTH_SHORT).show();
      }
    }

    Notification notification = new NotificationCompat.Builder(this, "chat")

//            .setContentTitle(contentTitle)
//            .setContentText(contentText)
//            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.mipmap.ic_launcher)
//            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.nature))
//            .setStyle(new NotificationCompat.MediaStyle())
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(expandedView)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .setColor(0xFFff0000)
            .setColorized(true)
            .setNumber(19)
            .build();
//            .setStyle(new NotificationCompat.BigPictureStyle()
//                    .bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.nature))
//                    .bigLargeIcon(null))
//            .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
            /*.setShowActionsInCompactView(1)*/
                    /*.setMediaSession(mediaSession.getSessionToken())*/
//            .addAction(R.drawable.nature, "Dislike", null)

//            .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
//                    .setShowActionsInCompactView(1, 2, 3)
//                    .setMediaSession(mediaSession.getSessionToken()))
//            .setSubText("Sub Text")
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//            .setStyle(new NotificationCompat.BigPictureStyle()
//                    .bigPicture(bitmap).setSummaryText(message))
//            .setStyle(new androidx.media.app.NotificationCompat.DecoratedMediaCustomViewStyle())
//            .setContentIntent(pendingIntent)


//    Notification n = NotificationExtras.buildWithBackgroundColor(this, notification, 0xffff0000);
//    manager.from(this).notify(1, n);
    manager.notify(1, notification);
  }
}