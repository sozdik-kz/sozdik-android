package kz.sozdik.di

import android.content.Context
import kz.sozdik.core.di.AppDependency

fun Context.getAppDepsProvider() =
    (applicationContext as AppDependency.AppDepsProvider).provideAppDependency()