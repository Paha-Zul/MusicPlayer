package com.paha.musicapp.util

import android.content.Context
import android.media.MediaMetadataRetriever
import com.paha.musicapp.objects.SongInfo
import com.paha.musicapp.shuffle
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File


object SongsUtil {
    var shuffledSongs:List<SongInfo> = listOf()
    var songsByArtistMap:HashMap<String, List<SongInfo>> = hashMapOf()
    var songsByAlbumMap:HashMap<String, List<SongInfo>> = hashMapOf()

    fun getAllSongs():List<SongInfo>{
        val files = findFiles(File(getStorage()))
        shuffledSongs = files.shuffle()
        return files
    }

    fun compileSongsIntoCategories(){
        val dr = MediaMetadataRetriever()

        val artistMap = hashMapOf<String, MutableList<SongInfo>>()
        val albumMap = hashMapOf<String, MutableList<SongInfo>>()

        shuffledSongs.forEach { song ->
            val audio = AudioFileIO.read(song.file)
            val tag = audio.tag

            dr.setDataSource(song.file.path, hashMapOf())
            val artistName = dr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "unknown"
            val albumName = dr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: "unknown"
            val albumArtist = dr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST) ?: "unknown"
//
//            val artistName = tag.getFirst(FieldKey.ARTIST) ?: "unknown"
//            val albumName = tag.getFirst(FieldKey.ALBUM) ?: "unknown"
//            val albumArtist = tag.getFirst(FieldKey.ALBUM_ARTIST) ?: "unknown"

            song.artisName = artistName
            song.albumName = albumName

//            println("song: ${song.fileName}, artist: $artistName, albumName: $albumName, albumArtist: $albumArtist")

            artistMap.getOrPut(artistName, { mutableListOf() }).add(song)
            albumMap.getOrPut(albumName, { mutableListOf() }).add(song)
        }

        dr.release()

        for (entry in artistMap) {
            songsByArtistMap.put(entry.key, entry.value.toList())
        }

        for (entry in albumMap) {
            songsByAlbumMap.put(entry.key, entry.value.toList())
        }

    }

    fun getNextSong(currSong:String): SongInfo {
        var index = shuffledSongs.indexOfLast { it.fileName == currSong }
        index = (index+1) % shuffledSongs.size
        return shuffledSongs[index]
    }

    fun getPreviousSong(currSong:String): SongInfo {
        var index = shuffledSongs.indexOfLast { it.fileName == currSong }
        index = (index-1) % shuffledSongs.size
        return shuffledSongs[index]
    }

    private fun findFiles(dir: File):List<SongInfo>{
        val children = dir.listFiles()

        val mutableList:MutableList<SongInfo> = mutableListOf()
        for (child in children) {
            if(child.isDirectory){
                mutableList.addAll(findFiles(child))
            } else if(child.extension == "mp3")
                mutableList += SongInfo(child, child.nameWithoutExtension)
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