package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.Shayari
import com.example.ui.components.ShayariCard
import com.example.ui.theme.ShayariTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleShayari = Shayari(
      id = "screenshot_1",
      lines = "हज़ारों ख़्वाहिशें ऐसी कि हर ख़्वाहिश पे दम निकले\nबहुत निकले मिरे अरमान लेकिन फिर भी कम निकले",
      author = "Mirza Ghalib",
      penName = "Ghalib",
      language = "hindi",
      emotion = "ishq",
      likesCount = 342,
      isLiked = true,
      isDailyPick = true
    )

    composeTestRule.setContent {
      ShayariTheme(darkTheme = true) {
        ShayariCard(
          shayari = sampleShayari,
          onLikeClick = {},
          onSaveClick = {},
          onReciteClick = { _, _ -> },
          onOpenCardStudio = {},
          onAnalyzeWithGemini = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
