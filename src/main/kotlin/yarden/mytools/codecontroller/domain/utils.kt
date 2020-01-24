public fun Double.mapTo(beforeMin: Double, beforeMax: Double, afterMin: Double, afterMax: Double): Double {
    val n = (this - beforeMin) / (beforeMax - beforeMin)
    return afterMin + n * (afterMax - afterMin)

}