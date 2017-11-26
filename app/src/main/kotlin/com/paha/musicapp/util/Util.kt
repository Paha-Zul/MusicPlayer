package com.paha.musicapp.util

object Util {
    fun mod(num:Int, bound:Int):Int = (((num % bound) + bound) % bound)
}