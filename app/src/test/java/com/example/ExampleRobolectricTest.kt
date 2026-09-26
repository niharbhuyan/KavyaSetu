package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Emotion
import com.example.data.model.Language
import com.example.data.model.Shayari
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
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
    assertEquals("Kavya Setu", appName)
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

  @Test
  fun `verify LafzOMaani lexicon root lookups`() {
    val entries = com.example.data.repository.LafzOMaaniData.vocabulary
    assertNotNull(entries)
    assertEquals(true, entries.size >= 8)

    val hijrEntry = com.example.data.repository.LafzOMaaniData.search("Hijr").firstOrNull()
    assertNotNull(hijrEntry)
    assertEquals(true, hijrEntry?.rootDerivation?.contains("H-J-R") == true)

    val matches = com.example.data.repository.LafzOMaaniData.findMatchingWordsInText("शब-ए-विसाल बहुत कम है आसमाँ के लिए")
    assertEquals(true, matches.isNotEmpty())
  }

  @Test
  fun `verify Ustaad Mentor personas critique generation`() {
    val personas = com.example.data.repository.UstaadMentorRepository.personas
    assertEquals(true, personas.size >= 4)

    val ghalib = personas.first { it.id == com.example.data.model.UstaadId.GHALIB }
    val critique = com.example.data.repository.UstaadMentorRepository.generateOfflineCritique(
      persona = ghalib,
      coupletText = "Tere aane ki khabar sun ke bahar aayi hai"
    )
    assertNotNull(critique)
    assertEquals(true, critique.persona.name.contains("Ghalib"))
    assertEquals(true, critique.critique.isNotBlank())
    assertEquals(true, critique.wordReplacements.isNotEmpty())
  }

  @Test
  fun `verify Tarannum Ragas and swara frequencies`() {
    val ragas = com.example.data.model.RagaTarannum.entries
    assertEquals(true, ragas.size >= 4)

    val yaman = com.example.data.model.RagaTarannum.YAMAN
    assertEquals("Raag Yaman", yaman.ragaName)
    assertEquals(true, yaman.scaleSwaras.isNotEmpty())
    assertEquals(true, yaman.scaleFrequencies.isNotEmpty())
    assertEquals(138.59, yaman.rootPitchHz, 0.1)
  }

  @Test
  fun `verify poem category enum and emoji mappings`() {
    val love = com.example.data.model.PoemCategory.LOVE
    assertEquals("Love", love.displayName)
    assertEquals("❤️", love.emoji)

    val nature = com.example.data.model.PoemCategory.NATURE
    assertEquals("Nature", nature.displayName)
    assertEquals("🍃", nature.emoji)

    val sorrow = com.example.data.model.PoemCategory.SORROW
    assertEquals("Sorrow", sorrow.displayName)
    assertEquals("🥀", sorrow.emoji)

    val inspiration = com.example.data.model.PoemCategory.INSPIRATION
    assertEquals("Inspiration", inspiration.displayName)
    assertEquals("🦅", inspiration.emoji)

    assertEquals(com.example.data.model.PoemCategory.LOVE, com.example.data.model.PoemCategory.fromId("love"))
    assertEquals(com.example.data.model.PoemCategory.NATURE, com.example.data.model.PoemCategory.fromId("nature"))
  }

  @Test
  fun `verify poetry display preferences persistence`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    com.example.data.local.PoetryDisplayPreferences.saveSettings(
      context = context,
      fontSizeSp = 24f,
      lineHeightMult = 1.8f,
      fontFamilyType = "serif"
    )

    assertEquals(24f, com.example.data.local.PoetryDisplayPreferences.getFontSizeSp(context), 0.01f)
    assertEquals(1.8f, com.example.data.local.PoetryDisplayPreferences.getLineHeightMult(context), 0.01f)
    assertEquals("serif", com.example.data.local.PoetryDisplayPreferences.getFontFamilyType(context))
  }

  @Test
  fun `verify Daily Pick widget string resources`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val widgetName = context.getString(R.string.widget_name)
    val widgetDesc = context.getString(R.string.widget_description)
    val widgetHeader = context.getString(R.string.widget_header_title)

    assertEquals("Daily Pick", widgetName)
    assertEquals("Displays a featured poem from the collection, updating once every 24 hours.", widgetDesc)
    assertEquals("✨ Daily Pick", widgetHeader)
  }

  @Test
  fun `verify Daily Pick 24-hour cycle date rotation and offset`() {
    val collection = listOf(
      Shayari(id = "p1", lines = "Poem 1", author = "Poet A", language = "hindi", emotion = "ishq"),
      Shayari(id = "p2", lines = "Poem 2", author = "Poet B", language = "odia", emotion = "dard"),
      Shayari(id = "p3", lines = "Poem 3", author = "Poet C", language = "english", emotion = "sukoon")
    )

    val day1Pick = com.example.data.repository.ShayariOfTheDayManager.selectDailyShayari(collection, dateKey = "20260918", offset = 0)
    val day1SameDay = com.example.data.repository.ShayariOfTheDayManager.selectDailyShayari(collection, dateKey = "20260918", offset = 0)
    val day1OffsetPick = com.example.data.repository.ShayariOfTheDayManager.selectDailyShayari(collection, dateKey = "20260918", offset = 1)

    assertNotNull(day1Pick)
    assertNotNull(day1SameDay)
    assertNotNull(day1OffsetPick)

    // Within the same 24-hour period, deterministic selection remains identical
    assertEquals(day1Pick?.id, day1SameDay?.id)
    assertEquals(true, day1Pick?.isDailyPick)

    // Offset rotates through the collection
    assertEquals(true, day1Pick?.id != day1OffsetPick?.id)
  }

  @Test
  fun `verify social share intent creation with ACTION_SEND`() {
    val sampleText = "✨ Dil se jo baat nikalti hai asar rakhti hai\n— Allama Iqbal"
    val subject = "Verse by Allama Iqbal"

    val intent = com.example.util.SocialShareHelper.createShareIntent(sampleText, subject)

    assertEquals(android.content.Intent.ACTION_SEND, intent.action)
    assertEquals("text/plain", intent.type)
    assertEquals(sampleText, intent.getStringExtra(android.content.Intent.EXTRA_TEXT))
    assertEquals(subject, intent.getStringExtra(android.content.Intent.EXTRA_SUBJECT))
  }

  @Test
  fun `verify poem format for social media sharing`() {
    val formatted = com.example.util.SocialShareHelper.formatPoemForSocial(
      lines = "Hazaron khwahishen aisi ke har khwahish pe dam nikle",
      author = "Mirza Ghalib",
      language = "hindi",
      emotion = "ishq",
      category = "love",
      penName = "Ghalib",
      id = "ghalib_123"
    )

    assertTrue(formatted.contains("Hazaron khwahishen aisi"))
    assertTrue(formatted.contains("Mirza Ghalib"))
    assertTrue(formatted.contains("#HindiShayari"))
    assertTrue(formatted.contains("#KavyaSetu"))
    assertTrue(formatted.contains("shayari://detail?id=ghalib_123"))
  }

  @Test
  fun `verify Gemini AI shayari format for social media sharing`() {
    val formatted = com.example.util.SocialShareHelper.formatGeminiPoemForSocial(
      lines = "सितारों से आगे जहाँ और भी हैं\nअभी इश्क़ के इम्तिहाँ और भी हैं",
      emotion = "inspiration",
      language = "hindi",
      author = "Sahir",
      topic = "Cosmic journey and resilience"
    )

    assertTrue(formatted.contains("सितारों से आगे जहाँ और भी हैं"))
    assertTrue(formatted.contains("Sahir"))
    assertTrue(formatted.contains("Composed with Gemini AI on Kavya Setu"))
    assertTrue(formatted.contains("Cosmic journey and resilience"))
    assertTrue(formatted.contains("#GeminiAI"))
    assertTrue(formatted.contains("#AIShayari"))
  }

  @Test
  fun `verify reading metrics for standard couplet`() {
    val couplet = "हज़ारों ख़्वाहिशें ऐसी कि हर ख़्वाहिश पे दम निकले\nबहुत निकले मिरे अरमान लेकिन फिर भी कम निकले"
    val metrics = com.example.ui.components.calculatePoemReadingMetrics(couplet, "Thousands of desires, each worth dying for.")

    assertEquals(2, metrics.lineCount)
    assertEquals(1, metrics.coupletCount)
    assertEquals(false, metrics.isLongNazmOrGhazal)
    assertTrue(metrics.wordCount > 0)
    assertTrue(metrics.estimatedSeconds >= 25)
    assertTrue(metrics.formattedReadTime.contains("read"))
  }

  @Test
  fun `verify reading metrics for long nazm or ghazal`() {
    val longNazm = """
      बोल कि लब आज़ाद हैं तेरे
      बोल ज़बाँ अब तक तेरी है
      तेरा सुतवाँ जिस्म है तेरा
      बोल कि जाँ अब तक तेरी है

      देख कि शीशगर की दूकां में
      तुंद हैं शो'ले सुर्ख़ है आहिन
      खुलने लगे क़ुफ़्लों के दहाने
      फैला हर एक ज़ंजीर का दामन
    """.trimIndent()

    val translation = "Speak, for your lips are free; speak, for your tongue is still your own; your upright body is still yours."
    val metrics = com.example.ui.components.calculatePoemReadingMetrics(longNazm, translation)

    assertEquals(8, metrics.lineCount)
    assertEquals(4, metrics.coupletCount)
    assertEquals(true, metrics.isLongNazmOrGhazal)
    assertTrue(metrics.estimatedSeconds > 40)
    assertTrue(metrics.formattedReadTime.contains("min read") || metrics.formattedReadTime.contains("sec read"))
  }

  @Test
  fun `verify reading metrics for multi-stanza long ghazal`() {
    val multiStanzaGhazal = """
      ख़ुदी को कर बुलंद इतना कि हर तक़दीर से पहले
      ख़ुदा बंदे से ख़ुद पूछे बता तेरी रज़ा क्या है

      सितारों से आगे जहाँ और भी हैं
      अभी इश्क़ के इम्तिहाँ और भी हैं

      तू शाहीं है परवाज़ है काम तेरा
      तेरे सामने आसमाँ और भी हैं
    """.trimIndent()

    val metrics = com.example.ui.components.calculatePoemReadingMetrics(
      poemText = multiStanzaGhazal,
      translationText = "Elevate your selfhood to such heights that before decreeing your destiny, God Himself shall ask: Tell me, what is your desire?"
    )

    assertEquals(6, metrics.lineCount)
    assertEquals(3, metrics.coupletCount)
    assertTrue(metrics.isLongNazmOrGhazal)
    assertTrue(metrics.wordCount >= 40)
    assertTrue(metrics.estimatedSeconds >= 50)
  }

  @Test
  fun `verify poem reader native share intent generation`() {
    val shayari = com.example.data.model.Shayari(
      id = "sh_ghalib_ishq",
      lines = "इश्क़ ने 'ग़ालिब' निकम्मा कर दिया\nवर्ना हम भी आदमी थे काम के",
      translationEnglish = "Love has made me worthless, Ghalib; otherwise, I too was once a capable man.",
      author = "Mirza Ghalib",
      penName = "Ghalib",
      language = "hindi",
      emotion = "ishq"
    )

    val shareText = com.example.util.SocialShareHelper.formatPoemForSocial(
      lines = shayari.lines,
      author = shayari.author,
      language = shayari.language,
      emotion = shayari.emotion,
      category = shayari.category,
      penName = shayari.penName,
      id = shayari.id
    )

    val shareIntent = com.example.util.SocialShareHelper.createShareIntent(
      text = shareText,
      subject = "Poetic Verse from Mirza Ghalib — Kavya Setu"
    )

    assertEquals(android.content.Intent.ACTION_SEND, shareIntent.action)
    assertEquals("text/plain", shareIntent.type)
    assertTrue(shareIntent.getStringExtra(android.content.Intent.EXTRA_TEXT)!!.contains("इश्क़ ने 'ग़ालिब' निकम्मा कर दिया"))
    assertTrue(shareIntent.getStringExtra(android.content.Intent.EXTRA_TEXT)!!.contains("Mirza Ghalib"))
    assertTrue(shareIntent.getStringExtra(android.content.Intent.EXTRA_TEXT)!!.contains("#KavyaSetu"))
    assertEquals("Poetic Verse from Mirza Ghalib — Kavya Setu", shareIntent.getStringExtra(android.content.Intent.EXTRA_SUBJECT))
  }

  @Test
  fun `verify poem reader copy to clipboard functionality`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val shayari = com.example.data.model.Shayari(
      id = "sh_mir_patta",
      lines = "पत्ता पत्ता बूटा बूटा हाल हमारा जाने है\nजाने न जाने गुल ही न जाने बाग़ तो सारा जाने है",
      translationEnglish = "Every leaf and every bud knows of my state; only the rose remains oblivious.",
      author = "Mir Taqi Mir",
      penName = "Mir",
      language = "hindi",
      emotion = "ishq"
    )

    com.example.util.SocialShareHelper.copyPoem(context, shayari)

    val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    val clip = clipboard.primaryClip
    assertNotNull(clip)
    assertTrue(clip!!.itemCount > 0)
    val copiedText = clip.getItemAt(0).text.toString()

    assertTrue(copiedText.contains("पत्ता पत्ता बूटा बूटा"))
    assertTrue(copiedText.contains("Mir Taqi Mir"))
    assertTrue(copiedText.contains("Every leaf and every bud knows of my state"))
    assertTrue(copiedText.contains("#KavyaSetu"))
  }

  @Test
  fun `verify auto update background manager configuration`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    com.example.sync.HourlySyncManager.scheduleHourlySync(context)
    val alarmManager = context.getSystemService(android.content.Context.ALARM_SERVICE) as android.app.AlarmManager
    assertNotNull(alarmManager)
  }
}
