package kz.sozdik.dictionary.di

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Vibrator
import dagger.Module
import dagger.Provides

@Module
object MediaModule {

    @Provides
    @JvmStatic
    fun provideMediaPlayer(): MediaPlayer =
        MediaPlayer().apply {
            setOnPreparedListener { it.start() }
            setOnCompletionListener { it.reset() }
            setAudioStreamType(AudioManager.STREAM_MUSIC)
        }

    @Provides
    @JvmStatic
    fun provideVibrator(context: Context): Vibrator =
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
}