package com.paha.musicapp

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import java.text.DecimalFormat


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MusicPlayerFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MusicPlayerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MusicPlayerFragment(private val parentContext:Context?, private val onCreateViewCallback:(view:View)->Unit) : android.app.Fragment() {
    constructor() : this(null, {})

    interface OnHeadlineSelectedListener {
        fun onArticleSelected(position: Int)
    }

    val onSeekBarTouched = {}

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1)
            mParam2 = arguments.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_music_player, container, false)
        onCreateViewCallback(view)
        view.findViewById<ImageButton>(R.id.previousSong).setOnClickListener { previousSong(it) }
        view.findViewById<ImageButton>(R.id.nextSong).setOnClickListener { nextSong(it) }
        val playButton = view.findViewById<ImageButton>(R.id.togglePlay)
        playButton.setOnClickListener {
            togglePlay(it)
            if(mediaPlayer.isPlaying)
                playButton.setImageResource(R.drawable.ic_pause_black_48dp)
            else
                playButton.setImageResource(R.drawable.ic_play_arrow_black_48dp)
        }

        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    fun playSong(context:Context, songFile:FileInfo){
        val attributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

        val myUri = Uri.parse(songFile.file.path) // initialize Uri here
        mediaPlayer.reset()

        MusicPlayerFragment.mediaPlayer.setAudioAttributes(attributes)
        mediaPlayer.setDataSource(context, myUri)
        mediaPlayer.prepare()
        mediaPlayer.start()

        if(view != null) {
            view.findViewById<TextView>(R.id.songPlayerSongName).text = songFile.fileName
            val playButton = view.findViewById<ImageButton>(R.id.togglePlay)
            playButton.setImageResource(R.drawable.ic_play_arrow_black_48dp)
        }

        currSongFile = songFile
    }

    private fun togglePlay(v:View):Unit{
        if(mediaPlayer.isPlaying) mediaPlayer.pause()
        else{
            val length = mediaPlayer.currentPosition
            mediaPlayer.start()
            mediaPlayer.seekTo(length)
        }
    }

    private fun previousSong(v:View){
        val newSong = SongsUtil.getPreviousSong(currSongFile.fileName)
        playSong(parentContext!!, newSong)
    }

    private fun nextSong(v:View){
        val newSong = SongsUtil.getNextSong(currSongFile.fileName)
        playSong(parentContext!!, newSong)
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        val mediaPlayer = MediaPlayer()
        val format = DecimalFormat("00")

        lateinit var currSongFile:FileInfo

        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MusicPlayerFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): MusicPlayerFragment {
            val fragment = MusicPlayerFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
