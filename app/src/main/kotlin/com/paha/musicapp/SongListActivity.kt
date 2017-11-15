package com.paha.musicapp

import android.media.MediaPlayer
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.SeekBar
import android.widget.TextView
import java.io.File

class SongListActivity : AppCompatActivity() {

    private lateinit var listView:ListView
    private lateinit var arrayAdapter:SongListAdapter

    private val songClickListener:View.OnClickListener = View.OnClickListener { view ->
        val fragmentManager = fragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val fragment = MusicPlayerFragment(this, { fragmentView ->
            val songName = fragmentView.findViewById<TextView>(R.id.songPlayerSongName)
            val songTime = fragmentView.findViewById<TextView>(R.id.songPlayerSongTime)
            val songSeek = fragmentView.findViewById<SeekBar>(R.id.songSeekBar)

            songName.text = view.findViewById<TextView>(R.id.songView).text
            songTime.text = view.findViewById<TextView>(R.id.timeView).text
        })

        fragmentTransaction.add(R.id.song_list_layout, fragment)
        fragmentTransaction.commit()

//        val myUri = Uri.parse(view.) // initialize Uri here
//        val mediaPlayer = MediaPlayer();
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mediaPlayer.setDataSource(getApplicationContext(), myUri);
//        mediaPlayer.prepare();
//        mediaPlayer.start();
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_list)

        listView = findViewById(R.id.listView) as ListView

        val files = SongsUtil.getAllSongs()

        arrayAdapter = SongListAdapter(this, files.toTypedArray(), songClickListener)
        listView.adapter = arrayAdapter
    }

}
