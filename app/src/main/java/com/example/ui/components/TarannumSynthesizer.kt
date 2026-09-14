package com.example.ui.components

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import com.example.data.model.RagaTarannum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class TarannumSynthesizer(private val context: Context) {
    private var audioTrack: AudioTrack? = null
    private var synthJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentRaga = MutableStateFlow(RagaTarannum.BHAIRAVI)
    val currentRaga: StateFlow<RagaTarannum> = _currentRaga.asStateFlow()

    private val _activeSwara = MutableStateFlow("Sa")
    val activeSwara: StateFlow<String> = _activeSwara.asStateFlow()

    private val _volume = MutableStateFlow(0.40f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    fun setVolume(vol: Float) {
        val clamped = vol.coerceIn(0.05f, 1f)
        _volume.value = clamped
        try {
            audioTrack?.setVolume(clamped)
        } catch (e: Exception) {
            Log.e("TarannumSynth", "Volume set error: ${e.message}")
        }
    }

    fun startTarannum(raga: RagaTarannum) {
        stopTarannum()
        _currentRaga.value = raga
        _isPlaying.value = true

        synthJob = scope.launch {
            val sampleRate = 22050
            val minBuf = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = (minBuf * 2).coerceAtLeast(4096)

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack = track
            track.setVolume(_volume.value)
            track.play()

            val shortBuffer = ShortArray(bufferSize / 2)
            var phaseRoot = 0.0
            var phaseFifth = 0.0
            var phaseOctave = 0.0
            var melodyPhase = 0.0

            val baseHz = raga.rootPitchHz
            val paHz = baseHz * 1.4983 // Just fifth (Pancham)
            val highSaHz = baseHz * 2.0

            var swaraIndex = 0
            var samplesSinceSwaraChange = 0
            val swaraChangeIntervalSamples = (sampleRate * 2.5).toInt() // Change swara every 2.5s

            while (isActive && _isPlaying.value) {
                val swaras = raga.scaleSwaras
                val freqs = raga.scaleFrequencies

                val currentMelodyFreq = freqs[swaraIndex % freqs.size]
                val currentSwaraName = swaras[swaraIndex % swaras.size]
                _activeSwara.value = currentSwaraName

                for (i in shortBuffer.indices) {
                    // Continuous rich Tanpura drone (Sa + Pa + High Sa)
                    val s1 = sin(phaseRoot)
                    val s2 = 0.7 * sin(phaseFifth)
                    val s3 = 0.4 * sin(phaseOctave)

                    // Subtle melodic harmonium shimmer on top of the drone
                    val sMelody = 0.35 * sin(melodyPhase)

                    // Natural acoustic tremolo / slow breathing swell
                    val tremolo = 0.85 + 0.15 * sin(phaseRoot * 0.02)

                    val mixed = (s1 + s2 + s3 + sMelody) * tremolo * 0.25
                    shortBuffer[i] = (mixed.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()

                    phaseRoot += 2 * PI * baseHz / sampleRate
                    if (phaseRoot > 2 * PI) phaseRoot -= 2 * PI

                    phaseFifth += 2 * PI * paHz / sampleRate
                    if (phaseFifth > 2 * PI) phaseFifth -= 2 * PI

                    phaseOctave += 2 * PI * highSaHz / sampleRate
                    if (phaseOctave > 2 * PI) phaseOctave -= 2 * PI

                    melodyPhase += 2 * PI * currentMelodyFreq / sampleRate
                    if (melodyPhase > 2 * PI) melodyPhase -= 2 * PI

                    samplesSinceSwaraChange++
                    if (samplesSinceSwaraChange >= swaraChangeIntervalSamples) {
                        samplesSinceSwaraChange = 0
                        swaraIndex = (swaraIndex + 1) % freqs.size
                    }
                }

                track.write(shortBuffer, 0, shortBuffer.size)
            }
        }
    }

    fun stopTarannum() {
        _isPlaying.value = false
        synthJob?.cancel()
        synthJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            // ignore
        }
        audioTrack = null
    }

    fun release() {
        stopTarannum()
    }
}
