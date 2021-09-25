package com.aybarsacar.cookpad.model.notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

/**
 * Worker class responsible to run Notification periodic task when the app is not running
 */
class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

  override fun doWork(): Result {

    println("inside doWork")

    return Result.success()
  }

}