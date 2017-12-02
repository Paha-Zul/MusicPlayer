package com.paha.musicapp.objects

data class SongInfo(val filePath: String, val songName:String) {
    var artisName = ""
    var albumName = ""
    var duration = 0L

    override fun toString(): String {
        return songName
    }
}