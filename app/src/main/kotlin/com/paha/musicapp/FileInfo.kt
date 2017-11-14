package com.paha.musicapp

import java.io.File

data class FileInfo(val file: File, val fileName:String) {
    override fun toString(): String {
        return fileName
    }
}