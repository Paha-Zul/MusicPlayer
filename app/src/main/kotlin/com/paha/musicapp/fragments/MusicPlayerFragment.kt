package com.paha.musicapp.fragments

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.math.MathUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.paha.musicapp.objects.SongInfo
import com.paha.musicapp.R
import com.paha.musicapp.util.PlaylistUtil
import com.paha.musicapp.util.SongsUtil
import java.text.DecimalFormat


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MusicPlayerFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MusicPlayerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MusicPlayerFragment(private val parentContext:Context?, private val onCreateViewCallback:(view:View)->Unit) : Fragment() {
    constructor() : this(null, {})

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mListener: OnFragmentInteractionListener? = null

    var shuffled = false

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

        view.findViewById<TextView>(R.id.songPlayerSongTime).text = " 0:00/0:00 "
        onCreateViewCallback(view)

        view.findViewById<ImageButton>(R.id.previousSong).setOnClickListener { previousSong(it) }
        view.findViewById<ImageButton>(R.id.nextSong).setOnClickListener { nextSong(it) }
        view.findViewById<ImageButton>(R.id.replay10).setOnClickListener { goBack10(it) }

        val seekBar = view.findViewById<SeekBar>(R.id.songSeekBar)
        seekBar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val value = (seekBar!!.progress/100f) * mediaPlayer.duration
                mediaPlayer.seekTo(value.toInt())
            }
        })

        val playButton = view.findViewById<ImageButton>(R.id.togglePlay)
        playButton.setOnClickListener {
            togglePlay(it)
            if(mediaPlayer.isPlaying)
                playButton.setBackgroundResource(R.drawable.ic_pause_white)
            else
                playButton.setBackgroundResource(R.drawable.ic_play_arrow_white_48dp)
        }

        val shuffleButton = view.findViewById<ImageButton>(R.id.shuffle_playlist)
        shuffleButton.setOnClickListener {
            shuffled = !shuffled
            if(shuffled)
                shuffleButton.backgroundTintList = ContextCompat.getColorStateList(context, R.color.maroon)
            else
                shuffleButton.backgroundTintList = ContextCompat.getColorStateList(context, R.color.white)
        }

        val favoriteButton = view.findViewById<ImageButton>(R.id.favorite_button)
        var isFavorite = SongsUtil.favoriteSongs.firstOrNull { it.songName == currSongFile.songName } != null
        val background = if(isFavorite) R.drawable.ic_favorite_white_48dp else R.drawable.ic_favorite_border_white_48dp
        favoriteButton.setBackgroundResource(background)
        favoriteButton.setOnClickListener {
            isFavorite = SongsUtil.favoriteSongs.firstOrNull { it.songName == currSongFile.songName } != null
            if(isFavorite){
                toggleFavoriteSong(view, false)
                favoriteButton.setBackgroundResource(R.drawable.ic_favorite_border_white_48dp)
            }else{
                toggleFavoriteSong(view, true)
                favoriteButton.setBackgroundResource(R.drawable.ic_favorite_white_48dp)
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        mediaPlayer.setOnCompletionListener {
            //TODO Don't leave "all" here. Find a way to differentiate playlists
            val song = PlaylistUtil.getNextSongInPlaylist(currSongFile.songName, "all", shuffled, false)
            playSong(song)
        }
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

    fun playSong(songFile: SongInfo){
        currSongFile = songFile

        val attributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

        val myUri = Uri.parse(songFile.filePath) // initialize Uri here
        mediaPlayer.reset()

        mediaPlayer.setAudioAttributes(attributes)
        mediaPlayer.setDataSource(parentContext, myUri) //We use the parent context because our context might be null when first starting
        mediaPlayer.prepare()
        mediaPlayer.start()

        if(view != null) {
            view!!.findViewById<TextView>(R.id.songPlayerSongName).text = songFile.songName
            val playButton = view!!.findViewById<ImageButton>(R.id.togglePlay)
            playButton.setBackgroundResource(R.drawable.ic_pause_white)
        }
    }

    private fun togglePlay(v:View){
        if(mediaPlayer.isPlaying) mediaPlayer.pause()
        else{
            val length = mediaPlayer.currentPosition
            mediaPlayer.start()
            mediaPlayer.seekTo(length)
        }
    }

    private fun goBack10(v:View){
        //seek to 10 seconds (10000ms) before
        mediaPlayer.seekTo(MathUtils.clamp(mediaPlayer.currentPosition - 10000, 0, mediaPlayer.duration))
    }

    private fun previousSong(v:View){
        val newSong = PlaylistUtil.getNextSongInPlaylist(currSongFile.songName, "all", shuffled, true)
        playSong(newSong)
    }

    private fun toggleFavoriteSong(v:View, setAsFavorite:Boolean){
        if(setAsFavorite)
            SongsUtil.favoriteSongs.add(currSongFile)
        else
            SongsUtil.favoriteSongs.removeAll { it.songName == currSongFile.songName }
    }

    private fun nextSong(v:View){
        val newSong = PlaylistUtil.getNextSongInPlaylist(currSongFile.songName, "all", shuffled, false)
        playSong(newSong)
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

        lateinit var currSongFile: SongInfo

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
