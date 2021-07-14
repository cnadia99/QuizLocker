 package com.cnadia.quizlocker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.MultiSelectListPreference
import android.preference.PreferenceFragment

 class MainActivity : AppCompatActivity() {
    val fragment = MyPreferenceFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fragmentManager.beginTransaction().replace(R.id.preferenceContent, fragment).commit()
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
        }
    }
}