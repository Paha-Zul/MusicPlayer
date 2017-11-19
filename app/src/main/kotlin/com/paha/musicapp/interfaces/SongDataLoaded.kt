package com.paha.musicapp.interfaces

import com.paha.musicapp.objects.SongInfo

interface SongDataLoaded {
    fun onSongDataLoaded(allSongs:List<SongInfo>, songsByArtist:HashMap<String, List<SongInfo>>, songsByAlbum:HashMap<String, List<SongInfo>>)
}