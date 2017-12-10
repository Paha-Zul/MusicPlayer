package com.paha.musicapp.util

import com.paha.musicapp.objects.SongInfo
import com.paha.musicapp.shuffle

object PlaylistUtil {

    private class PlayList{
        var name = ""
        var playlist:List<SongInfo> = listOf()
        var shuffled = false
        var initialized = false
        var currSongIndex = 0
    }

    private var currPlaylist = PlayList()

    fun getNextSongInPlaylist(songName:String, playlistName:String, shuffled:Boolean, previous:Boolean):SongInfo{
        if(currPlaylist.name != playlistName || currPlaylist.shuffled != shuffled){
            startPlaylist(playlistName, shuffled)
            currPlaylist.currSongIndex = currPlaylist.playlist.indexOfFirst { it.songName == songName }
        }
        //TODO Maybe handle going back to the start of the song here when going backwards? Within 5 seconds of the start perhaps?
        var nextIndex = if(previous) --currPlaylist.currSongIndex else ++currPlaylist.currSongIndex
        nextIndex = Util.mod(nextIndex, currPlaylist.playlist.size)
        return currPlaylist.playlist[nextIndex]
    }

    fun FindSongAndSetPlaylistIndex(songName:String, playlistName:String, shuffled:Boolean):Int{
        if(currPlaylist.name != playlistName || currPlaylist.shuffled != shuffled){
            startPlaylist(playlistName, shuffled)
            currPlaylist.currSongIndex = currPlaylist.playlist.indexOfFirst { it.songName == songName }
        }

        currPlaylist.currSongIndex = currPlaylist.playlist.indexOfFirst { it.songName == songName }
        return currPlaylist.currSongIndex
    }

    fun findSongInPlayList(songName:String, playlistName:String, shuffled:Boolean):Int{
        if(currPlaylist.name != playlistName || currPlaylist.shuffled != shuffled){
            startPlaylist(playlistName, shuffled)
            currPlaylist.currSongIndex = currPlaylist.playlist.indexOfFirst { it.songName == songName }
        }

        return currPlaylist.playlist.indexOfFirst { it.songName == songName }
    }

    /**
     * Starts a playlist and returns the first song in the playlist
     */
    private fun startPlaylist(playlistName:String, shuffled:Boolean){
        currPlaylist.name = playlistName
        currPlaylist.shuffled = shuffled
        currPlaylist.playlist = if(shuffled) SongsUtil.allSongs.toList().shuffle() else SongsUtil.allSongs.toList() //Copy the list

        currPlaylist.currSongIndex = 0
        currPlaylist.initialized = true
    }


}