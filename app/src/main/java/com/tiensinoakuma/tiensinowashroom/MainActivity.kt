package com.tiensinoakuma.tiensinowashroom

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.IOException

class MainActivity : View, Activity() {

    private lateinit var service: PeripheralManager
    private val switchManager: SwitchManager = SwitchManager()
    private lateinit var presenter: Presenter
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    lateinit var redSwitch: Gpio
    lateinit var greenSwitch: Gpio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        service = PeripheralManager.getInstance()
        val firstSwitch = Switch("Switch_1", service.openGpio("GPIO2_IO07"))
        switchManager.addSwitch(firstSwitch)
        switchManager.setCallbacks({ change ->
            run {
                presenter.onSwitchChanged(change)
            }
        })
        presenter = Presenter(WashRoomApiClient, this)

        redSwitch = service.openGpio("GPIO2_IO02")
        redSwitch.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
        greenSwitch = service.openGpio("GPIO6_IO15")
        greenSwitch.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        switchManager.onDestroy()
        greenSwitch.close()
        redSwitch.close()
        presenter.onDestroy()
        super.onDestroy()
    }

    fun SwitchManager.setCallbacks(callback: (change: SwitchChange) -> Unit) {
        switchMap.forEach {
            compositeDisposable.add(it.value.switchSubject
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ switchChange -> callback(switchChange) }, { error -> Log.w("Switch error", error) })
            )
        }
    }

    override fun setRoomAvailable(available: Boolean) {
        try {
            greenSwitch.value = available
            redSwitch.value = !available
        } catch (e: IOException) {
            Log.e("Error", "Error updating GPIO value", e)
        }

    }

}
