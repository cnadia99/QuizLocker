package com.cnadia.quizlocker

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_file_ex.*
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream

class FileExActivity : AppCompatActivity() {
    val filename = "data.txt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_ex)

        saveButton.setOnClickListener {
            val text = textField.text.toString()
            when {
                TextUtils.isEmpty(text) -> {
                    Toast.makeText(applicationContext, "텍스트가 비어 있습니다.", Toast.LENGTH_SHORT).show()
                }
                !isExternalStorageWritable() -> {
                    Toast.makeText(applicationContext, "외부 저장장치가 없습니다.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    //saveToInnerStorage(filename, text)
                    saveToExternalStroage(filename, text)
                }
            }
        }

        loadButton.setOnClickListener {
            try {
//                val text: String = loadFromInnerStorage(filename)
                val text: String = loadFromExernalStorage(filename)
                textField.setText(text)
            } catch(e: FileNotFoundException) {
                Toast.makeText(applicationContext, "저장된 텍스트가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadFromInnerStorage(filename: String): String {
        val fileInputString = openFileInput(filename)
        return fileInputString.reader().readText()
    }

    private fun saveToInnerStorage(filename: String, text: String) {
        val fileOutputStream = openFileOutput(filename, Context.MODE_PRIVATE)
        fileOutputStream.write(text.toByteArray())
        fileOutputStream.close()
    }

    fun isExternalStorageWritable(): Boolean {
        return when {
            Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED -> true
            else -> false
        }
    }

    fun getAppDataFileFromExternalStorage(filename: String): File {
        val dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        } else {
            File(Environment.getExternalStorageDirectory().absolutePath + "/Documents")
        }

        dir?.mkdir()

        return File("${dir?.absolutePath}${File.separator}${filename}")
    }

    fun saveToExternalStroage(filename: String, text: String) {
        val fileOutputStream = FileOutputStream(getAppDataFileFromExternalStorage(filename))
        fileOutputStream.write(text.toByteArray())
        fileOutputStream.close()
    }

    fun loadFromExernalStorage(filename: String): String {
        return FileInputStream(getAppDataFileFromExternalStorage(filename)).reader().readText()
    }
}