package com.paha.musicapp

import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import java.io.File

object SongsUtil {
    lateinit var shuffledSongs:List<FileInfo>
    lateinit var songsByArtistMap:HashMap<String, MutableList<FileInfo>>
    lateinit var songsByAlbumMap:HashMap<String, MutableList<FileInfo>>

    fun getAllSongs():List<FileInfo>{
        val files = findFiles(File(getStorage()))
        shuffledSongs = files.shuffle()
        return files
    }

    fun compileSongsByArtist(){
        val dr = MediaMetadataRetriever()
        songsByArtistMap = hashMapOf()
        songsByAlbumMap = hashMapOf()

        shuffledSongs.forEach { song ->
            dr.setDataSource(song.file.path)
            val artistName = dr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
            val albumName = dr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)

            songsByArtistMap.getOrPut(artistName, { mutableListOf() }).add(song)
            songsByAlbumMap.getOrPut(albumName, { mutableListOf() }).add(song)
        }
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