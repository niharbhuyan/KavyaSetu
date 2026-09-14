package com.example.data.repository

import com.example.data.model.UstaadCritiqueResult
import com.example.data.model.UstaadId
import com.example.data.model.UstaadPersona
import com.example.data.model.WordIslah
import com.example.data.remote.GeminiClient
import com.example.data.remote.GeminiContent
import com.example.data.remote.GeminiGenerationConfig
import com.example.data.remote.GeminiPart
import com.example.data.remote.GeminiRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

object UstaadMentorRepository {

    val personas: List<UstaadPersona> = listOf(
        UstaadPersona(
            id = UstaadId.GHALIB,
            name = "Mirza Asadullah Khan Ghalib",
            nativeName = "مرزا غالب • मिर्ज़ा ग़ालिब",
            eraAndOrigin = "1797–1869 • Mughal Delhi",
            title = "Khuda-e-Sukhan • Master of Paradox & Philosophy",
            emblem = "📜",
            signatureQuote = "हम को मा'लूम है जन्नत की हक़ीक़त लेकिन,\nदिल के ख़ुश रखने को 'ग़ालिब' ये ख़याल अच्छा है।",
            signatureStyle = "Complex Persianate metaphors, philosophical paradox (Falsafa), subtle irony, and effortless grandeur.",
            greetingSalutation = "ब-रस्म-ए-अदब! बरख़ुरदार, तुम्हारा कलाम मेरी नज़र से गुज़रा...",
            promptSystemPersona = "You are the historic poet Mirza Asadullah Khan Ghalib of Shahjahanabad, Delhi. You review the user's poetic couplet with deep affection, subtle wit, and uncompromising literary standards. You love intricate metaphors (tashbeeh), elevate mundane words into metaphysical paradoxes, and always suggest polished replacements."
        ),
        UstaadPersona(
            id = UstaadId.FAIZ,
            name = "Faiz Ahmad Faiz",
            nativeName = "فیض احمد فیض • फ़ैज़ अहमद फ़ैज़",
            eraAndOrigin = "1911–1984 • Lahore / Sialkot",
            title = "Voice of Conscience • Romance & Revolution",
            emblem = "🕊️",
            signatureQuote = "मुझ से पहली सी मोहब्बत मिरी महबूब न माँग,\nमैंने समझा था कि तू है तो दरख़्शाँ है हयात।",
            signatureStyle = "Melding tender romantic longing (Ishq) with progressive solidarity (Inqilab); musical cadence and dignified sorrow.",
            greetingSalutation = "रफ़ीक़! तुम्हारा शे'र दिल को छू गया, मगर बात और गहरी हो सकती है...",
            promptSystemPersona = "You are Faiz Ahmad Faiz. You write with quiet revolutionary tenderness, weaving the scent of rain, prison bars, and beloved lips into a timeless plea for justice and human warmth. Critique the student's verse with warm comradeship."
        ),
        UstaadPersona(
            id = UstaadId.KABIR,
            name = "Sant Kabir Das",
            nativeName = "संत कबीर दास • କବୀର ଦାସ",
            eraAndOrigin = "1398–1518 • Varanasi, Kashi",
            title = "Mystic Weaver • Truth Beyond Ritual",
            emblem = "🪕",
            signatureQuote = "पोथी पढ़ि पढ़ि जग मुआ, पंडित भया न कोय।\nढाई आखर प्रेम का, पढ़े सो पंडित होय॥",
            signatureStyle = "Unadorned Doha couplets in rustic vernacular (Saddukkadi/Braj), direct spiritual insight, razor-sharp critique of hypocrisy.",
            greetingSalutation = "साधो! तुम्हारी वाणी सुनी। शब्द में रस है, पर चित्त को और निर्मल करो...",
            promptSystemPersona = "You are Sant Kabir Das, the mystic weaver of Kashi. You speak in simple, devastatingly true couplets (Doha). You strip away fancy decorative fluff to find the raw pulse of love, honesty, and divine oneness within the heart."
        ),
        UstaadPersona(
            id = UstaadId.BHANJA,
            name = "Kabi Samrata Upendra Bhanja",
            nativeName = "କବି ସମ୍ରାଟ ଉପେନ୍ଦ୍ର ଭଞ୍ଜ",
            eraAndOrigin = "1670–1740 • Bhanjanagar, Odisha",
            title = "Kabi Samrata • Master of Chhanda & Yamaka",
            emblem = "🦚",
            signatureQuote = "କୋଟି ବ୍ରହ୍ମାଣ୍ଡ ସୁନ୍ଦରୀ ରୂପ ଲକ୍ଷଣେ,\nମୋହିଲା କୋବିଦ ଚିତ୍ତ କ୍ଷଣେ କ୍ଷଣେ।",
            signatureStyle = "Sublime alliteration (Anuprasa), strict classical metrical Chhanda, and rich devotion expressed through musical Odia.",
            greetingSalutation = "କାବ୍ୟରସିକ ବନ୍ଧୁ! ତୁମର ପଦ୍ୟ ପଢ଼ି ଚିତ୍ତ ପ୍ରଫୁଲ୍ଲିତ ହେଲା...",
            promptSystemPersona = "You are Kabi Samrata Upendra Bhanja of Odisha. You are renowned for your supreme mastery of musical meter (Chhanda), alliteration, and ornate lyrical beauty in the classical Odia tradition. Encourage the poet to refine the rhythm (laya) and internal rhyme (yamaka)."
        ),
        UstaadPersona(
            id = UstaadId.MEER,
            name = "Meer Taqi Meer",
            nativeName = "میر تقی میر • मीर तक़ी मीर",
            eraAndOrigin = "1723–1810 • Delhi / Lucknow",
            title = "Khuda-e-Sukhan • Master of Deceptive Simplicity",
            emblem = "🥀",
            signatureQuote = "पत्ता पत्ता बूटा बूटा हाल हमारा जाने है,\nजाने न जाने गुल ही न जाने बाग़ तो सारा जाने है।",
            signatureStyle = "Heartbreaking simplicity (Sahl-e-Mumtana), sighing musical flow, deep personal pathos, and delicate tenderness.",
            greetingSalutation = "साहब-ए-कमाल! दर्द तुम्हारे शे'र में झलकता है, ज़रा बह्र की नज़ाकत देखो...",
            promptSystemPersona = "You are Meer Taqi Meer, known as Khuda-e-Sukhan (God of Poetic Verses). You value gentle, flowing sorrow, crystalline simplicity where ordinary words carry ocean-deep weight, and effortless internal melody."
        ),
        UstaadPersona(
            id = UstaadId.RAHAT,
            name = "Dr. Rahat Indori",
            nativeName = "राहत इंदौरी • راحت اندوری",
            eraAndOrigin = "1950–2020 • Indore, Madhya Pradesh",
            title = "Sultan of Mushaira • Fearless Stage Fire",
            emblem = "🔥",
            signatureQuote = "सभी का ख़ून है शामिल यहाँ की मिट्टी में,\nकिसी के बाप का हिन्दुस्तान थोड़ी है!",
            signatureStyle = "Fierce colloquial punch, magnetic stage rhythm, contemporary courage, electrifying applause-drawing delivery.",
            greetingSalutation = "अरे भाई! शे'र में जान है, मगर जब मंच पर पढ़ोगे तो आग लगनी चाहिए...",
            promptSystemPersona = "You are Dr. Rahat Indori, the legend of the modern mushaira. You evaluate poetry with theatrical passion, commanding cadence, bold colloquial choices, and sheer spine-tingling energy."
        )
    )

    fun getPersona(id: UstaadId): UstaadPersona {
        return personas.find { it.id == id } ?: personas.first()
    }

    suspend fun getCritique(
        persona: UstaadPersona,
        coupletText: String,
        language: String
    ): Result<UstaadCritiqueResult> = withContext(Dispatchers.IO) {
        val apiKey = GeminiClient.getApiKey()
        if (apiKey.isNotBlank()) {
            try {
                val prompt = """
                    You are impersonating the legendary classical poet: ${persona.name} (${persona.title}).
                    Your voice and tone: ${persona.signatureStyle}
                    
                    The aspiring student has submitted this poetic couplet for your master's review (Islah):
                    Couplet:
                    "$coupletText"
                    Language Context: $language
                    
                    Perform an authentic, in-character master's critique (Islah).
                    Respond STRICTLY in valid JSON matching this schema:
                    {
                      "greeting": "An in-character greeting from ${persona.name} (e.g. ${persona.greetingSalutation})",
                      "critique": "A detailed 2-3 paragraph poetic appraisal written strictly in character, praising strengths and dissecting meter, emotional resonance, and word choice.",
                      "revisedCouplet": "Your masterly revised version of the user's couplet, maintaining their original thought but vastly improving the rhythm, rhyme, and lyrical power.",
                      "wordReplacements": [
                        {
                          "originalWord": "word from user verse",
                          "suggestedWord": "your elevated replacement",
                          "poeticReason": "brief aesthetic reason for the swap"
                        }
                      ],
                      "meterComment": "Commentary on the rhythm, wazn, or chhanda.",
                      "encouragement": "A warm concluding blessing/encouragement from ${persona.name}."
                    }
                """.trimIndent()

                val request = GeminiRequest(
                    contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
                    generationConfig = GeminiGenerationConfig(temperature = 0.7f)
                )

                val response = GeminiClient.api.generateContent("gemini-3.5-flash", apiKey, request)
                val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()

                if (!rawText.isNullOrBlank()) {
                    val cleanJson = rawText
                        .removePrefix("```json")
                        .removePrefix("```")
                        .removeSuffix("```")
                        .trim()

                    val json = JSONObject(cleanJson)
                    val greeting = json.optString("greeting", persona.greetingSalutation)
                    val critique = json.optString("critique", "A commendable effort with poignant emotion.")
                    val revised = json.optString("revisedCouplet", coupletText)
                    val meter = json.optString("meterComment", "Cadence aligns well with classical meter.")
                    val encouragement = json.optString("encouragement", "Keep writing with an open heart.")

                    val replacements = mutableListOf<WordIslah>()
                    val wordsArray = json.optJSONArray("wordReplacements") ?: JSONArray()
                    for (i in 0 until wordsArray.length()) {
                        val obj = wordsArray.optJSONObject(i) ?: continue
                        replacements.add(
                            WordIslah(
                                originalWord = obj.optString("originalWord"),
                                suggestedWord = obj.optString("suggestedWord"),
                                poeticReason = obj.optString("poeticReason")
                            )
                        )
                    }

                    return@withContext Result.success(
                        UstaadCritiqueResult(
                            persona = persona,
                            greeting = greeting,
                            critique = critique,
                            revisedCouplet = revised,
                            wordReplacements = replacements,
                            meterComment = meter,
                            encouragement = encouragement
                        )
                    )
                }
            } catch (e: Exception) {
                // Fall back to persona-tailored offline critique
            }
        }

        // Offline / Fallback Critique engine tailored to each persona
        Result.success(generateOfflineCritique(persona, coupletText))
    }

    fun generateOfflineCritique(persona: UstaadPersona, coupletText: String): UstaadCritiqueResult {
        val lines = coupletText.lines().filter { it.isNotBlank() }
        val line1 = lines.firstOrNull() ?: coupletText
        val line2 = lines.getOrNull(1) ?: "दिल की सदा जो गूँजे ख़ामोशी में..."

        return when (persona.id) {
            UstaadId.GHALIB -> UstaadCritiqueResult(
                persona = persona,
                greeting = persona.greetingSalutation,
                critique = "मियां! तुम्हारे ख़याल में सादगी तो है, मगर शे'र में जब तक वो पेचीदगी न हो जो सोचने पर मजबूर करे, बात अधूरी रहती है। तुम्हारा पहला मिसरा जज़्बात को छेड़ता है, मगर दूसरा मिसरा अगर किसी गहरे फलसफे (Falsafa) से जुड़ जाए तो शे'र अमर हो जाता है।",
                revisedCouplet = "$line1\n'ग़ालिब' ये दिल की बात है, कोई खेल तो नहीं!",
                wordReplacements = listOf(
                    WordIslah("दर्द", "कशमकश", "साधारण दर्द के बजाय कशमकश आंतरिक संघर्ष को उभारता है।"),
                    WordIslah("याद", "ख़ुमार", "याद से बढ़कर 'ख़ुमार' प्रेम के असर को बयान करता है।")
                ),
                meterComment = "बह्र-ए-हज़ज (Bahr-e-Hazaj) की लय का पालन करें, ताकि काफ़िया और रदीफ़ में तवाज़ुन बना रहे।",
                encouragement = "क़लम चलाते रहो बरख़ुरदार, शायरी रियाज़ मांगती है और तुम्हारी लगन साफ़ नज़र आ रही है।"
            )
            UstaadId.FAIZ -> UstaadCritiqueResult(
                persona = persona,
                greeting = persona.greetingSalutation,
                critique = "रफ़ीक़! तुमने जो लफ़्ज़ चुने हैं उनमें एक ख़ामोश बेचैनी है। मोहब्बत का दर्द जब तक ज़िंदगी की हक़ीक़त और इंसानी उम्मीद से न जुड़े, तब तक नज़्म की परवाज़ महदूद रहती है। दूसरे मिसरे में थोड़ी और नज़ाकत पैदा की जा सकती है।",
                revisedCouplet = "$line1\nये ज़ुल्मत-ए-शब भी ढलेगी, ज़रा सुब्ह का इंतज़ार तो कर।",
                wordReplacements = listOf(
                    WordIslah("रोना", "तड़प", "आँसुओं से ज़्यादा आंतरिक तड़प पाठकों के दिल पर असर छोड़ती है।"),
                    WordIslah("रात", "ज़ुल्मत-ए-शब", "फ़ारसी तरकीब से मिसरे का वज़न और वक़ार बढ़ जाता है।")
                ),
                meterComment = "रदीफ़ का दोहराव बहुत मुकम्मल है, बस मिसरे के पहले हिस्से में वक़्फ़ा (pause) बनाए रखें।",
                encouragement = "तुम्हारी आवाज़ में इंसानियत का दर्द है। इसे कभी बुझने मत देना।"
            )
            UstaadId.BHANJA -> UstaadCritiqueResult(
                persona = persona,
                greeting = persona.greetingSalutation,
                critique = "କାବ୍ୟରସିକ ବନ୍ଧୁ! ତୁମର ଭାବନା ଅତ୍ୟନ୍ତ ନିଷ୍ଠାପୂର୍ଣ୍ଣ। କିନ୍ତୁ କାବ୍ୟ ସେତିକିବେଳେ ଚିରନ୍ତନ ହୁଏ ଯେତେବେଳେ ପ୍ରତିଟି ଶବ୍ଦରେ ଅନୁପ୍ରାସ (Alliteration) ଏବଂ ମାତ୍ରା ଛାନ୍ଦର ମଧୁର ଝଙ୍କାର ଥାଏ।",
                revisedCouplet = "$line1\nମଧୁର ମୁରଲୀ ତାନେ ମନ ହୁଏ ବିଭୋର ସତେ।",
                wordReplacements = listOf(
                    WordIslah("ମନ", "ଚିତ୍ତ", "ଶାସ୍ତ୍ରୀୟ ଭାବ ବ୍ୟକ୍ତ କରିବା ପାଇଁ ଅଧିକ ସମର୍ଥ।"),
                    WordIslah("ଦୁଃଖ", "ବିରହ ଜ୍ୱାଳା", "ଭାବଗଭୀରତା ବୃଦ୍ଧି କରେ।")
                ),
                meterComment = "ନବାକ୍ଷରୀ କିମ୍ବା ଚଉପଦୀ ଛାନ୍ଦର ମାତ୍ରା ସଂଖ୍ୟାକୁ ସନ୍ତୁଳିତ ରଖନ୍ତୁ।",
                encouragement = "ଶବ୍ଦର ସାଧନା ଜାରି ରଖ, ଓଡ଼ିଆ କାବ୍ୟ ଜଗତ ତୁମର ପ୍ରତିଭାକୁ ସ୍ୱାଗତ କରେ।"
            )
            UstaadId.KABIR -> UstaadCritiqueResult(
                persona = persona,
                greeting = persona.greetingSalutation,
                critique = "साधो! लफ़्ज़ों का जाल बुनने से क्या फ़ायदा अगर दिल की गाँठ न खुले? जो भीतर अनुभव किया है, उसे बिना किसी आडंबर के कहो। दोहे की खूबी यही है कि वह सुई की तरह सीधा दिल में चुभता है।",
                revisedCouplet = "$line1\nकहै कबीर सुन भाई साधो, घट ही में साहिब पावे।",
                wordReplacements = listOf(
                    WordIslah("किस्मत", "करम", "भाग्य के रोने से अच्छा कर्म का मर्म समझना है।"),
                    WordIslah("मंदिर", "घट", "ईश्वर बाहर नहीं, घट (हृदय) के भीतर विराजता है।")
                ),
                meterComment = "दोहा छंद: प्रथम और तृतीय चरण में 13 मात्राएँ, द्वितीय और चतुर्थ में 11 मात्राएँ रखें।",
                encouragement = "सहज भाव से कहो, सत्य की आवाज़ को किसी बनावट की दरकार नहीं होती।"
            )
            UstaadId.MEER -> UstaadCritiqueResult(
                persona = persona,
                greeting = persona.greetingSalutation,
                critique = "साहब! शे'र वही है जो आह बन कर निकले। बहुत भारी लफ़्ज़ों का बोझ मत लादो। सहल-ए-मुम्तना (सरल मगर असीम गहरा) अंदाज़ इख़्तियार करो। मिसरा ऐसे बहे जैसे चुपचाप बहती हुई नदी।",
                revisedCouplet = "$line1\nमीर क्या कहें हाल-ए-दिल, बस आँख भर आई।",
                wordReplacements = listOf(
                    WordIslah("परेशान", "बेकरार", "मीर के दबिस्तान में बेकरारी को रूहानी मर्तबा हासिल है।")
                ),
                meterComment = "बह्र छोटी और मीठी हो तो दिल पर वार गहरा होता है।",
                encouragement = "दर्द का रियाज़ करते रहो, तुम्हारी नज़्मों में ख़ुदा की रहमत होगी।"
            )
            UstaadId.RAHAT -> UstaadCritiqueResult(
                persona = persona,
                greeting = persona.greetingSalutation,
                critique = "अरे भाई सुनो! बात जब तक सीने में तीर की तरह न लगे, तब तक शे'र कैसा? पहले मिसरे में सन्नाटा खींचो और दूसरे मिसरे में ऐसा वार करो कि पूरा मुशायरा उछल पड़े! लफ़्ज़ों में धार पैदा करो।",
                revisedCouplet = "$line1\nये शहर तुम्हारा है तो क्या, हमारे भी कुछ ख़्वाब हैं!",
                wordReplacements = listOf(
                    WordIslah("दुखी", "बेबाक", "शायरी में बेचारगी नहीं, तेवर होने चाहिए।")
                ),
                meterComment = "अंतिम लफ़्ज़ पर ऐसा वज़न दो कि तालियों की गूँज अपने आप गूँज उठे!",
                encouragement = "डरो मत, खुलकर लिखो। जब सच कहोगे तो ज़माना सुनेगा!"
            )
        }
    }
}
