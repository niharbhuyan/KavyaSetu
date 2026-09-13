package com.example

import com.example.data.model.PromptLibraryData
import com.example.data.model.PromptMood
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun promptLibrary_containsAllMoodsAndValidData() {
    val allPrompts = PromptLibraryData.prompts
    assertTrue("Should have at least 20 prompts in library", allPrompts.size >= 20)

    // Verify all moods are populated
    for (mood in PromptMood.entries) {
      val promptsForMood = PromptLibraryData.getPromptsByMood(mood)
      assertTrue("Mood ${mood.name} should have prompts", promptsForMood.isNotEmpty())
      promptsForMood.forEach { prompt ->
        assertTrue("Prompt title must not be blank", prompt.title.isNotBlank())
        assertTrue("Prompt text must not be blank", prompt.promptText.isNotBlank())
        assertEquals(mood, prompt.mood)
        assertEquals(mood.emotionCode, prompt.emotionCode)
      }
    }

    // Verify search filtering
    val rainResults = PromptLibraryData.searchPrompts("rain", null)
    assertTrue("Search for 'rain' should find matching prompts", rainResults.isNotEmpty())

    val loveRainResults = PromptLibraryData.searchPrompts("rain", PromptMood.LOVE)
    assertTrue("Search for 'rain' in LOVE mood should return results", loveRainResults.isNotEmpty())
    assertEquals(PromptMood.LOVE, loveRainResults.first().mood)
  }
}
