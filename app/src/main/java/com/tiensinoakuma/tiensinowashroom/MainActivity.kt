package com.tiensinoakuma.tiensinowashroom

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.google.android.things.pio.PeripheralManagerService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainActivity : Activity() {

    lateinit var service: PeripheralManagerService
    val switchManager: SwitchManager = SwitchManager()
    val presenter: Presenter = Presenter(WashRoomApiClient)
    val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        service = PeripheralManagerService()
        val firstSwitch = Switch("Switch_1", service.openGpio("GPIO2_IO07"))
        switchManager.addSwitch(firstSwitch)
        switchManager.setCallbacks({ change ->
            run {
                presenter.onSwitchChanged(change)
                Log.d("but what", "why")
            }
        })
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        switchManager.onDestroy()
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
}
