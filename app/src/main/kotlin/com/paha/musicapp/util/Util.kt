package com.paha.musicapp.util

import java.text.DecimalFormat

object Util {
    val format = DecimalFormat("00")

    fun mod(num:Int, bound:Int):Int = (((num % bound) + bound) % bound)

    fun convertMilliToFormattedTime(time:Int):String
        = "${(time / 60000)}:${format.format((time% 60000) / 1000)}"

    fun convertMilliToFormattedTime(time:Long):String
        = "${(time / 60000)}:${format.format((time% 60000) / 1000)}"

}