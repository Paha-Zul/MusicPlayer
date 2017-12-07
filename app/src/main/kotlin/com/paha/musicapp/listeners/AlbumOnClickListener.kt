package com.paha.musicapp.listeners

import android.content.Context
import android.support.v4.app.FragmentManager
import android.view.View
import com.paha.musicapp.R
import com.paha.musicapp.fragments.SongListFragment
import com.paha.musicapp.objects.SongInfo

class AlbumOnClickListener(private val fragmentManager: FragmentManager, private val context: Context, private val songs:List<SongInfo>) : View.OnClickListener {
    override fun onClick(v: View?) {
        val fragmentTransaction = fragmentManager.beginTransaction()

        val fragment = SongListFragment(songs)

        fragmentTransaction.replace(R.id.overlay_container, fragment)
        fragmentTransaction.addToBackStack("")
        fragmentTransaction.commit()
    }
}