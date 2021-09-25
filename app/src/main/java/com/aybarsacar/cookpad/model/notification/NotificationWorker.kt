package com.aybarsacar.cookpad.model.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.provider.SyncStateContract
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.aybarsacar.cookpad.R
import com.aybarsacar.cookpad.utils.Constants
import com.aybarsacar.cookpad.view.activities.MainActivity

/**
 * Worker class responsible to run Notification periodic task when the app is not running
 */
class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

  override fun doWork(): Result {

    sendNotification()

    return Result.success()
  }

  private fun sendNotification() {
    // must be unique for each type of notification we send
    val notificationId = 0

    // we will send the user to the main activity
    val intent = Intent(applicationContext, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

    intent.putExtra(Constants.NOTIFICATION_ID, notificationId)

    // inject the notification manager as the service
    val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val notificationTitle = applicationContext.getString(R.string.notification_title)
    val notificationSubtitle = applicationContext.getString(R.string.notification_subtitle)

    val bitmap = applicationContext.vectorToBitmap(R.drawable.ic_baseline_emoji_food_beverage_24)

    val bigPictureStyle = NotificationCompat.BigPictureStyle()
      .bigPicture(bitmap)
      .bigLargeIcon(null)

    val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

    val notification = NotificationCompat.Builder(applicationContext, Constants.NOTIFICATION_CHANNEL)
      .setContentTitle(notificationTitle)
      .setContentText(notificationSubtitle)
      .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
      .setLargeIcon(bitmap)
      .setDefaults(NotificationCompat.DEFAULT_ALL) // colour and style etc of the notification
      .setContentIntent(pendingIntent)
      .setStyle(bigPictureStyle)
      .setAutoCancel(true) // notification disappears when the user clicks on it

    notification.priority = NotificationCompat.PRIORITY_MAX

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      notification.setChannelId(Constants.NOTIFICATION_CHANNEL)

      // setup the ringtone for notification
      val ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

      val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

      val channel = NotificationChannel(
        Constants.NOTIFICATION_CHANNEL,
        Constants.NOTIFICATION_NAME,
        NotificationManager.IMPORTANCE_HIGH
      )

      channel.enableLights(true)
      channel.lightColor = Color.RED
      channel.enableVibration(true)
      channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
      channel.setSound(ringtone, audioAttributes)

      notificationManager.createNotificationChannel(channel)
    }

    // send the notification
    notificationManager.notify(notificationId, notification.build())
  }

  /**
   * or use BitmapFactory
   */
  private fun Context.vectorToBitmap(drawableId: Int): Bitmap? {

    val drawable = ContextCompat.getDrawable(this, drawableId) ?: return null

    val bitmap =
      Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888) ?: return null

    val canvas = Canvas(bitmap)

    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
  }
}