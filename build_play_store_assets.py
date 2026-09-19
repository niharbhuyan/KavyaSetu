#!/usr/bin/env python3
import os
import subprocess

OUT_DIR = "play_store_assets"
RES_DIR = "app/src/main/res/drawable"
os.makedirs(OUT_DIR, exist_ok=True)
os.makedirs(RES_DIR, exist_ok=True)

def run(cmd):
    print("Executing:", cmd[:120], "..." if len(cmd) > 120 else "")
    res = subprocess.run(cmd, shell=True, capture_output=True, text=True)
    if res.returncode != 0:
        print("Error:", res.stderr)
        raise RuntimeError(res.stderr)

print("=== Generating Google Play Store Assets for Kavya Setu ===")

# ==============================================================================
# 1. PLAY STORE 512x512 APP ICON
# ==============================================================================
print("1. Generating 512x512 Play Store App Icon...")
icon_cmd = f"""
convert -size 512x512 xc:'#180829' \\
  \\( -size 512x512 radial-gradient:'#3B124D-#180829' -alpha set -channel A -evaluate set 80% +channel \\) -composite \\
  \\( -size 512x512 xc:none \\
     -stroke '#E5B247' -strokewidth 2.5 -fill none -draw 'circle 256,256 256,36' \\
     -stroke '#FFD700' -strokewidth 1.5 -fill none -draw 'circle 256,256 256,48' \\
     -stroke '#4A1C6D' -strokewidth 1.0 -fill none -draw 'circle 256,256 256,60' \\
     \\
     -stroke '#C5A059' -strokewidth 1.5 -fill '#2A0B3C' \\
     -draw 'roundrectangle 130,320 382,410 20,20' \\
     -stroke '#FFD700' -strokewidth 3.0 -fill none \\
     -draw 'path "M 130,370 Q 256,320 382,370"' \\
     -stroke '#E5B247' -strokewidth 2.0 -fill none \\
     -draw 'path "M 150,375 Q 256,335 362,375"' \\
     -stroke '#FFD700' -strokewidth 2.0 -fill none \\
     -draw 'line 180,360 180,395  line 220,345 220,395  line 256,340 256,395  line 292,345 292,395  line 332,360 332,395' \\
     \\
     -stroke '#8B6B23' -strokewidth 2.0 -fill '#FFFDF5' \\
     -draw 'path "M 160,250 C 210,230 245,240 256,260 C 267,240 302,230 352,250 L 352,320 C 302,300 267,310 256,330 C 245,310 210,300 160,320 Z"' \\
     -stroke '#D4AF37' -strokewidth 1.2 -fill '#F7E7C4' \\
     -draw 'path "M 162,252 C 210,235 245,245 256,263 L 256,330 C 245,312 210,303 162,322 Z"' \\
     \\
     -stroke '#C5A059' -strokewidth 1.5 \\
     -draw 'line 175,270 242,260  line 175,282 242,272  line 175,294 242,284' \\
     -draw 'line 270,260 337,270  line 270,272 337,282  line 270,284 337,294' \\
     \\
     -stroke '#FFE599' -strokewidth 1.5 -fill '#E5B247' \\
     -draw 'path "M 360,110 C 320,130 280,180 240,240 C 230,255 222,270 215,285 L 225,290 C 235,275 250,255 270,230 C 310,180 340,140 365,115 Z"' \\
     -stroke '#FFE599' -strokewidth 1.0 -fill '#FFF0B3' \\
     -draw 'polygon 215,285 205,305 225,290' \\
     -stroke '#FFD700' -strokewidth 1.0 -fill '#D4AF37' \\
     -draw 'line 210,297 220,287' \\
     \\
     -stroke none -fill '#FFE082' \\
     -draw 'polygon 310,150 314,160 324,164 314,168 310,178 306,168 296,164 306,160' \\
     -draw 'polygon 200,190 203,197 210,200 203,203 200,210 197,203 190,200 197,197' \\
     -draw 'polygon 360,220 362,226 368,228 362,230 360,236 358,230 352,228 358,226' \\
  \\) -composite \\
  {OUT_DIR}/play_store_512_icon.png
"""
run(icon_cmd)
run(f"cp {OUT_DIR}/play_store_512_icon.png {RES_DIR}/img_play_store_icon.png")

# ==============================================================================
# 2. PLAY STORE 1024x500 FEATURE GRAPHIC
# ==============================================================================
print("2. Generating 1024x500 Play Store Feature Graphic...")
feature_cmd = f"""
convert -size 1024x500 gradient:'#120622-#2D0B3E' \\
  -stroke '#3D1658' -strokewidth 1.0 -fill none \\
  -draw 'line 50,440 974,440  line 50,60 974,60' \\
  -stroke '#C5A059' -strokewidth 1.5 -fill none \\
  -draw 'circle 210,250 210,120' \\
  -stroke '#FFD700' -strokewidth 2.5 -fill '#1A082A' \\
  -draw 'circle 210,250 210,135' \\
  \\
  -stroke '#C5A059' -strokewidth 2.0 -fill none \\
  -draw 'path "M 130,310 Q 210,260 290,310"' \\
  -stroke '#FFD700' -strokewidth 1.5 -fill none \\
  -draw 'line 160,295 160,320  line 185,285 185,320  line 210,280 210,320  line 235,285 235,320  line 260,295 260,320' \\
  \\
  -stroke '#8B6B23' -strokewidth 1.5 -fill '#FFFDF5' \\
  -draw 'path "M 145,230 C 180,215 200,225 210,240 C 220,225 240,215 275,230 L 275,280 C 240,265 220,275 210,290 C 200,275 180,265 145,280 Z"' \\
  \\
  -stroke '#FFE599' -strokewidth 1.2 -fill '#E5B247' \\
  -draw 'path "M 285,150 C 260,165 235,200 205,245 L 185,280 L 195,282 C 220,235 245,190 290,155 Z"' \\
  \\
  -stroke none -fill '#FFE082' \\
  -draw 'polygon 245,175 248,183 256,186 248,189 245,197 242,189 234,186 242,183' \\
  \\
  -fill '#FFE599' -font DejaVu-Serif-Bold -pointsize 54 \\
  -draw 'text 380,180 "KAVYA SETU"' \\
  \\
  -fill '#E5C07B' -font DejaVu-Sans-Bold -pointsize 26 \\
  -draw 'text 385,230 "काव्य सेतु  •  କାବ୍ୟ ସେତୁ  •  POETRY & SHAYARI"' \\
  \\
  -fill '#D8C3E5' -font DejaVu-Sans -pointsize 20 \\
  -draw 'text 385,275 "Timeless Classical Verses, Audio Recital & Gemini AI Studio"' \\
  \\
  -stroke '#E5B247' -strokewidth 1.5 -fill '#280A38' \\
  -draw 'roundrectangle 385,320 545,365 22,22' \\
  -stroke '#E5B247' -strokewidth 1.5 -fill '#280A38' \\
  -draw 'roundrectangle 560,320 735,365 22,22' \\
  -stroke '#E5B247' -strokewidth 1.5 -fill '#280A38' \\
  -draw 'roundrectangle 750,320 945,365 22,22' \\
  \\
  -stroke '#C5A059' -strokewidth 1.5 -fill '#280A38' \\
  -draw 'roundrectangle 385,380 575,425 22,22' \\
  -stroke '#C5A059' -strokewidth 1.5 -fill '#280A38' \\
  -draw 'roundrectangle 590,380 770,425 22,22' \\
  -stroke '#C5A059' -strokewidth 1.5 -fill '#280A38' \\
  -draw 'roundrectangle 785,380 945,425 22,22' \\
  \\
  -fill '#FFE082' -font DejaVu-Sans-Bold -pointsize 15 \\
  -draw 'text 405,350 "Hindi & Urdu"' \\
  -draw 'text 580,350 "Odia Poetry"' \\
  -draw 'text 770,350 "Tarannum Audio"' \\
  -draw 'text 405,410 "Gemini AI Studio"' \\
  -draw 'text 610,410 "Card Studio"' \\
  -draw 'text 805,410 "Virtual Mehfil"' \\
  {OUT_DIR}/play_store_feature_graphic_1024x500.png
"""
run(feature_cmd)
run(f"cp {OUT_DIR}/play_store_feature_graphic_1024x500.png {RES_DIR}/img_play_store_feature_graphic.png")

# ==============================================================================
# 3. SCREENSHOT 1: HOME FEED & AUDIO RECITER (1080x1920)
# ==============================================================================
print("3. Generating Screenshot 1: Home Feed & Audio Reciter (1080x1920)...")
s1_cmd = f"""
convert -size 1080x1920 gradient:'#140722-#2D0B3E' \\
  \\( -size 1080x1920 xc:none \\
     -fill '#FFF8E7' -font DejaVu-Serif-Bold -pointsize 52 -gravity North -annotate +0+100 "DISCOVER & LISTEN" \\
     -fill '#E5C07B' -font DejaVu-Sans -pointsize 28 -gravity North -annotate +0+175 "Classical & Contemporary Shayari with Audio Recital" \\
     \\
     -stroke '#3D1B54' -strokewidth 8 -fill '#180A28' \\
     -draw 'roundrectangle 90,260 990,1820 60,60' \\
     -stroke '#FFE082' -strokewidth 2 -fill none \\
     -draw 'roundrectangle 94,264 986,1816 56,56' \\
     \\
     -fill '#220C34' -stroke '#381352' -strokewidth 2 \\
     -draw 'roundrectangle 120,290 960,370 20,20' \\
     -fill '#FFE599' -font DejaVu-Serif-Bold -pointsize 32 \\
     -draw 'text 150,345 "Kavya Setu • काव्य सेतु"' \\
     \\
     -stroke '#C5A059' -strokewidth 1.5 -fill '#3A144E' \\
     -draw 'roundrectangle 130,400 240,445 15,15' \\
     -stroke '#4A1D64' -strokewidth 1.5 -fill '#200A30' \\
     -draw 'roundrectangle 260,400 370,445 15,15' \\
     -draw 'roundrectangle 390,400 500,445 15,15' \\
     -draw 'roundrectangle 520,400 630,445 15,15' \\
     -fill '#FFE082' -font DejaVu-Sans-Bold -pointsize 18 \\
     -draw 'text 155,430 "✦ All"' \\
     -fill '#C2B0D2' -font DejaVu-Sans -pointsize 18 \\
     -draw 'text 285,430 "Hindi"' \\
     -draw 'text 415,430 "Urdu"' \\
     -draw 'text 545,430 "Odia"' \\
     \\
     -stroke '#FFD700' -strokewidth 3.0 -fill '#2B0B3C' \\
     -draw 'roundrectangle 130,480 950,970 30,30' \\
     -fill '#FFD700' -font DejaVu-Sans-Bold -pointsize 20 \\
     -draw 'text 160,530 "✨ DAILY PICK OF THE DAY"' \\
     -fill '#E0C080' -font DejaVu-Sans -pointsize 18 \\
     -draw 'text 720,530 "Ghazal • Urdu"' \\
     \\
     -fill '#FFFDF5' -font DejaVu-Serif-Bold -pointsize 30 \\
     -draw 'text 160,610 "हज़ारों ख़्वाहिशें ऐसी कि हर ख़्वाहिश पे दम निकले"' \\
     -draw 'text 160,665 "बहुत निकले मिरे अरमाँ लेकिन फिर भी कम निकले"' \\
     \\
     -fill '#E5C07B' -font DejaVu-Serif -pointsize 22 \\
     -draw 'text 160,735 "Thousands of desires, each breath-taking in longing,"' \\
     -draw 'text 160,775 "Many were fulfilled, yet so many remained..."' \\
     \\
     -fill '#FFD700' -font DejaVu-Sans-Bold -pointsize 24 \\
     -draw 'text 160,845 "— Mirza Asadullah Baig Khan (Ghalib)"' \\
     \\
     -stroke '#E5B247' -strokewidth 2.0 -fill '#54176E' \\
     -draw 'circle 860,890 860,850' \\
     -stroke none -fill '#FFE082' \\
     -draw 'polygon 850,870 850,910 880,890' \\
     -stroke '#FFD700' -strokewidth 2 -fill none \\
     -draw 'line 160,890 800,890' \\
     -stroke none -fill '#FFD700' \\
     -draw 'circle 420,890 420,883' \\
     -fill '#E0C080' -font DejaVu-Sans -pointsize 16 \\
     -draw 'text 160,935 "01:24 / 03:10 • Tarannum Recitation Active 🎵"' \\
     \\
     -stroke '#4A1D64' -strokewidth 2.0 -fill '#200A30' \\
     -draw 'roundrectangle 130,1010 950,1360 25,25' \\
     -fill '#E5C07B' -font DejaVu-Sans-Bold -pointsize 20 \\
     -draw 'text 160,1055 "KAVITA • HINDI CLASSIC"' \\
     -fill '#FFF8E7' -font DejaVu-Serif-Bold -pointsize 28 \\
     -draw 'text 160,1115 "सच है, विपत्ति जब आती है, कायर को ही दहलाती है,"' \\
     -draw 'text 160,1165 "सूरमा नहीं विचलित होते, क्षण एक नहीं धीरज खोते।"' \\
     -fill '#FFE082' -font DejaVu-Sans-Bold -pointsize 22 \\
     -draw 'text 160,1235 "— Ramdhari Singh Dinkar"' \\
     -fill '#C2B0D2' -font DejaVu-Sans -pointsize 18 \\
     -draw 'text 160,1300 "Theme: Courage & Resilience • 248 Likes • 54 Bookmarks"' \\
     \\
     -stroke '#4A1D64' -strokewidth 2.0 -fill '#200A30' \\
     -draw 'roundrectangle 130,1400 950,1720 25,25' \\
     -fill '#E5C07B' -font DejaVu-Sans-Bold -pointsize 20 \\
     -draw 'text 160,1445 "CHHANDA • ODIA HERITAGE"' \\
     -fill '#FFF8E7' -font DejaVu-Serif-Bold -pointsize 26 \\
     -draw 'text 160,1505 "ବନ୍ଦେ ଉତ୍କଳ ଜନନୀ, ଚାରୁ ହାସମୟୀ ଚାରୁ ଭାଷମୟୀ,"' \\
     -draw 'text 160,1555 "ଜନନୀ, ଜନନୀ, ଜନନୀ! ଶୁଭ୍ର ତଟିନୀ କୂଳ ଶୀକର ସମୀରେ..."' \\
     -fill '#FFE082' -font DejaVu-Sans-Bold -pointsize 22 \\
     -draw 'text 160,1625 "— Kantakabi Laxmikanta Mohapatra"' \\
     -fill '#C2B0D2' -font DejaVu-Sans -pointsize 18 \\
     -draw 'text 160,1685 "Theme: Reverence & Homeland • Audio Available 🎧"' \\
  \\) -composite \\
  {OUT_DIR}/screenshot_1_home_feed.png
"""
run(s1_cmd)
run(f"cp {OUT_DIR}/screenshot_1_home_feed.png {RES_DIR}/img_screenshot_feed.png")

# ==============================================================================
# 4. SCREENSHOT 2: GEMINI AI POETRY STUDIO (1080x1920)
# ==============================================================================
print("4. Generating Screenshot 2: Gemini AI Poetry Studio (1080x1920)...")
s2_cmd = f"""
convert -size 1080x1920 gradient:'#140722-#2D0B3E' \\
  \\( -size 1080x1920 xc:none \\
     -fill '#FFF8E7' -font DejaVu-Serif-Bold -pointsize 52 -gravity North -annotate +0+100 "GEMINI AI POETRY STUDIO" \\
     -fill '#E5C07B' -font DejaVu-Sans -pointsize 28 -gravity North -annotate +0+175 "Compose Classical Verses with Real-Time Meter Analysis" \\
     \\
     -stroke '#3D1B54' -strokewidth 8 -fill '#180A28' \\
     -draw 'roundrectangle 90,260 990,1820 60,60' \\
     -stroke '#FFE082' -strokewidth 2 -fill none \\
     -draw 'roundrectangle 94,264 986,1816 56,56' \\
     \\
     -fill '#240C38' -stroke '#381352' -strokewidth 2 \\
     -draw 'roundrectangle 120,290 960,370 20,20' \\
     -fill '#FFE599' -font DejaVu-Serif-Bold -pointsize 30 \\
     -draw 'text 150,345 "✨ AI Verse Studio • कविकर्म AI"' \\
     \\
     -fill '#FFF8E7' -font DejaVu-Sans-Bold -pointsize 22 \\
     -draw 'text 130,420 "Select Poetic Form / विधा चयन:"' \\
     -stroke '#FFD700' -strokewidth 2.0 -fill '#4E1458' \\
     -draw 'roundrectangle 130,445 285,495 20,20' \\
     -stroke '#4A1D64' -strokewidth 1.5 -fill '#200A30' \\
     -draw 'roundrectangle 300,445 440,495 20,20' \\
     -draw 'roundrectangle 455,445 595,495 20,20' \\
     -draw 'roundrectangle 610,445 750,495 20,20' \\
     -draw 'roundrectangle 765,445 920,495 20,20' \\
     -fill '#FFE082' -font DejaVu-Sans-Bold -pointsize 18 \\
     -draw 'text 155,478 "Ghazal (ग़ज़ल)"' \\
     -fill '#C2B0D2' -font DejaVu-Sans -pointsize 18 \\
     -draw 'text 330,478 "Nazm"' \\
     -draw 'text 485,478 "Haiku"' \\
     -draw 'text 640,478 "Doha"' \\
     -draw 'text 790,478 "Chhanda"' \\
     \\
     -fill '#FFF8E7' -font DejaVu-Sans-Bold -pointsize 22 \\
     -draw 'text 130,545 "Theme / भाव & कल्पना:"' \\
     -stroke '#4A1D64' -strokewidth 2.0 -fill '#200A30' \\
     -draw 'roundrectangle 130,570 950,680 20,20' \\
     -fill '#FFF8E7' -font DejaVu-Serif -pointsize 24 \\
     -draw 'text 160,620 "Monsoon rain on old stone arches and lingering memories..."' \\
     -fill '#8F7B9D' -font DejaVu-Sans -pointsize 18 \\
     -draw 'text 160,660 "Style: Classical Ghalibian • Language: Hindi / Urdu"' \\
     \\
     -stroke '#FFD700' -strokewidth 2.0 -fill '#661D6E' \\
     -draw 'roundrectangle 130,710 950,780 25,25' \\
     -fill '#FFF8E7' -font DejaVu-Sans-Bold -pointsize 24 \\
     -draw 'text 420,755 "✨ Compose with Gemini AI"' \\
     \\
     -stroke '#FFD700' -strokewidth 2.5 -fill '#2A0B3C' \\
     -draw 'roundrectangle 130,820 950,1480 30,30' \\
     -fill '#FFD700' -font DejaVu-Sans-Bold -pointsize 20 \\
     -draw 'text 160,875 "✦ AI GENERATED MASTERPIECE • GHAZAL"' \\
     \\
     -fill '#FFFDF5' -font DejaVu-Serif-Bold -pointsize 32 \\
     -draw 'text 160,955 "भीगी हुई रातें जो पुरानी याद लाती हैं,"' \\
     -draw 'text 160,1015 "दीवारों से कुछ अनकही बात लाती हैं।"' \\
     -draw 'text 160,1095 "काग़ज़ पे उतर आती है सदियों की उदासी,"' \\
     -draw 'text 160,1155 "जब बूँदें ख़यालों की बारात लाती हैं।"' \\
     \\
     -stroke '#4A1D64' -strokewidth 1.5 -fill '#1A0826' \\
     -draw 'roundrectangle 160,1220 920,1380 20,20' \\
     -fill '#FFE599' -font DejaVu-Sans-Bold -pointsize 20 \\
     -draw 'text 185,1260 "Metrical Analysis & Form:"' \\
     -fill '#D8C3E5' -font DejaVu-Sans -pointsize 18 \\
     -draw 'text 185,1300 "• Bahr: Hazaj Musamman Salim (Mafaa-eelun Mafaa-eelun)"' \\
     -draw 'text 185,1335 "• Radif (रदीफ़): लाती हैं  •  Kafiya (क़ाफ़िया): पुरानी, अनकही, बारात"' \\
     -draw 'text 185,1365 "• Matla & Makhta balance: Perfect Classical Meter"' \\
     \\
     -stroke '#E5B247' -strokewidth 2.0 -fill '#4E1458' \\
     -draw 'roundrectangle 160,1410 420,1460 20,20' \\
     -stroke '#E5B247' -strokewidth 2.0 -fill '#4E1458' \\
     -draw 'roundrectangle 440,1410 680,1460 20,20' \\
     -stroke '#E5B247' -strokewidth 2.0 -fill '#4E1458' \\
     -draw 'roundrectangle 700,1410 920,1460 20,20' \\
     -fill '#FFE082' -font DejaVu-Sans-Bold -pointsize 18 \\
     -draw 'text 210,1442 "🔊 Listen Audio"' \\
     -draw 'text 490,1442 "📋 Copy Verse"' \\
     -draw 'text 750,1442 "🎨 Create Card"' \\
     \\
     -stroke '#381352' -strokewidth 2.0 -fill '#200A30' \\
     -draw 'roundrectangle 130,1515 950,1750 25,25' \\
     -fill '#E5C07B' -font DejaVu-Sans-Bold -pointsize 20 \\
     -draw 'text 160,1560 "📚 Recent AI Compositions History (14 Saved)"' \\
     -fill '#FFF8E7' -font DejaVu-Serif -pointsize 22 \\
     -draw 'text 160,1615 "• Nazm: खामोशियों का दरिया (River of Silences)"' \\
     -draw 'text 160,1660 "• Haiku: ଗ୍ରୀଷ୍ମ ଝଡ଼ ପରେ (After the Summer Gale - Odia)"' \\
     -draw 'text 160,1705 "• Rubaiyat: पैमाने में चाँदनी (Moonlight in the Goblet)"' \\
  \\) -composite \\
  {OUT_DIR}/screenshot_2_ai_composer.png
"""
run(s2_cmd)
run(f"cp {OUT_DIR}/screenshot_2_ai_composer.png {RES_DIR}/img_screenshot_composer.png")

# ==============================================================================
# 5. SCREENSHOT 3: CALLIGRAPHY CARD STUDIO & VAULT (1080x1920)
# ==============================================================================
print("5. Generating Screenshot 3: Calligraphy Card Studio & Vault (1080x1920)...")
s3_cmd = f"""
convert -size 1080x1920 gradient:'#140722-#2D0B3E' \\
  \\( -size 1080x1920 xc:none \\
     -fill '#FFF8E7' -font DejaVu-Serif-Bold -pointsize 52 -gravity North -annotate +0+100 "CALLIGRAPHY CARD STUDIO" \\
     -fill '#E5C07B' -font DejaVu-Sans -pointsize 28 -gravity North -annotate +0+175 "Design Elegant Poetry Cards & Track Reading Streaks" \\
     \\
     -stroke '#3D1B54' -strokewidth 8 -fill '#180A28' \\
     -draw 'roundrectangle 90,260 990,1820 60,60' \\
     -stroke '#FFE082' -strokewidth 2 -fill none \\
     -draw 'roundrectangle 94,264 986,1816 56,56' \\
     \\
     -fill '#240C38' -stroke '#381352' -strokewidth 2 \\
     -draw 'roundrectangle 120,290 960,370 20,20' \\
     -fill '#FFE599' -font DejaVu-Serif-Bold -pointsize 30 \\
     -draw 'text 150,345 "🎨 Card Studio & Offline Vault"' \\
     \\
     -stroke '#E5B247' -strokewidth 2.0 -fill '#280A38' \\
     -draw 'roundrectangle 130,395 950,545 25,25' \\
     -fill '#FFD700' -font DejaVu-Sans-Bold -pointsize 22 \\
     -draw 'text 160,440 "📖 Reading Progress & Habits"' \\
     -fill '#FFF8E7' -font DejaVu-Sans -pointsize 20 \\
     -draw 'text 160,480 "🔥 7-Day Reading Streak  •  45 Couplets Read This Week"' \\
     -stroke '#4A1D64' -strokewidth 8 -fill none \\
     -draw 'line 160,515 920,515' \\
     -stroke '#FFD700' -strokewidth 8 -fill none \\
     -draw 'line 160,515 750,515' \\
     \\
     -fill '#FFF8E7' -font DejaVu-Sans-Bold -pointsize 22 \\
     -draw 'text 130,590 "Interactive Calligraphy Card Preview:"' \\
     \\
     -stroke '#D4AF37' -strokewidth 4.0 -fill '#FAF6EE' \\
     -draw 'roundrectangle 160,620 920,1180 30,30' \\
     -stroke '#C5A059' -strokewidth 1.5 -fill none \\
     -draw 'roundrectangle 175,635 905,1165 24,24' \\
     -stroke '#E5C07B' -strokewidth 1.0 -fill none \\
     -draw 'circle 195,655 195,640  circle 885,655 885,640  circle 195,1145 195,1130  circle 885,1145 885,1130' \\
     \\
     -fill '#8B6508' -font DejaVu-Sans-Bold -pointsize 18 -gravity Center -annotate +0-270 "✦  काव्य सेतु  •  KAVYA SETU  ✦" \\
     \\
     -fill '#1F1608' -font DejaVu-Serif-Bold -pointsize 36 -gravity Center -annotate +0-160 "सितारों से आगे जहाँ और भी हैं," \\
     -fill '#1F1608' -font DejaVu-Serif-Bold -pointsize 36 -gravity Center -annotate +0-95 "अभी इश्क़ के इम्तिहाँ और भी हैं।" \\
     \\
     -fill '#7A5A18' -font DejaVu-Serif -pointsize 24 -gravity Center -annotate +0-20 "Beyond the stars, worlds yet uncharted lie," \\
     -fill '#7A5A18' -font DejaVu-Serif -pointsize 24 -gravity Center -annotate +0+25 "More trials of devotion await the sky." \\
     \\
     -fill '#8B6508' -font DejaVu-Sans-Bold -pointsize 24 -gravity Center -annotate +0+105 "— Allama Sir Muhammad Iqbal" \\
     -fill '#A88538' -font DejaVu-Sans -pointsize 18 -gravity Center -annotate +0+155 "Ball-e-Jibreel • Classic Urdu Shayari" \\
     \\
     -fill '#FFF8E7' -font DejaVu-Sans-Bold -pointsize 22 \\
     -draw 'text 130,1235 "Theme & Aesthetic Palette:"' \\
     -stroke '#FFD700' -strokewidth 3.0 -fill '#FAF6EE' \\
     -draw 'circle 200,1285 200,1255' \\
     -stroke '#4A1D64' -strokewidth 2.0 -fill '#1A0828' \\
     -draw 'circle 280,1285 280,1255' \\
     -stroke '#4A1D64' -strokewidth 2.0 -fill '#0A1E1E' \\
     -draw 'circle 360,1285 360,1255' \\
     -stroke '#4A1D64' -strokewidth 2.0 -fill '#2A0818' \\
     -draw 'circle 440,1285 440,1255' \\
     \\
     -stroke '#FFD700' -strokewidth 2.0 -fill '#661D6E' \\
     -draw 'roundrectangle 130,1340 520,1420 25,25' \\
     -stroke '#FFD700' -strokewidth 2.0 -fill '#3A144E' \\
     -draw 'roundrectangle 560,1340 950,1420 25,25' \\
     -fill '#FFE082' -font DejaVu-Sans-Bold -pointsize 22 \\
     -draw 'text 210,1388 "💾 Export Card (PNG)"' \\
     -draw 'text 660,1388 "📤 Share to Story"' \\
     \\
     -stroke '#381352' -strokewidth 2.0 -fill '#200A30' \\
     -draw 'roundrectangle 130,1460 950,1750 25,25' \\
     -fill '#E5C07B' -font DejaVu-Sans-Bold -pointsize 20 \\
     -draw 'text 160,1505 "🔖 Saved Offline Vault & Bookmarks (32 Verses)"' \\
     -fill '#FFF8E7' -font DejaVu-Serif -pointsize 22 \\
     -draw 'text 160,1560 "• Last Read Bookmark: Mirza Ghalib — Couplet #12"' \\
     -draw 'text 160,1610 "• Collection: Monsoon Melancholy (12 couplets)"' \\
     -draw 'text 160,1660 "• Collection: Odia Bhakti & Heritage (8 verses)"' \\
     -draw 'text 160,1710 "• Collection: Sufi Ghazals of Rumi & Khusrau (12 verses)"' \\
  \\) -composite \\
  {OUT_DIR}/screenshot_3_card_studio.png
"""
run(s3_cmd)
run(f"cp {OUT_DIR}/screenshot_3_card_studio.png {RES_DIR}/img_screenshot_studio.png")

# ==============================================================================
# 6. SCREENSHOT 4: VIRTUAL MEHFIL & MUSHAIRA (1080x1920)
# ==============================================================================
print("6. Generating Screenshot 4: Virtual Mehfil & Mushaira (1080x1920)...")
s4_cmd = f"""
convert -size 1080x1920 gradient:'#140722-#2D0B3E' \\
  \\( -size 1080x1920 xc:none \\
     -fill '#FFF8E7' -font DejaVu-Serif-Bold -pointsize 52 -gravity North -annotate +0+100 "VIRTUAL MEHFIL & MUSHAIRA" \\
     -fill '#E5C07B' -font DejaVu-Sans -pointsize 28 -gravity North -annotate +0+175 "Live Poetic Gatherings, Wah-Wah Reactions & Sitar Audio" \\
     \\
     -stroke '#3D1B54' -strokewidth 8 -fill '#180A28' \\
     -draw 'roundrectangle 90,260 990,1820 60,60' \\
     -stroke '#FFE082' -strokewidth 2 -fill none \\
     -draw 'roundrectangle 94,264 986,1816 56,56' \\
     \\
     -fill '#240C38' -stroke '#381352' -strokewidth 2 \\
     -draw 'roundrectangle 120,290 960,370 20,20' \\
     -fill '#FFE599' -font DejaVu-Serif-Bold -pointsize 30 \\
     -draw 'text 150,345 "🎭 Virtual Mehfil • बज़्म-ए-शायरी"' \\
     \\
     -stroke '#FFD700' -strokewidth 2.5 -fill '#2D0A35' \\
     -draw 'roundrectangle 130,400 950,900 30,30' \\
     -fill '#FFD700' -font DejaVu-Sans-Bold -pointsize 20 \\
     -draw 'text 160,450 "🔴 LIVE POETIC RECITATION ON STAGE"' \\
     -fill '#E0C080' -font DejaVu-Sans -pointsize 18 \\
     -draw 'text 720,450 "Audience: 1,420 👥"' \\
     \\
     -fill '#FFE082' -font DejaVu-Serif-Bold -pointsize 32 \\
     -draw 'text 160,540 "तू पहले बात फिर बात का मफ़हूम पैदा कर,"' \\
     -draw 'text 160,600 "ज़बाँ से जब पुकारे तो नया महफ़ूम पैदा कर!"' \\
     -fill '#FFF8E7' -font DejaVu-Serif -pointsize 24 \\
     -draw 'text 160,670 "First give birth to the word, then forge its deeper soul,"' \\
     -draw 'text 160,715 "When your voice cries out, let a new meaning unfold!"' \\
     \\
     -fill '#FFE082' -font DejaVu-Sans-Bold -pointsize 22 \\
     -draw 'text 160,785 "— Poet: Ustad Dagh Dehlvi"' \\
     -fill '#C2B0D2' -font DejaVu-Sans -pointsize 18 \\
     -draw 'text 160,845 "🎵 Ambient Sitar & Tanpura Soundscape: Playing (Malkauns)"' \\
     \\
     -fill '#FFF8E7' -font DejaVu-Sans-Bold -pointsize 22 \\
     -draw 'text 130,950 "Express Appreciation / दाद & वाह-वाह:"' \\
     \\
     -stroke '#E5B247' -strokewidth 2.0 -fill '#4E1458' \\
     -draw 'roundrectangle 130,985 380,1055 25,25' \\
     -stroke '#E5B247' -strokewidth 2.0 -fill '#4E1458' \\
     -draw 'roundrectangle 415,985 665,1055 25,25' \\
     -stroke '#E5B247' -strokewidth 2.0 -fill '#4E1458' \\
     -draw 'roundrectangle 700,985 950,1055 25,25' \\
     \\
     -fill '#FFE082' -font DejaVu-Sans-Bold -pointsize 20 \\
     -draw 'text 165,1028 "👏 Wah-Wah! (वाह)"' \\
     -draw 'text 445,1028 "❤️ Mukarrar (मुकर्रर)"' \\
     -draw 'text 725,1028 "✨ Subhanallah (लाजवाब)"' \\
     \\
     -stroke '#381352' -strokewidth 2.0 -fill '#200A30' \\
     -draw 'roundrectangle 130,1095 950,1460 25,25' \\
     -fill '#FFD700' -font DejaVu-Sans-Bold -pointsize 22 \\
     -draw 'text 160,1145 "⚔️ Sher-baazi (Poetic Rhyming Duel)"' \\
     -fill '#FFF8E7' -font DejaVu-Sans -pointsize 18 \\
     -draw 'text 160,1190 "Opponent: Kavi Saurabh  •  Last Letter: न (N)"' \\
     \\
     -stroke '#FFD700' -strokewidth 1.5 -fill '#2D0A35' \\
     -draw 'roundrectangle 160,1225 920,1335 20,20' \\
     -fill '#FFE082' -font DejaVu-Serif -pointsize 24 \\
     -draw 'text 185,1275 "नफ़रत की ज़मीं पर मुहब्बत के गुल खिलाना है,"' \\
     -draw 'text 185,1315 "यही पैग़ाम हमारा इस ज़माने को बताना है।"' \\
     -fill '#C2B0D2' -font DejaVu-Sans -pointsize 18 \\
     -draw 'text 160,1380 "Turn Next: Your turn to rhyme with letter ह (H)"' \\
     -fill '#E5C07B' -font DejaVu-Sans-Bold -pointsize 18 \\
     -draw 'text 160,1420 "Status: Suggesting matching couplets from Ghalib & Dinkar ⚡"' \\
     \\
     -stroke '#FFD700' -strokewidth 2.0 -fill '#661D6E' \\
     -draw 'roundrectangle 130,1500 950,1580 25,25' \\
     -fill '#FFF8E7' -font DejaVu-Sans-Bold -pointsize 22 \\
     -draw 'text 360,1548 "🎤 Recite Your Couplet (Microphone Active)"' \\
     \\
     -stroke '#381352' -strokewidth 2.0 -fill '#200A30' \\
     -draw 'roundrectangle 130,1615 950,1750 25,25' \\
     -fill '#E5C07B' -font DejaVu-Sans-Bold -pointsize 20 \\
     -draw 'text 160,1660 "📜 Upcoming Mehfils Schedule"' \\
     -fill '#FFF8E7' -font DejaVu-Sans -pointsize 18 \\
     -draw 'text 160,1700 "• Ghalib Centenary Mushaira — Tonight 8:00 PM IST"' \\
     -draw 'text 160,1735 "• Odia Chhanda Sanjh — Tomorrow 7:00 PM IST"' \\
  \\) -composite \\
  {OUT_DIR}/screenshot_4_virtual_mehfil.png
"""
run(s4_cmd)
run(f"cp {OUT_DIR}/screenshot_4_virtual_mehfil.png {RES_DIR}/img_screenshot_mehfil.png")

print("=== Finished Generating All Google Play Store Visual Assets ===")
