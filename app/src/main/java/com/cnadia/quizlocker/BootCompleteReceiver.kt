package com.cnadia.quizlocker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log

class BootCompleteReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when {
            intent?.action == Intent.ACTION_BOOT_COMPLETED -> {
                Log.d("QuizLocker", "부팅이 완료됨")

                context?.let {
                    runService(it)
                }
            }
        }
    }

    private fun runService(context: Context) {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val useLockScreen = pref.getBoolean("useLockScreen", false)
        if (useLockScreen) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                context.startForegroundService(Intent(context, LockScreenService::class.java))
            } else {
                context.startService(Intent(context, LockScreenService::class.java))
            }
        }
    }
}