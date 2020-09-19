package kz.sozdik.screen

import com.agoda.kakao.navigation.KNavigationView
import kz.sozdik.R
import kz.sozdik.main.MainActivity

object MainScreen : KScreen<MainScreen>() {

    override val layoutId: Int? = R.layout.activity_main
    override val viewClass: Class<*>? = MainActivity::class.java

    val navigationView = KNavigationView { withId(R.id.bottomNavigationView) }
}