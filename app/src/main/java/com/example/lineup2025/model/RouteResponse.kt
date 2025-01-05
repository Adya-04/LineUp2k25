package com.example.lineup2025.model

data class RouteResponse(
    val nearestUsers : Array<Location>
)

data class Location(
    val name : String,
    var avatar : Int,
    val distance: Double,
    val direction: String
)
