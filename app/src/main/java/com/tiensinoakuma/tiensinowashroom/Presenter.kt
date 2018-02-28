package com.tiensinoakuma.tiensinowashroom

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Business logic (None Android System related)
 */
class Presenter(private val api: WashRoomApiClient, private val view: View) {

    val DOOR_CLOSED_TOO_SOON: Long = 1000
    val DOOR_OPENED_TOO_LONG: Long = 10000
    val RESET_TIMER: Long = 1200000

    /**
     * Map contains room's Unique identifier as Key, and the Value is a Pair of Timestamp of the
     * most recent change and a Boolean isVacant
     */
    private val roomStateMap: MutableMap<String, RoomStatus> = HashMap()

    /**
     * Determines whether or not someone has entered or left the room depending on the state of the
     * door switch. Valid movement to/from a room occurs only if the user opens the door and closes
     * the door in a reasonable time frame.
     *
     * Note: The door switch outputs false if the door is closed and true otherwise.
     */
    fun onSwitchChanged(switchChange: SwitchChange) {
        if (roomStateMap.contains(switchChange.name)) {
            if (switchChange.on) {
                roomStateMap[switchChange.name]!!.lastUpdated = System.currentTimeMillis()
            } else {
                //Door has been closed, check if this can be considered valid
                if (isValidTimeFrame(roomStateMap[switchChange.name]!!.lastUpdated)) {
                    //todo Notify that the room state has changed
                    roomStateMap[switchChange.name]!!.vacant = !roomStateMap[switchChange.name]!!.vacant
                    view.setRoomAvailable(roomStateMap[switchChange.name]!!.vacant)
                    api.updateRoom(switchChange.name, roomStateMap[switchChange.name]!!.vacant)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                //Success!
                            }, { error -> Log.w("error", error) })

                }
            }
            //Todo kick off alarm manager to reset every 20 minutes as a fail safe
        } else {
            //First time switch is activated, assume that door is being opened and the room is currently vacant
            if (!switchChange.on) {
                //Door is being closed, ignore
                return
            }
            roomStateMap.put(switchChange.name, RoomStatus(System.currentTimeMillis()))
        }
        Log.e("Room is: ", if (roomStateMap[switchChange.name]!!.vacant) "Vacant" else "in use")
    }

    private fun isValidTimeFrame(previousTime: Long): Boolean {
        val timeFrame = System.currentTimeMillis() - previousTime
        return timeFrame in (DOOR_CLOSED_TOO_SOON)..(DOOR_OPENED_TOO_LONG)
    }

    private data class RoomStatus(var lastUpdated: Long, var vacant: Boolean = true)
}