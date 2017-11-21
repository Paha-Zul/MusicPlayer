package com.paha.musicapp.objects

data class SongInfo(val filePath: String, val songName:String) {
    var artisName = ""
    var albumName = ""

    override fun toString(): String {
        return songName
    }
}