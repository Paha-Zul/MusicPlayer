package com.paha.musicapp.adapaters

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import com.paha.musicapp.R
import com.paha.musicapp.fragments.MusicPlayerFragment
import com.paha.musicapp.listeners.MusicInfoOnClickListener
import com.paha.musicapp.objects.SongInfo


class SongListAdapter(val parentContext:Context, private val fragmentManager:FragmentManager, val data:Array<SongInfo>)
    : ArrayAdapter<SongInfo>(parentContext, R.layout.simple_list, data), View.OnClickListener {

    private var lastPosition:Int = 0

//    val animationStates = Array(data.size, {false})
//    var initialAnimation = false

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

//        if (!animationStates[position]) {
//            val listView = parent!!.findViewById<ListView>(R.id.all_songs_list_view)
//            val firstVisible = listView.firstVisiblePosition
//            Log.e("TAG", "Animating item no: " + position)
//            animationStates[position] = true
//            val animation = AnimationUtils.loadAnimation(context, R.anim.abc_slide_in_bottom)
//            animation.startOffset = (position - firstVisible)*100L
//            convertView.startAnimation(animation)
//        }

//        val animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
//        animation.startOffset = position * 100L
//        convertView.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left))

        // Return the completed view to render on screen
        return convertView
    }

    private fun initialAnimation(parentView:View){
        val listView = parentView.findViewById<ListView>(R.id.all_songs_list_view)
        //This will animate only the initial viewed items when the adapter is set.
        for(i in listView.firstVisiblePosition until listView.lastVisiblePosition){
            val child = listView.getChildAt(i)
            Log.e("TAG", "Animating item no: " + i)
            val animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom)
            animation.startOffset = i*500L
            child.startAnimation(animation)
        }
    }


    override fun onClick(v: View) {
        val fragmentManager = (parentContext as AppCompatActivity).supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val fragment = MusicPlayerFragment()
        fragmentTransaction.add(R.id.all_songs_list_view, fragment)
        fragmentTransaction.commit()

        println("Hi2")
    }


}