package com.paha.musicapp.listeners

import android.content.Context
import android.os.Handler
import android.support.v4.app.FragmentManager
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import com.paha.musicapp.objects.SongInfo
import com.paha.musicapp.R
import com.paha.musicapp.activities.SongDirectoryActivity
import com.paha.musicapp.fragments.MusicPlayerFragment
import com.paha.musicapp.fragments.AllSongsListTab

class MusicInfoOnClickListener(private val fragmentManager:FragmentManager, private val context:Context, private val songData: SongInfo) : View.OnClickListener {

    override fun onClick(v: View) {
        println("Hey whats up")

        val fragmentTransaction = fragmentManager.beginTransaction()

        val format = MusicPlayerFragment.format
        val mediaPlayer = MusicPlayerFragment.mediaPlayer

        val fragment = SongDirectoryActivity.musicPlayerFragment ?: MusicPlayerFragment(context, { fragmentView ->
            val songName = fragmentView.findViewById<TextView>(R.id.songPlayerSongName)
            val songTime = fragmentView.findViewById<TextView>(R.id.songPlayerSongTime)
            val songSeek = fragmentView.findViewById<SeekBar>(R.id.songSeekBar)
            songSeek.max = 100

            songName.text = v.findViewById<TextView>(R.id.songView).text
            songTime.text = v.findViewById<TextView>(R.id.timeView).text

            val handler = Handler()
            var runnable: () -> Unit = {}

            runnable = {
                val currentTime = "${(mediaPlayer.currentPosition / 60000)}:${format.format((mediaPlayer.currentPosition % 60000) / 1000)}"
                val maxTime = "${(mediaPlayer.duration / 60000)}:${format.format((mediaPlayer.duration % 60000) / 1000)}"
                songTime.text = "$currentTime / $maxTime"
                songSeek.progress = ((mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration.toFloat()) * 100f).toInt()
                handler.postDelayed(runnable, 500)
                Unit
            }

            handler.postDelayed(runnable, 500)
        })

        fragment.playSong(context, songData)

        fragmentTransaction.replace(R.id.music_player_container, fragment)
        fragmentTransaction.commit()
    }
}