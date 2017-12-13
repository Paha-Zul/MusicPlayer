package com.paha.musicapp.util

import android.app.Activity
import android.content.Context
import android.util.Log
import com.paha.musicapp.objects.SongInfo
import java.io.*
import android.provider.MediaStore


object SongsUtil {
    var allSongs:List<SongInfo> = listOf()
    var songsByArtistMap:HashMap<String, List<SongInfo>> = hashMapOf()
    var songsByAlbumMap:HashMap<String, List<SongInfo>> = hashMapOf()
    var favoriteSongs:MutableList<SongInfo> = mutableListOf()

    fun loadAllSongs(activity:Activity):List<SongInfo>{
        val files = findFiles(File(getStorage()))
        allSongs = getAllSongs(activity)
        return files
    }

    fun compileSongsIntoCategories(){

        val artistMap = hashMapOf<String, MutableList<SongInfo>>()
        val albumMap = hashMapOf<String, MutableList<SongInfo>>()

        allSongs.forEach { song ->
            artistMap.getOrPut(song.artisName, { mutableListOf() }).add(song)
            albumMap.getOrPut(song.albumName, { mutableListOf() }).add(song)
        }

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
            File("/storage/").absolutePath
        }
    }

    private fun getAllSongs(activity:Activity) : List<SongInfo> {
        val allSongsURI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

        val songList: MutableList<SongInfo> = mutableListOf()
//        val cursor = activity.managedQuery(allsongsuri, STAR, selection, null, null)
        //or
        val cursor = activity.contentResolver.query(allSongsURI, null, null, null, selection);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val songName = cursor
                            .getString(cursor
                                    .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                    val songID = cursor.getInt(cursor
                            .getColumnIndex(MediaStore.Audio.Media._ID))

                    val fullPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))

                    songList += SongInfo(fullPath, songName).apply {
                        this.duration = duration
                        this.albumName = album
                        this.artisName = artist
                    }

//                    Log.e(TAG, "Song Name ::$songName Song Id :$songID fullpath ::$fullPath Duration ::$duration")

                } while (cursor.moveToNext())

            }
            cursor.close()
        }

        return songList.toList()
    }
}