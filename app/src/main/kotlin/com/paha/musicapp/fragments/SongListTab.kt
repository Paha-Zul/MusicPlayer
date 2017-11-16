package com.paha.musicapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.paha.musicapp.R
import com.paha.musicapp.SongListAdapter
import com.paha.musicapp.SongsUtil
import com.paha.musicapp.tasks.LoadSongsTask

class SongListTab : Fragment() {
    companion object {
        var musicPlayerFragment: MusicPlayerFragment? = null
    }

    private lateinit var listView: ListView
    private lateinit var arrayAdapter: SongListAdapter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = View.inflate(context, R.layout.songs_tab_1, null)
        return view
    }

    override fun onStart() {
        super.onStart()

        LoadSongsTask(activity, fragmentManager, view!!).execute("")
//        listView = view!!.findViewById(R.id.listView)
//        val files = SongsUtil.getAllSongs()
//        arrayAdapter = SongListAdapter(activity, fragmentManager, files.toTypedArray())
//        listView.adapter = arrayAdapter
    }

    //    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return super.onCreateView(inflater, container, savedInstanceState)
//    }
}