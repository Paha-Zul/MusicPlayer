package com.paha.musicapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class DisplayMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_message)

        val intent = intent
        val message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE)

        val textView: TextView = findViewById(R.id.textView) as TextView
        textView.text = message

        println("Hi")

//        println("Files found: ${findFiles(File(getStorage()))}")
    }


}
