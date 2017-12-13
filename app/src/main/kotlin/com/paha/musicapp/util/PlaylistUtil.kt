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

    fun getNextSongInPlaylist(songFile:SongInfo, playlistName:String, shuffled:Boolean):SongInfo{
        if(currPlaylist.name != playlistName || currPlaylist.shuffled != shuffled){
            startPlaylist(playlistName, shuffled)
            currPlaylist.currSongIndex = currPlaylist.playlist.indexOfFirst { it.songName == songFile.songName }
        }

        var nextIndex = ++currPlaylist.currSongIndex
        nextIndex = Util.mod(nextIndex, currPlaylist.playlist.size)
        return currPlaylist.playlist[nextIndex]
    }

    fun getPreviousSongInPlaylist(songFile:SongInfo, playlistName:String, shuffled:Boolean):SongInfo{
        if(songFile.currTime > 10000) //If we're past 10 seconds of play time, simply restart the song
            return songFile

        if(currPlaylist.name != playlistName || currPlaylist.shuffled != shuffled){
            startPlaylist(playlistName, shuffled)
            currPlaylist.currSongIndex = currPlaylist.playlist.indexOfFirst { it.songName == songFile.songName }
        }

        var nextIndex = --currPlaylist.currSongIndex
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