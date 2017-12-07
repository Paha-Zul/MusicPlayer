package com.paha.musicapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ProgressBar
import com.paha.musicapp.R
import com.paha.musicapp.adapaters.SongArtistAdapter
import com.paha.musicapp.interfaces.SongDataLoaded
import com.paha.musicapp.objects.SongInfo
import com.paha.musicapp.tasks.LoadSongsTask
import com.paha.musicapp.util.SongsUtil

class SongsByArtistTab : Fragment(), SongDataLoaded{
    init{
        LoadSongsTask.listeners += this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return View.inflate(context, R.layout.song_grouped_tab, null)
    }

    override fun onStart() {
        super.onStart()
        view!!.findViewById<ProgressBar>(R.id.progress_loader).visibility = View.VISIBLE
        onSongDataLoaded(SongsUtil.allSongs, SongsUtil.songsByArtistMap, SongsUtil.songsByAlbumMap)
    }

    override fun onSongDataLoaded(allSongs: List<SongInfo>, songsByArtist: HashMap<String, List<SongInfo>>, songsByAlbum: HashMap<String, List<SongInfo>>) {
        if(allSongs.isEmpty() || view == null)
            return

        val listView = view!!.findViewById<GridView>(R.id.grouped_songs_grid)
        val keyList = SongsUtil.songsByArtistMap.keys.toList()
        val list: MutableList<Pair<String, List<SongInfo>>> = mutableListOf()
        keyList.forEach { key ->
            list += Pair(key, SongsUtil.songsByArtistMap[key]!!)
        }
        val adapter = SongArtistAdapter(activity, fragmentManager, list)
        listView.adapter = adapter
        view!!.findViewById<ProgressBar>(R.id.progress_loader).visibility = View.GONE
    }
}