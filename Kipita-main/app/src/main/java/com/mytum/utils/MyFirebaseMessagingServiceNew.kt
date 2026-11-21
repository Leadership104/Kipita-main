package com.mytum.utils

import android.annotation.SuppressLint
import android.app.*
import android.content.ComponentName
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mytum.MyApplication
import com.mytum.R
import java.util.*

@Suppress("DEPRECATION")
class MyFirebaseMessagingServiceNew : FirebaseMessagingService() {
    private fun isForeground(myPackage: String): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningTaskInfo = manager.getRunningTasks(1)
        val componentInfo = runningTaskInfo[0].topActivity
        return componentInfo!!.packageName == myPackage
    }

    @SuppressLint("LongLogTag")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.i("notification:", remoteMessage.data.toString())
        wakeApp()
        val extras = Bundle()
        // Check if message contains a data payload.
        val dataPayLoad = remoteMessage.data
        for ((key, value) in dataPayLoad) {
            // Code to be executed for each key-value pair
            println("Key: $key, Value: $value")
            extras.putString(key, value)
        }
        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            // Check if the message contains a notification payload.
            // Launch the fullscreen activity here.
            val body = remoteMessage.notification!!.body
            val title = remoteMessage.notification!!.title
            Log.i("Message Notification Body: ", body!!)
            extras.putString("payloadBody", body)
            extras.putString("title", title)
        }
        val startAppIntent = Intent()
        val ctx = applicationContext
        val mPackage = ctx.packageName
        val mClass = ctx.resources.getString(R.string.dash)
        // R.string.main_activity_name will be overridden by the module using this library
        startAppIntent.component = ComponentName(mPackage, mClass)
        val activityClass: Class<*> = try {
            Class.forName("$mPackage.$mClass")
        } catch (e: ClassNotFoundException) {
            Log.e("activityClass", "activityClass is null")
            e.printStackTrace()
            return
        }
        if (isForeground(mPackage)) {
//            scheduleNotification(false);
            sendBroadcast(extras)
            showNotification(remoteMessage, dataPayLoad, activityClass, extras, "")
        } else {
            wakeApp()
//            scheduleNotification(true)
//            if (extras.getString("openApp", "").equals("true")) {
            showNotification(remoteMessage, dataPayLoad, activityClass, extras, "1")
            startActivity(activityClass, extras)
//            } else {
//                Log.i("openApp", extras.getString("openApp", ""))
//            }
        }
    }

    // [END receive_message]
    private fun wakeApp() {
        val pm = applicationContext.getSystemService(POWER_SERVICE) as PowerManager
        val screenIsOn = pm.isInteractive // check if screen is on
        val s = screenIsOn.toString()
        Log.i("wakeApp", s)
        if (!screenIsOn) {
            val wakeLockTag = packageName + "WAKELOCK"
            val wakeLock = pm.newWakeLock(
                PowerManager.FULL_WAKE_LOCK or
                        PowerManager.ACQUIRE_CAUSES_WAKEUP or
                        PowerManager.ON_AFTER_RELEASE, wakeLockTag
            )
            //acquire will turn on the display
            wakeLock.acquire(3 * 60 * 1000L /*10 minutes*/)
            //release will release the lock from CPU, in case of that, screen will go back to sleep mode in defined time bt device settings
            wakeLock.release()
        }
    }

    private fun sendBroadcast(extras: Bundle) {
        Log.i("sendBroadcast", "Sending broadcast after received a message")
        val intent = Intent("onNewEcosDoc")
        intent.putExtras(extras)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    private fun startActivity(activityClass: Class<*>, extras: Bundle) {
        Log.i("Starting activity...", activityClass.toString())
        val myIntent = Intent(this, activityClass)
        myIntent.flags = (
                Intent.FLAG_ACTIVITY_NEW_TASK
                        or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        myIntent.putExtras(extras)
        this.applicationContext.startActivity(myIntent)
    }

    @SuppressLint("UnspecifiedImmutableFlag", "RemoteViewLayout")
    private fun showNotification(
        remoteMessage: RemoteMessage,
        dataPayLoad: Map<String, String>,
        activityClass: Class<*>,
        extras: Bundle,
        type: String
    ) {
        val channelID = getString(R.string.default_notification_channel_id)
//        val channelID =  extras.getString("organizationId", "")
        createNotificationChannel(channelID)
        val sound =
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + MyApplication.getInstance().packageName + "/" + R.raw.alarm)
        // Check if message contains a notification payload.
        val title: String = if (type == "") {
            dataPayLoad["bodyText"]!!
        } else {
            extras.getString("title", "")
        }

        val body = remoteMessage.notification?.body
        val notificationIntent = Intent(applicationContext, activityClass)
        val resultIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                applicationContext,
                0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(
                applicationContext,
                0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        val drawable = ContextCompat.getDrawable(applicationContext, R.drawable.noti_icon)
        val bitmap = Objects.requireNonNull(drawable).let {
            it?.let { it1 ->
                Bitmap.createBitmap(
                    it1.intrinsicWidth,
                    drawable!!.intrinsicHeight, Bitmap.Config.ARGB_8888
                )
            }
        }
        val bigTextStyle = NotificationCompat.BigTextStyle()
        bigTextStyle.setBigContentTitle(getString(R.string.app_name))
        bigTextStyle.bigText(getString(R.string.english))
        if (type == "1") {
            val mBuilder = NotificationCompat.Builder(
                applicationContext, channelID
            )
                .setSmallIcon(R.drawable.noti_icon)
                .setContentTitle(title)
                .setContentText(body)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        resources,
                        R.drawable.noti_icon
                    )
                )
                .setSound(sound)
                .setAutoCancel(true)
                .setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                )
                .setColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
//                .setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .addAction(
                    androidx.appcompat.R.drawable.abc_ic_arrow_drop_right_black_24dp,
                    "Turn OFF",
                    resultIntent
                )
//            .setContentIntent(resultIntent)
//            .setFullScreenIntent(resultIntent, true)
            val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.notify(0, mBuilder.build())
        } else {
            val mBuilder = NotificationCompat.Builder(
                applicationContext, channelID
            ).setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.noti_icon
                )
            )
                .setSmallIcon(R.drawable.noti_icon)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(sound)
                .setAutoCancel(true)
//                .setStyle(
//                    NotificationCompat.BigPictureStyle()
//                        .bigPicture(bitmap)
//                        .bigLargeIcon(null)
//                )
                .setColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .addAction(
                    androidx.appcompat.R.drawable.abc_ic_arrow_drop_right_black_24dp,
                    "Remove And Refresh",
                    null
                )
            val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.notify(0, mBuilder.build())
        }

    }

    private fun createNotificationChannel(channelID: String) {
        // Create the NotificationChannel, but only on API 26+ because
        val sound =
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + MyApplication.getInstance().packageName + "/" + R.raw.alarm)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Foreground Service Channel"
            val description = "channel description"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelID, name, importance)
            channel.description = description
            channel.lightColor = Color.RED
            channel.enableLights(true)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
            channel.setSound(sound, audioAttributes)
            channel.vibrationPattern = longArrayOf(500, 500, 500)
            channel.lockscreenVisibility
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        sendCurrentToken(applicationContext)
    }

    // [END on_new_token]
    /*
     * Should be called after login (ce 1030 edge),\
     *  will send the current FCM token to backend
     */
    private fun sendCurrentToken(context: Context?) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("onComplete", "Fetching FCM registration token failed")
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            // Log and toast
            val intent = Intent("FCMOnTokenReceive")
            intent.putExtra("FCMToken", token)
            LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("onDestroy:", " Service is destroyed")
    }
}