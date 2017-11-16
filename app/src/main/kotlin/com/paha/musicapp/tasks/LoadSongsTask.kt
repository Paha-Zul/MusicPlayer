package com.paha.musicapp.tasks

import android.content.Context
import android.os.AsyncTask
import android.support.v4.app.FragmentManager
import android.view.View
import android.widget.ListView
import com.paha.musicapp.FileInfo
import com.paha.musicapp.R
import com.paha.musicapp.SongListAdapter
import com.paha.musicapp.SongsUtil

class LoadSongsTask(private val activity: Context, private val fragmentManager: FragmentManager, private val v:View) : AsyncTask<Any, String, List<FileInfo>>() {
    lateinit var arrayAdapter:SongListAdapter
    lateinit var listView:ListView
    lateinit var files:List<FileInfo>

    override fun doInBackground(vararg params: Any?): List<FileInfo> {
        files = SongsUtil.getAllSongs()
        return files
    }

    override fun onPreExecute() {
        super.onPreExecute()
        listView = v.findViewById(R.id.listView)
    }

    override fun onPostExecute(result: List<FileInfo>?) {
        super.onPostExecute(result)
        arrayAdapter = SongListAdapter(activity, fragmentManager, files.toTypedArray())
            listView.adapter = arrayAdapter
    }


}