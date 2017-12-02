package com.paha.musicapp.tasks

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import com.paha.musicapp.interfaces.SongDataLoaded
import com.paha.musicapp.objects.SongInfo
import com.paha.musicapp.util.SongsUtil

class LoadSongsTask : AsyncTask<Any, String, List<SongInfo>>() {
    companion object {
        val listeners:MutableList<SongDataLoaded> = mutableListOf()
    }

    override fun doInBackground(vararg params: Any?): List<SongInfo> {
        val files = SongsUtil.loadAllSongs(params[0] as Activity)
        SongsUtil.compileSongsIntoCategories()
        SongsUtil.loadFavoriteSongs(params[0] as Context)
        return files
    }

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun onPostExecute(result: List<SongInfo>) {
        super.onPostExecute(result)
        listeners.forEach{it.onSongDataLoaded(SongsUtil.allSongs, SongsUtil.songsByArtistMap, SongsUtil.songsByAlbumMap)}
    }
}