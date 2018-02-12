package com.tiensinoakuma.tiensinowashroom

/**
 * Data class to be passed around when listening to Switch.kt
 * Switch state on indicates that there is a connection
 */
data class SwitchChange(val on: Boolean, val name: String)