package com.paha.musicapp

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView


class SongListAdapter(context:Context, val data:Array<FileInfo>, private val clickListener:View.OnClickListener)
    : ArrayAdapter<FileInfo>(context, R.layout.simple_list, data), View.OnClickListener {

    private var lastPosition:Int = 0

    // View lookup cache
    private class ViewHolder {
        internal lateinit var songName: TextView
        internal lateinit var songTime: TextView
        internal lateinit var button: Button
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
            viewHolder.button = convertView.findViewById(R.id.button2)

            result = convertView

            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            result = convertView
        }

//        val animation = AnimationUtils.loadAnimation(context, if (position > lastPosition) R.anim.up_from_bottom else R.anim.down_from_top)
//        result.startAnimation(animation)
        lastPosition = position

        viewHolder.songName.text = dataModel.fileName
        viewHolder.songTime.text = "3:20"

//        convertView!!.isClickable = true
        convertView!!.setOnClickListener(clickListener)

//        viewHolder.songName.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED)
//        viewHolder.songTime.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
//        viewHolder.button.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED)
//        convertView!!.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED)
//
//        val w1 = viewHolder.songName.measuredWidth
//        val w2 = viewHolder.songTime.measuredWidth
//        val w3 = viewHolder.button.measuredWidth
//        val w5 = convertView.measuredWidth
//
//        val songTimeWidth = MainActivity.SCREEN_WIDTH - (widthOfSongName + 18 + convertView!!.height)
//
//        viewHolder.songName.width = widthOfSongName
//        viewHolder.button.width = w3
//        viewHolder.songTime.width = songTimeWidth

//        viewHolder.button.layoutParams = ConstraintLayout.LayoutParams(viewHolder.button.height, viewHolder.button.height)

//        viewHolder.txtVersion.setText(dataModel.getVersion_number())
//        viewHolder.info.setOnClickListener(this)
//        viewHolder.info.setTag(position)
        // Return the completed view to render on screen
        return convertView!!
    }

    override fun onClick(v: View) {
//        val name = v.findViewById<TextView>(R.id.songView)
//        val time = v.findViewById<TextView>(R.id.songView)
//        val options = v.findViewById<Button>(R.id.button2)

        val fragmentManager = (context as Activity).fragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val fragment = MusicPlayerFragment()
        fragmentTransaction.add(R.id.listView, fragment)
        fragmentTransaction.commit()

        println("Hi2")
    }
}