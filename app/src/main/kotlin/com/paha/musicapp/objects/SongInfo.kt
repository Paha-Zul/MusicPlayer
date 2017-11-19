package com.paha.musicapp.objects

import java.io.File

data class SongInfo(val file: File, val fileName:String) {
    var artisName = ""
    var albumName = ""

    override fun toString(): String {
        return fileName
    }
}