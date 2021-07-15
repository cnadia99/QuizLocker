 package com.cnadia.quizlocker

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.MultiSelectListPreference
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

 class MainActivity : AppCompatActivity() {
    val fragment = MyPreferenceFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fragmentManager.beginTransaction().replace(R.id.preferenceContent, fragment).commit()

        initButton.setOnClickListener {
            val answerStatistics = getSharedPreferences("answerStatistics", Context.MODE_PRIVATE)
            answerStatistics.edit().clear().apply()
            Toast.makeText(this, "초기화 되었습니다", Toast.LENGTH_SHORT).show()
        }
    }

    class MyPreferenceFragment: PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            addPreferencesFromResource(R.xml.pref)

            val categoryPref = findPreference("category") as MultiSelectListPreference
            categoryPref.summary = categoryPref.values.joinToString(", ")

            categoryPref.setOnPreferenceChangeListener { preference, newValue ->
                val newValueSet = newValue as? HashSet<*> ?: return@setOnPreferenceChangeListener true

                categoryPref.summary = newValue.joinToString(", ")

                true
            }

            val useLockScreenPref = findPreference("useLockScreen") as SwitchPreference
            useLockScreenPref.setOnPreferenceClickListener {
                when {
                    useLockScreenPref.isChecked -> {
                        runService()
                    }
                    else -> {
                        activity.stopService(Intent(activity, LockScreenService::class.java))
                    }
                }
                true
            }

            if (useLockScreenPref.isChecked) {
                runService()
            }
        }

        private fun runService() {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                activity.startForegroundService(Intent(activity, LockScreenService::class.java))
            } else {
                activity.startService(Intent(activity, LockScreenService::class.java))
            }
        }
    }
}