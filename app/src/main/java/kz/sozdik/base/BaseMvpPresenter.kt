package kz.sozdik.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import moxy.MvpPresenter
import moxy.MvpView
import kotlin.coroutines.CoroutineContext

abstract class BaseMvpPresenter<V : MvpView> :
    MvpPresenter<V>(),
    CoroutineScope {

    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main.immediate

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}