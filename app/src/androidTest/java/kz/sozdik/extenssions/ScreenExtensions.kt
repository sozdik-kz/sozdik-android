package kz.sozdik.extenssions

import io.github.kakaocup.kakao.text.KButton

fun KButton.scrollToAndClick() {
    scrollTo()
    click()
}