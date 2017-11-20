package com.paha.musicapp.fragments

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.math.MathUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.paha.musicapp.objects.SongInfo
import com.paha.musicapp.R
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

        val favoriteButton = view.findViewById<ImageButton>(R.id.favorite_button)
        val isFavorite = SongsUtil.favoriteSongs.firstOrNull { it.fileName == currSongFile.fileName } != null
        val background = if(isFavorite) R.drawable.ic_favorite_white_48dp else R.drawable.ic_favorite_border_white_48dp
        favoriteButton.setBackgroundResource(background)
        favoriteButton.setOnClickListener {
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

//        val container = (parentContext!! as Activity).findViewById<FrameLayout>(R.id.music_player_container)
//        view!!.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
//        val params = container.layoutParams
//        params.height = view!!.measuredHeight
//        params.width = view!!.measuredWidth
//        container.layoutParams = params
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

    fun playSong(context:Context, songFile: SongInfo){
        val attributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

        val myUri = Uri.parse(songFile.filePath) // initialize Uri here
        mediaPlayer.reset()

        mediaPlayer.setAudioAttributes(attributes)
        mediaPlayer.setDataSource(context, myUri)
        mediaPlayer.prepare()
        mediaPlayer.start()

        if(view != null) {
            view!!.findViewById<TextView>(R.id.songPlayerSongName).text = songFile.fileName
            val playButton = view!!.findViewById<ImageButton>(R.id.togglePlay)
            playButton.setBackgroundResource(R.drawable.ic_pause_white)
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

    private fun goBack10(v:View){
        //seek to 10 seconds (10000ms) before
        mediaPlayer.seekTo(MathUtils.clamp(mediaPlayer.currentPosition - 10000, 0, mediaPlayer.duration))
    }

    private fun previousSong(v:View){
        val newSong = SongsUtil.getPreviousSong(currSongFile.fileName)
        playSong(parentContext!!, newSong)
    }

    private fun toggleFavoriteSong(v:View, setAsFavorite:Boolean){
        if(setAsFavorite)
            SongsUtil.favoriteSongs.add(currSongFile)
        else
            SongsUtil.favoriteSongs.removeAll { it.fileName == currSongFile.fileName }
    }

    private fun seekTo(v:View){

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
