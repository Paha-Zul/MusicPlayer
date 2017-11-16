package com.paha.musicapp.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ListView
import android.widget.SeekBar
import android.widget.TextView
import com.paha.musicapp.R
import com.paha.musicapp.SongListAdapter
import com.paha.musicapp.SongsUtil
import com.paha.musicapp.fragments.MusicPlayerFragment

class SongListActivity : AppCompatActivity() {



    private lateinit var listView:ListView
    private lateinit var arrayAdapter: SongListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_list)

        listView = findViewById(R.id.listView) as ListView

        val files = SongsUtil.getAllSongs()

//        arrayAdapter = SongListAdapter(this,  files.toTypedArray())
        listView.adapter = arrayAdapter
    }

}
