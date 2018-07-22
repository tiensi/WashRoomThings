package com.tiensinoakuma.tiensinowashroom

import com.nhaarman.mockitokotlin2.*
import com.tiensinoakuma.tiensinowashroom.util.TestSchedulerRule
import io.reactivex.Completable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * Test Door logic
 */
class PresenterTest {

    @get:Rule
    val schedulerRule = TestSchedulerRule()
    private val scheduler = schedulerRule.scheduler
    private val apiClient: WashRoomApiClient = mock()
    private val view: View = mock()
    lateinit var presenter: Presenter

    @Before
    fun setup() {
        presenter = Presenter(apiClient, view)
        whenever(apiClient.updateRoom(any(), any(), any())).thenReturn(Completable.complete())
    }

    @Test
    fun ignoreIfValueHasntChanged() {
        presenter = Presenter(apiClient, view)
        presenter.onSwitchChanged(SwitchChange(true, "door"))
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        presenter.onSwitchChanged(SwitchChange(true, "door"))
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        verify(view).setRoomAvailable(true)
    }

    @Test
    fun ignoreIfCaughtByDebounce() {
        presenter = Presenter(apiClient, view)
        presenter.onSwitchChanged(SwitchChange(true, "door"))
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        verify(view).setRoomAvailable(true)
        presenter.onSwitchChanged(SwitchChange(false, "door"))
        scheduler.advanceTimeBy(200, TimeUnit.MILLISECONDS)
        verify(view, never()).setRoomAvailable(false)
    }

    @Test
    fun updateIfChangedOverTime() {
        presenter.onSwitchChanged(SwitchChange(true, "door"))
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        verify(view).setRoomAvailable(true)
        presenter.onSwitchChanged(SwitchChange(false, "door"))
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        verify(view).setRoomAvailable(false)
    }

}
