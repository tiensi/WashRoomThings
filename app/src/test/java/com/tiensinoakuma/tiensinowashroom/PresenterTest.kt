package com.tiensinoakuma.tiensinowashroom

import org.junit.Test

/**
 * Test Door logic
 */
class PresenterTest {

    //todo
    val presenter: Presenter = Presenter()
    val SWITCH_1 : String = "first_switch"
    val SWITCH_2 : String = "second_switch"

    @Test
    fun doorOpenedAndClosedTooQuickly() {
    }

    @Test
    fun doorClosedAfterInitializing() {
        presenter.onSwitchChanged(SwitchChange(false, SWITCH_1))

    }


}
