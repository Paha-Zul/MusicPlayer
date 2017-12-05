package com.paha.musicapp.adapaters

import android.content.Context
import android.provider.ContactsContract
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import com.paha.musicapp.R
import com.paha.musicapp.fragments.MusicPlayerFragment
import com.paha.musicapp.listeners.MusicInfoOnClickListener
import com.paha.musicapp.objects.SongInfo


class SongListAdapter(val parentContext:Context, private val fragmentManager:FragmentManager, val data:MutableList<SongInfo>)
    : ArrayAdapter<SongInfo>(parentContext, R.layout.simple_list, data), View.OnClickListener, Filterable {

    private var lastPosition:Int = 0
    private val originalData = data.toList()

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

        viewHolder.songName.text = dataModel.songName
        viewHolder.songTime.text = "3:20"

        convertView!!.setOnClickListener(MusicInfoOnClickListener(fragmentManager, parentContext, dataModel))

//        if (!animationStates[position]) {
//            val listView = parent!!.findViewById<ListView>(R.id.all_songs_list_view)
//            val firstVisible = listView.firstVisiblePosition
//            Log.e("TAG", "Animating item no: " + position)
//            animationStates[position] = true
//            val animation = AnimationUtils.loadAnimation(parentContext, R.anim.abc_slide_in_bottom)
//            animation.startOffset = (position - firstVisible)*100L
//            convertView.startAnimation(animation)
//        }

//        val animation = AnimationUtils.loadAnimation(parentContext, android.R.anim.slide_in_left)
//        animation.startOffset = position * 100L
//        convertView.startAnimation(AnimationUtils.loadAnimation(parentContext, android.R.anim.slide_in_left))

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

    override fun getFilter(): Filter {

        return object : Filter() {

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
//                arrayListNames = results.values
                clear()
                addAll(results.values as List<SongInfo>)
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence): FilterResults {
                var constraint = constraint

                val results = FilterResults()
                val filteredSongs:ArrayList<SongInfo>?
                if(constraint.isNotEmpty()) { //If our constraint is "", don't bother with this. Use the original data
                    filteredSongs = ArrayList()
                    //Get the constraint in lowercase
                    constraint = constraint.toString().toLowerCase()
                    for (i in 0 until originalData.size) { //Loop through each piece of data checking for a substring of the constraint
                        val songName = originalData[i].songName
                        if (songName.toLowerCase().contains(constraint)) { //Check if it contains a substring, if so, add it to the filtered data
                            filteredSongs.add(originalData[i])
                        }
                    }
                }else
                    filteredSongs = null //Set this to null if we don't have a valid constraint

                results.count = filteredSongs?.size ?: originalData.size //Either use the filtered songs or the original data
                results.values = filteredSongs ?: originalData //Either use the filtered songs or the original data
//                Log.e("VALUES", results.values.toString())

                return results
            }
        }
    }

}