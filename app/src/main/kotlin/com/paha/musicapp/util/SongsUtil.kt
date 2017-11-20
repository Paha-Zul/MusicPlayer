package com.paha.musicapp.util

import android.content.Context
import android.media.MediaMetadataRetriever
import android.util.Log
import com.paha.musicapp.objects.SongInfo
import com.paha.musicapp.shuffle
import java.io.*


object SongsUtil {
    var shuffledSongs:List<SongInfo> = listOf()
    var songsByArtistMap:HashMap<String, List<SongInfo>> = hashMapOf()
    var songsByAlbumMap:HashMap<String, List<SongInfo>> = hashMapOf()
    var favoriteSongs:MutableList<SongInfo> = mutableListOf()

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
//            val audio = AudioFileIO.read(File(song.filePath))
//            val tag = audio.tag

            dr.setDataSource(song.filePath, hashMapOf())
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
                songData += "${songInfo.fileName}:${songInfo.filePath}${if(index < favoriteSongs.size-1) ";" else ""}" }

            val outputStreamWriter = OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE))
            outputStreamWriter.write(songData)
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: " + e.toString())
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
                mutableList += SongInfo(child.path, child.nameWithoutExtension)
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