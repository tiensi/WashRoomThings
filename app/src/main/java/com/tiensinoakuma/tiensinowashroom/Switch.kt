package com.tiensinoakuma.tiensinowashroom

import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class Switch(val name: String, val gpio: Gpio) {
    private val callback: GpioCallback
    val switchSubject: Subject<SwitchChange> = PublishSubject.create()

    init {
        switchSubject.toSerialized()
        callback = GpioCallback { gpio ->
            Log.d("Amenity is vacant", gpio.value.toString())
            switchSubject.onNext(SwitchChange(gpio.value, name))
            true
        }
        gpio.setDirection(Gpio.DIRECTION_IN)
        gpio.setActiveType(Gpio.ACTIVE_HIGH)
        gpio.setEdgeTriggerType(Gpio.EDGE_BOTH)
        gpio.registerGpioCallback(callback)
    }

    fun onDestroy() {
        gpio.unregisterGpioCallback(callback)
    }
}
