package hr.algebra.infiniguide.model

data class Monument(
        val IDMonument: Int,
        val MonumentID: String,
        val Sound: String,
        val About: String,
        val Name: String,
        val Lat: Double,
        val Lng: Double
)