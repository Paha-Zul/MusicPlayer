package com.paha.musicapp

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.paha.musicapp.fragments.MusicPlayerFragment


class SongListAdapter(val parentContext:Context, private val fragmentManager:FragmentManager, val data:Array<FileInfo>)
    : ArrayAdapter<FileInfo>(parentContext, R.layout.simple_list, data), View.OnClickListener {

    private var lastPosition:Int = 0

    // View lookup cache
    private class ViewHolder {
        internal lateinit var songName: TextView
        internal lateinit var songTime: TextView
        internal lateinit var button: ImageButton
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView

        // Get the data item for this position
        val dataModel = getItem(position)
        // Check if an existing view is being reused, otherwise inflate the view
        val viewHolder: ViewHolder // view lookup cache stored in tag

        val result: View

        if (convertView == null) {

            viewHolder = ViewHolder()
            val inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(R.layout.simple_list, parent, false)
            viewHolder.songName = convertView.findViewById(R.id.songView)
            viewHolder.songTime = convertView.findViewById(R.id.timeView)
            viewHolder.button = convertView.findViewById(R.id.songOptionsButton)

            result = convertView

            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            result = convertView
        }

        lastPosition = position

        viewHolder.songName.text = dataModel.fileName
        viewHolder.songTime.text = "3:20"

        convertView!!.setOnClickListener(MusicInfoOnClickListener(fragmentManager, parentContext, dataModel))

        // Return the completed view to render on screen
        return convertView
    }

    override fun onClick(v: View) {
        val fragmentManager = (parentContext as AppCompatActivity).supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val fragment = MusicPlayerFragment()
        fragmentTransaction.add(R.id.listView, fragment)
        fragmentTransaction.commit()

        println("Hi2")
    }
}