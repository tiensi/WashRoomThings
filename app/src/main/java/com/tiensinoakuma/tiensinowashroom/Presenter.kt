package com.tiensinoakuma.tiensinowashroom

import android.support.annotation.VisibleForTesting
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.SerialDisposable
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

/**
 * Business logic (None Android System related)
 */
class Presenter(private val api: WashRoomApiClient, private val view: View) {

    @VisibleForTesting
    val switchSubject = BehaviorSubject.create<SwitchChange>()
    private val disposable = SerialDisposable()
    private var isVacant = false

    init{
        //Only update api and screen if door status has changed and stayed change
        disposable.set(switchSubject
                .debounce(1, TimeUnit.SECONDS)
                .filter {
                    it.on != isVacant
                }
                .doOnNext {
                    view.setRoomAvailable(it.on)
                    isVacant = it.on
                }
                .flatMapCompletable {
                    api.updateRoom(ROOM_NAME, AMENITY_NAME, it.on)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //Success!
                    Log.d("Api call", "success!")
                }, { error ->
                    System.out.println(error.message)
                    Log.w("error", error) })
        )
    }

    companion object {
        const val ROOM_NAME = "t49"
        const val AMENITY_NAME = "unisex"
    }

    /**
     * Determines whether or not someone has entered or left the room depending on the state of the
     * door switch.
     *
     * Note: The door switch outputs false if the door is closed and true otherwise.
     */
    fun onSwitchChanged(switchChange: SwitchChange) {
        switchSubject.onNext(switchChange)
    }

    fun onDestroy() {
        disposable.dispose()
    }
}