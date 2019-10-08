package com.github2136.base.entity

/**
 * Created by YB on 2019/10/8
 */
data class Weather(
    val weatherinfo: Weatherinfo
)

data class Weatherinfo(
    val Radar: String,
    val SD: String,
    val WD: String,
    val WS: String,
    val WSE: String,
    val city: String,
    val cityid: String,
    val isRadar: String,
    val njd: String,
    val qy: String,
    val temp: String,
    val time: String
)