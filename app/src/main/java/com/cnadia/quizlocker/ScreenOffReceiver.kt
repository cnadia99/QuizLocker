package com.cnadia.quizlocker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ScreenOffReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        when {
            intent?.action == Intent.ACTION_SCREEN_OFF -> {
//                Log.d("QuizLocker", "퀴즈 잠금 : 화면이 꺼졌습니다.")
                val intent = Intent(context, QuizLockerActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                context?.startActivity(intent)
            }
        }
    }
}