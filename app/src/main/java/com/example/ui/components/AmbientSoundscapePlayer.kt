package com.example.ui.components

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

enum class AmbientSoundPreset(
    val title: String,
    val hindiTitle: String,
    val emoji: String,
    val description: String
) {
    OFF("Off", "बंद", "🔇", "Silent recitation"),
    TANPURA("Tanpura Drone", "तानपूरा", "🪕", "Resonant Sa-Pa classical drone"),
    MONSOON_RAIN("Monsoon Rain", "बरसात", "🌧️", "Gentle rhythmic monsoon droplets"),
    NIGHT_WHISPERS("Night Whispers", "रात की हवा", "🌙", "Nocturnal harmonic breeze"),
    NIGHT_CRICKETS("Night Crickets", "झिंगुर की आवाज़", "🦗", "Atmospheric nocturnal crickets with soothing drone"),
    TEMPLE_BELL("Singing Bowl", "घंटी ध्वनि", "🔔", "Meditative harmonic bells")
}

class AmbientSoundscapePlayer {
    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _currentPreset = MutableStateFlow(AmbientSoundPreset.OFF)
    val currentPreset: StateFlow<AmbientSoundPreset> = _currentPreset

    private val _volume = MutableStateFlow(0.35f)
    val volume: StateFlow<Float> = _volume

    fun setVolume(vol: Float) {
        val clamped = vol.coerceIn(0f, 1f)
        _volume.value = clamped
        try {
            audioTrack?.setVolume(clamped)
        } catch (e: Exception) {
            Log.e("AmbientPlayer", "Error adjusting volume: ${e.message}")
        }
    }

    fun play(preset: AmbientSoundPreset) {
        if (preset == AmbientSoundPreset.OFF) {
            stop()
            return
        }

        stop()
        _currentPreset.value = preset

        playbackJob = scope.launch {
            val sampleRate = 22050
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = (minBufferSize * 2).coerceAtLeast(4096)

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
            var phase1 = 0.0
            var phase2 = 0.0
            var phase3 = 0.0
            var bellEnvelope = 1.0

            // Base frequencies
            val f1 = when (preset) {
                AmbientSoundPreset.TANPURA -> 136.1 // Indian Om/Sa fundamental
                AmbientSoundPreset.NIGHT_WHISPERS -> 174.0 // Solfeggio healing frequency
                AmbientSoundPreset.TEMPLE_BELL -> 216.0
                else -> 100.0
            }
            val f2 = f1 * 1.5 // Perfect fifth (Pa)
            val f3 = f1 * 2.0 // Octave (Tar Sa)

            val step1 = 2.0 * PI * f1 / sampleRate
            val step2 = 2.0 * PI * f2 / sampleRate
            val step3 = 2.0 * PI * f3 / sampleRate

            var rainNoise = 0.0

            while (isActive) {
                for (i in shortBuffer.indices) {
                    val sample: Double = when (preset) {
                        AmbientSoundPreset.TANPURA -> {
                            val slowLfo = 0.8 + 0.2 * sin(phase1 * 0.02)
                            val wave1 = sin(phase1) * 0.45
                            val wave2 = sin(phase2) * 0.35 * slowLfo
                            val wave3 = sin(phase3) * 0.20
                            (wave1 + wave2 + wave3) * 0.7
                        }
                        AmbientSoundPreset.MONSOON_RAIN -> {
                            val whiteNoise = (Random.nextDouble() * 2.0 - 1.0)
                            rainNoise = rainNoise * 0.88 + whiteNoise * 0.12 // Low-pass filter
                            val drop = if (Random.nextInt(1200) == 0) sin(phase1 * 8) * 0.4 else 0.0
                            (rainNoise * 0.5 + drop) * 0.6
                        }
                        AmbientSoundPreset.NIGHT_WHISPERS -> {
                            val breezeLfo = 0.5 + 0.5 * sin(phase1 * 0.005)
                            val tone = sin(phase1) * 0.4 + sin(phase2) * 0.3
                            tone * breezeLfo * 0.6
                        }
                        AmbientSoundPreset.NIGHT_CRICKETS -> {
                            // Chirping rhythm modulation
                            val chirpEnvelope = if ((phase1.toInt() % 180) < 45) {
                                sin(phase1 * 0.4).coerceAtLeast(0.0)
                            } else 0.0
                            val cricketChirp = sin(phase1 * 14.0) * chirpEnvelope * 0.35
                            val backgroundDrone = sin(phase2 * 0.5) * 0.15
                            (cricketChirp + backgroundDrone) * 0.6
                        }
                        AmbientSoundPreset.TEMPLE_BELL -> {
                            bellEnvelope *= 0.99992
                            if (bellEnvelope < 0.08) bellEnvelope = 1.0
                            val ring = sin(phase1) * 0.5 + sin(phase2 * 1.02) * 0.35 + sin(phase3 * 2.76) * 0.15
                            ring * bellEnvelope * 0.75
                        }
                        AmbientSoundPreset.OFF -> 0.0
                    }

                    shortBuffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()

                    phase1 = (phase1 + step1) % (2.0 * PI * 1000)
                    phase2 = (phase2 + step2) % (2.0 * PI * 1000)
                    phase3 = (phase3 + step3) % (2.0 * PI * 1000)
                }

                track.write(shortBuffer, 0, shortBuffer.size)
            }
        }
    }

    fun stop() {
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.release()
        } catch (e: Exception) {
            Log.e("AmbientPlayer", "Error stopping track: ${e.message}")
        }
        audioTrack = null
        _currentPreset.value = AmbientSoundPreset.OFF
    }
}
