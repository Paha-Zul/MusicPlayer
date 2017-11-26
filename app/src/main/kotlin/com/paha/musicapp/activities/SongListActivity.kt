package com.paha.musicapp.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ListView
import com.paha.musicapp.R
import com.paha.musicapp.adapaters.SongListAdapter
import com.paha.musicapp.util.SongsUtil

class SongListActivity : AppCompatActivity() {

    private lateinit var listView:ListView
    private lateinit var arrayAdapter: SongListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_list)

        listView = findViewById(R.id.all_songs_list_view) as ListView

        val files = SongsUtil.loadAllSongs()

//        arrayAdapter = SongListAdapter(this,  files.toTypedArray())
        listView.adapter = arrayAdapter
    }

}
