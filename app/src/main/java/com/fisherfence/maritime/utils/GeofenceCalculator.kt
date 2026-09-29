package com.fisherfence.maritime.utils

import kotlin.math.*

object GeofenceCalculator {

    // Hardcoded IMBL boundary coordinates
    val IMBL_POLYGON = listOf(
        Pair(13.35, 80.45),
        Pair(13.20, 80.60),
        Pair(13.00, 80.65),
        Pair(12.80, 80.55),
        Pair(12.70, 80.40),
        Pair(12.85, 80.25),
        Pair(13.10, 80.20),
        Pair(13.35, 80.45)
    )

    private const val EARTH_RADIUS_KM = 6371.0

    /**
     * Calculates the distance between two points in kilometers using the Haversine formula.
     */
    fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }

    /**
     * Finds the shortest distance in kilometers from a point to the IMBL boundary.
     */
    fun getDistanceToBoundary(lat: Double, lon: Double): Double {
        var minDistance = Double.MAX_VALUE
        for (i in 0 until IMBL_POLYGON.size - 1) {
            val p1 = IMBL_POLYGON[i]
            val p2 = IMBL_POLYGON[i + 1]
            val distance = distanceToSegment(lat, lon, p1.first, p1.second, p2.first, p2.second)
            if (distance < minDistance) {
                minDistance = distance
            }
        }
        return minDistance
    }

    /**
     * Checks if the boat is on the outer side (restricted side) of the IMBL boundary.
     * We can determine this by checking if the coordinate lies to the east/outside of the Chennai baseline.
     * Simple polygon containment (Ray Casting Algorithm) can also be used.
     */
    fun isCrossedBorder(lat: Double, lon: Double): Boolean {
        // Ray casting algorithm to check if inside the restricted polygon
        var isInside = false
        var j = IMBL_POLYGON.size - 1
        for (i in IMBL_POLYGON.indices) {
            val pi = IMBL_POLYGON[i]
            val pj = IMBL_POLYGON[j]
            if ((pi.second > lon) != (pj.second > lon) &&
                (lat < (pj.first - pi.first) * (lon - pi.second) / (pj.second - pi.second) + pi.first)
            ) {
                isInside = true
            }
            j = i
        }
        
        // Wait, our polygon outlines the boundary. In our mapping, the land is to the West (Chennai)
        // and the sea extends East. The polygon represents the international maritime boundary line (20km offshore).
        // Let's assume that if the boat's longitude is greater than the polygon's edge at that latitude,
        // or if it's "inside/past" the polygon, it has crossed the border.
        // Let's check: if the boat moves east (towards higher longitudes), it crosses the IMBL boundary.
        // Let's define crossed border if: we are past the line (i.e. longitude > boundary longitude at this latitude).
        // Let's find the boundary longitude at the boat's latitude.
        val boundaryLon = getBoundaryLongitudeAt(lat)
        return lon >= boundaryLon
    }

    /**
     * Finds the boundary longitude for a given latitude.
     */
    private fun getBoundaryLongitudeAt(lat: Double): Double {
        // Find segment containing this latitude, project longitude
        var intersectLon = 80.45 // default fallback
        for (i in 0 until IMBL_POLYGON.size - 1) {
            val p1 = IMBL_POLYGON[i]
            val p2 = IMBL_POLYGON[i + 1]
            val minLat = minOf(p1.first, p2.first)
            val maxLat = maxOf(p1.first, p2.first)
            if (lat in minLat..maxLat) {
                if (maxLat - minLat > 0.0001) {
                    val t = (lat - p1.first) / (p2.first - p1.first)
                    val segmentLon = p1.second + t * (p2.second - p1.second)
                    if (segmentLon > intersectLon) {
                        intersectLon = segmentLon
                    }
                }
            }
        }
        return intersectLon
    }

    /**
     * Distance from point P(lat, lon) to segment AB.
     */
    private fun distanceToSegment(lat: Double, lon: Double, latA: Double, lonA: Double, latB: Double, lonB: Double): Double {
        // We project point P onto line AB.
        // For sphere/geographical coordinates, we can approximate projection locally by treating 
        // degree differences as Cartesian since the distance is short (~20-50km).
        val xA = lonA
        val yA = latA
        val xB = lonB
        val yB = latB
        val xP = lon
        val yP = lat

        val dx = xB - xA
        val dy = yB - yA
        if (dx * dx + dy * dy < 1e-10) {
            return haversineDistance(lat, lon, latA, lonA)
        }

        // Projection coefficient t
        val t = ((xP - xA) * dx + (yP - yA) * dy) / (dx * dx + dy * dy)
        return when {
            t < 0.0 -> haversineDistance(lat, lon, latA, lonA)
            t > 1.0 -> haversineDistance(lat, lon, latB, lonB)
            else -> {
                val projLat = yA + t * dy
                val projLon = xA + t * dx
                haversineDistance(lat, lon, projLat, projLon)
            }
        }
    }
}
