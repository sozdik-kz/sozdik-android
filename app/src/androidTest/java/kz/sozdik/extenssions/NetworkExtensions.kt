package kz.sozdik.extenssions

import com.kaspersky.kaspresso.device.network.Network

fun Network.enableWifiAndMobileData() {
    runCatching {
        toggleWiFi(true)
        enable()
    }
}

fun Network.disableWifiAndMobileData() {
    runCatching {
        toggleWiFi(false)
        disable()
    }
}