package com.paha.musicapp

import android.app.Activity
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Handler
import android.view.View
import android.widget.SeekBar
import android.widget.TextView

class MusicInfoOnClickListener(private val context:Context, private val songData:FileInfo) : View.OnClickListener {

    override fun onClick(v: View) {
        println("Hey whats up")

        val fragmentManager = (context as Activity).fragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val format = MusicPlayerFragment.format
        val mediaPlayer = MusicPlayerFragment.mediaPlayer


        val fragment = MusicPlayerFragment(context, { fragmentView ->
            val songName = fragmentView.findViewById<TextView>(R.id.songPlayerSongName)
            val songTime = fragmentView.findViewById<TextView>(R.id.songPlayerSongTime)
            val songSeek = fragmentView.findViewById<SeekBar>(R.id.songSeekBar)
            songSeek.max = 100

            songName.text = v.findViewById<TextView>(R.id.songView).text
            songTime.text = v.findViewById<TextView>(R.id.timeView).text

            val handler = Handler()
            var runnable:()->Unit = {}

            runnable = {
                val currentTime = "${(mediaPlayer.currentPosition/60000)}:${format.format((mediaPlayer.currentPosition%60000)/1000)}"
                val maxTime = "${(mediaPlayer.duration/60000)}:${format.format((mediaPlayer.duration%60000)/1000)}"
                songTime.text = "$currentTime / $maxTime"
                songSeek.progress = ((mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration.toFloat())*100f).toInt()
                handler.postDelayed(runnable, 1000)
                Unit
            }

            handler.postDelayed(runnable, 1000)
        })

        fragment.playSong(context, songData)

        fragmentTransaction.add(R.id.song_list_layout, fragment)
        fragmentTransaction.commit()
    }
}