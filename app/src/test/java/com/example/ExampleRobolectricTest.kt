package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Emotion
import com.example.data.model.Language
import com.example.data.model.Shayari
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read app name string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("KavyaSetu", appName)
  }

  @Test
  fun `verify multilingual emotion mapping`() {
    val ishq = Emotion.ISHQ
    assertEquals("Love", ishq.englishLabel)
    assertEquals("इश्क़", ishq.hindiLabel)
    assertEquals("ପ୍ରେମ", ishq.odiaLabel)
    assertEquals("❤️", ishq.emoji)
  }

  @Test
  fun `verify language model codes`() {
    assertEquals("hindi", Language.HINDI.code)
    assertEquals("odia", Language.ODIA.code)
    assertEquals("english", Language.ENGLISH.code)
  }

  @Test
  fun `verify domain model initialization`() {
    val shayari = Shayari(
      id = "test_1",
      lines = "Test lines\nSecond line",
      author = "Poet",
      penName = "Voice",
      language = "odia",
      emotion = "ishq"
    )
    assertNotNull(shayari)
    assertEquals("test_1", shayari.id)
    assertEquals(false, shayari.isLiked)
    assertEquals(false, shayari.isDownloaded)
    assertEquals("APPROVED", shayari.moderationStatus)
  }

  @Test
  fun `verify content moderator flags spam url and phone numbers`() {
    val spamText = "Visit https://freepromo.xyz and call 9876543210 to win free prizes!"
    val result = com.example.moderation.ContentModerator.analyze(spamText)
    assertEquals(true, result.isFlagged)
    assertEquals("FLAGGED", result.suggestedStatus)
    assertEquals(com.example.data.model.ModerationSeverity.HIGH_RISK, result.severity)
    assertEquals(true, result.detectedIssues.any { it.contains("URL link", ignoreCase = true) })
    assertEquals(true, result.detectedIssues.any { it.contains("Phone number", ignoreCase = true) })
  }

  @Test
  fun `verify content moderator passes authentic classical shayari`() {
    val cleanShayari = "हज़ारों ख़्वाहिशें ऐसी कि हर ख़्वाहिश पे दम निकले\nबहुत निकले मिरे अरमान लेकिन फिर भी कम निकले"
    val result = com.example.moderation.ContentModerator.analyze(cleanShayari)
    assertEquals(false, result.isFlagged)
    assertEquals("APPROVED", result.suggestedStatus)
    assertEquals(com.example.data.model.ModerationSeverity.SAFE, result.severity)
    assertEquals(0, result.detectedIssues.size)
  }

  @Test
  fun `verify shayari of the day deterministic picker`() {
    val list = listOf(
      Shayari(id = "s1", lines = "Line 1", author = "Author 1", language = "hindi", emotion = "ishq"),
      Shayari(id = "s2", lines = "Line 2", author = "Author 2", language = "odia", emotion = "dard")
    )
    val pick = com.example.data.repository.ShayariOfTheDayManager.selectDailyShayari(list)
    assertNotNull(pick)
  }
}
