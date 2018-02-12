package com.tiensinoakuma.tiensinowashroom

/**
 * Holds onto switches
 */
class SwitchManager {

    val switchMap: MutableMap<String, Switch> = HashMap()

    fun addSwitch(switch: Switch) {
        if (switchMap.contains(switch.name)) {
            throw IllegalArgumentException("No duplicate naming")
        }
        switchMap.put(switch.name, switch)
    }

    fun onDestroy() {
        switchMap.forEach {
            it.value.onDestroy()
        }
    }

}