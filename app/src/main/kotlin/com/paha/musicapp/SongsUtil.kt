package com.paha.musicapp

import java.io.File

object SongsUtil {
    lateinit var shuffledSongs:List<FileInfo>

    fun getAllSongs():List<FileInfo>{
        val files = findFiles(File(getStorage()))
        shuffledSongs = files.shuffle()
        return files
    }

    fun getNextSong(currSong:String):FileInfo{
        var index = shuffledSongs.indexOfLast { it.fileName == currSong }
        index = (index+1) % shuffledSongs.size
        return shuffledSongs[index]
    }

    fun getPreviousSong(currSong:String):FileInfo{
        var index = shuffledSongs.indexOfLast { it.fileName == currSong }
        index = (index-1) % shuffledSongs.size
        return shuffledSongs[index]
    }

    private fun findFiles(dir: File):List<FileInfo>{
        val children = dir.listFiles()

        val mutableList:MutableList<FileInfo> = mutableListOf()
        for (child in children) {
            if(child.isDirectory){
                mutableList.addAll(findFiles(child))
            } else if(child.extension == "mp3")
                mutableList += FileInfo(child, child.nameWithoutExtension)
        }

        return mutableList.toList()
    }

    private fun getStorage():String = when{
        File("/storage/extSdCard/").exists() -> "/storage/extSdCard/"
        File("/storage/sdcard1/").exists() -> "/storage/sdcard1/"
        File("/storage/usbcard1/").exists() -> "/storage/usbcard1/"
        File("/storage/sdcard0/").exists() -> "/storage/sdcard0/"
        else -> ""
    }
}