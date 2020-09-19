package kz.sozdik.extenssions

import com.agoda.kakao.text.KButton

fun KButton.scrollToAndClick() {
    scrollTo()
    click()
}