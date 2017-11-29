package com.paha.musicapp.util

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.paha.musicapp.objects.SongInfo
import com.paha.musicapp.shuffle
import java.io.*
import android.os.Environment.getExternalStorageDirectory
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag


object SongsUtil {
    var allSongs:List<SongInfo> = listOf()
    var songsByArtistMap:HashMap<String, List<SongInfo>> = hashMapOf()
    var songsByAlbumMap:HashMap<String, List<SongInfo>> = hashMapOf()
    var favoriteSongs:MutableList<SongInfo> = mutableListOf()



    fun loadAllSongs():List<SongInfo>{
        val files = findFiles(File(getStorage()))
        allSongs = files
        return files
    }

    fun compileSongsIntoCategories(){
//        val dr = MediaMetadataRetriever()

        val artistMap = hashMapOf<String, MutableList<SongInfo>>()
        val albumMap = hashMapOf<String, MutableList<SongInfo>>()

        allSongs.forEach { song ->
            val audio = AudioFileIO.read(File(song.filePath))
            val tag = audio.tag
            val file = File(song.filePath)

//            dr.setDataSource(song.filePath, hashMapOf())
//            val artistName = dr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "unknown"
//            //If we don't have an album name, use the parent's name (a folder that is probably named the album)
//            val albumName = dr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: file.parentFile.name
//            val albumArtist = dr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST) ?: "unknown"

            val artistName = tag.getFirst(FieldKey.ARTIST) ?: "unknown"
            //If we don't have an album name, use the parent's name (a folder that is probably named the album)
            val albumName = tag.getFirst(FieldKey.ALBUM)  ?: file.parentFile.name
            val albumArtist = tag.getFirst(FieldKey.ALBUM_ARTIST)  ?: "unknown"

            song.artisName = artistName
            song.albumName = albumName

//            println("song: ${song.fileName}, artist: $artistName, albumName: $albumName, albumArtist: $albumArtist")

            artistMap.getOrPut(artistName, { mutableListOf() }).add(song)
            albumMap.getOrPut(albumName, { mutableListOf() }).add(song)
        }

//        dr.release()

        for (entry in artistMap) {
            songsByArtistMap.put(entry.key, entry.value.toList())
        }

        for (entry in albumMap) {
            songsByAlbumMap.put(entry.key, entry.value.toList())
        }
    }

    fun loadFavoriteSongs(context:Context){
        var data = ""

        try {
            val inputStream = context.openFileInput("config.txt")

            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                val stringBuilder = StringBuilder()

                val receiveString = bufferedReader.readLine()
                stringBuilder.append(receiveString)

//                while (receiveString != null) {
//                    stringBuilder.append(receiveString)
//                    receiveString = bufferedReader.readLine()
//                }

                inputStream.close()
                data = stringBuilder.toString()
            }
        } catch (e: FileNotFoundException) {
            Log.e("login activity", "File not found: " + e.toString())
        } catch (e: IOException) {
            Log.e("login activity", "Can not read file: " + e.toString())
        }

        //Split the data into their separate parts
        val songData = data.split(';')
        if(songData.size == 1 && songData[0].isEmpty())
            return

        songData.forEach { //For each piece of data, split it into the song path and the song name
            if(it != "null" && it.isNotEmpty()) {
                val parts = it.split(':')
                if(parts.size > 1)
                    favoriteSongs.add(SongInfo(parts[1], parts[0])) //The data will be in songName:songFile format
            }
        }
    }

    fun saveFavorites(context:Context){
        try {
            var songData = ""
            favoriteSongs.forEachIndexed { index, songInfo ->
                songData += "${songInfo.songName}:${songInfo.filePath}${if(index < favoriteSongs.size-1) ";" else ""}" }

            val outputStreamWriter = OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE))
            outputStreamWriter.write(songData)
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: " + e.toString())
        }

    }

    fun getNextSong(currSong:String): SongInfo {
        var index = allSongs.indexOfLast { it.songName == currSong }
        index = (index+1) % allSongs.size
        return allSongs[index]
    }

    fun getPreviousSong(currSong:String): SongInfo {
        var index = allSongs.indexOfLast { it.songName == currSong }
        index = (index-1) % allSongs.size
        return allSongs[index]
    }



    private fun findFiles(dir: File):List<SongInfo>{
        val children = dir.listFiles() ?: return listOf()

        val mutableList:MutableList<SongInfo> = mutableListOf()
        for (child in children) {
            if(child.isDirectory){
                mutableList.addAll(findFiles(child))
            } else if(child.extension == "mp3")
                mutableList += SongInfo(child.path, child.nameWithoutExtension)
        }

        return mutableList.toList()
    }

    private fun getStorage():String = when{
        File("/storage/extSdCard/").exists() -> "/storage/extSdCard/"
        File("/storage/sdcard1/").exists() -> "/storage/sdcard1/"
        File("/storage/usbcard1/").exists() -> "/storage/usbcard1/"
        File("/storage/sdcard0/").exists() -> "/storage/sdcard0/"
        else -> {
            var removableStoragePath: String = ""
            val fileList = File("/storage/").listFiles()
            for (file in fileList) {
                if (!file.absolutePath.equals(Environment.getExternalStorageDirectory().absolutePath, true) && file.isDirectory && file.canRead())
                    removableStoragePath = file.absolutePath
            }

            if(removableStoragePath.isEmpty()) Environment.getExternalStorageDirectory().absolutePath
            else removableStoragePath
        }
    }
}