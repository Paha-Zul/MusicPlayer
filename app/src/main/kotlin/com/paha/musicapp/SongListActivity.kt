package com.paha.musicapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.SeekBar
import android.widget.TextView
import java.io.File

class SongListActivity : AppCompatActivity() {

    private lateinit var listView:ListView
    private lateinit var arrayAdapter:SongListAdapter

    private val songClickListener:View.OnClickListener = View.OnClickListener { view ->
        println("Hey whats up")

        val fragmentManager = fragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val fragment = MusicPlayerFragment({ fragmentView ->
            val songName = fragmentView.findViewById<TextView>(R.id.songPlayerSongName)
            val songTime = fragmentView.findViewById<TextView>(R.id.songPlayerSongTime)
            val songSeek = fragmentView.findViewById<SeekBar>(R.id.songSeekBar)

            songName.text = view.findViewById<TextView>(R.id.songView).text
            songTime.text = view.findViewById<TextView>(R.id.timeView).text
        })

        fragmentTransaction.add(R.id.song_list_layout, fragment)
        fragmentTransaction.commit()


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_list)

        listView = findViewById(R.id.listView) as ListView

        val files = findFiles(File(getStorage()))

        arrayAdapter = SongListAdapter(this, files.toTypedArray(), songClickListener)
        listView.adapter = arrayAdapter
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

    private fun getStorage():String{
        return when{
            File("/storage/extSdCard/").exists() -> "/storage/extSdCard/"
            File("/storage/sdcard1/").exists() -> "/storage/sdcard1/"
            File("/storage/usbcard1/").exists() -> "/storage/usbcard1/"
            File("/storage/sdcard0/").exists() -> "/storage/sdcard0/"
            else -> ""
        }
    }
}
