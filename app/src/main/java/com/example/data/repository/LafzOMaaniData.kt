package com.example.data.repository

import com.example.data.model.PoeticWordDefinition

object LafzOMaaniData {
    val vocabulary: List<PoeticWordDefinition> = listOf(
        PoeticWordDefinition(
            wordLatin = "Hijr",
            wordUrdu = "حجر",
            wordHindi = "हिज्र",
            wordOdia = "ହିଜ୍ର (ବିରହ)",
            originLanguage = "Arabic / Persian",
            literalMeaning = "Separation, physical distance, abandonment",
            poeticNuance = "The agonizing state of lovers held apart by fate, time, or social decree; often treated as a spiritual crucible in Sufi poetry.",
            rootDerivation = "Root H-J-R (هجر) - to forsake, depart, or sever ties.",
            rhymingCompanions = listOf("Fikr (फ़िक्र)", "Zikr (ज़िक्र)", "Sabr (सब्र)", "Umar (उम्र)"),
            exampleCouplet = "मौत का एक दिन मुअय्यन है,\nनींद क्यूँ रात भर नहीं आती?",
            exampleAuthor = "Mirza Ghalib",
            categoryTag = "Grief"
        ),
        PoeticWordDefinition(
            wordLatin = "Visal",
            wordUrdu = "وصال",
            wordHindi = "विसाल",
            wordOdia = "ବିସାଲ (ମିଳନ)",
            originLanguage = "Arabic",
            literalMeaning = "Union, joining together, rendezvous",
            poeticNuance = "The ecstatic, elusive meeting of the lover with the beloved or the seeker's soul dissolving into the Divine.",
            rootDerivation = "Root W-S-L (وصل) - to arrive, connect, or unite.",
            rhymingCompanions = listOf("Khayal (ख़याल)", "Zawal (ज़वाल)", "Kamal (कमाल)", "Sawwal (सवाल)"),
            exampleCouplet = "शब-ए-विसाल बहुत कम है आसमाँ के लिए,\nजो हो सके तो कोई और शब बढ़ा दीजे।",
            exampleAuthor = "Dagh Dehlvi",
            categoryTag = "Love"
        ),
        PoeticWordDefinition(
            wordLatin = "Dastoor",
            wordUrdu = "دستور",
            wordHindi = "दस्तूर",
            wordOdia = "ଦସ୍ତୁର (ପରମ୍ପରା)",
            originLanguage = "Persian",
            literalMeaning = "Custom, rule, tradition, constitution",
            poeticNuance = "The unbending societal laws, court rituals, or orthodox constraints that true lovers and free poets challenge.",
            rootDerivation = "Persian dast (hand) + var/oor (holder) -> guideline or authority.",
            rhymingCompanions = listOf("Noor (नूर)", "Futoor (फ़ितूर)", "Door (दूर)", "Suroor (सरूर)"),
            exampleCouplet = "दीप जिसका महल्लात ही में जले,\nऐसे दस्तूर को, सुब्ह-ए-बे-नूर को, मैं नहीं मानता!",
            exampleAuthor = "Habib Jalib",
            categoryTag = "Aesthetics"
        ),
        PoeticWordDefinition(
            wordLatin = "Bismil",
            wordUrdu = "بسمل",
            wordHindi = "बिस्मिल",
            wordOdia = "ବିସ୍ମିଲ (ଆହତ)",
            originLanguage = "Arabic / Persian",
            literalMeaning = "Sacrificed, wounded, fluttering half-slain creature",
            poeticNuance = "Metaphor for a lover completely entranced and wounded by the glancing gaze of the beloved, unable to flee or heal.",
            rootDerivation = "From the phrase 'Bismillah' spoken before an offering.",
            rhymingCompanions = listOf("Dil (दिल)", "Manzil (मंज़िल)", "Saahil (साहिल)", "Mehfil (महफ़िल)"),
            exampleCouplet = "सरफ़रोशी की तमन्ना अब हमारे दिल में है,\nदेखना है ज़ोर कितना बाज़ू-ए-क़ातिल में है।",
            exampleAuthor = "Bismil Azimabadi",
            categoryTag = "Grief"
        ),
        PoeticWordDefinition(
            wordLatin = "Qasid",
            wordUrdu = "قاصد",
            wordHindi = "क़ासिद",
            wordOdia = "କାଶିଦ (ଦୂତ)",
            originLanguage = "Arabic",
            literalMeaning = "Messenger, envoy, courier",
            poeticNuance = "The trusted yet envied messenger carrying intimate letters to the beloved's sequestered court.",
            rootDerivation = "Root Q-S-D (قصد) - to aim, intend, or carry out a mission.",
            rhymingCompanions = listOf("Ummeed (उम्मीद)", "Shaheed (शहीद)", "Naweed (नवीद)"),
            exampleCouplet = "क़ासिद के आते-आते ख़त इक और लिख रखूँ,\nमैं जानता हूँ जो वो लिखेंगे जवाब में।",
            exampleAuthor = "Mirza Ghalib",
            categoryTag = "Love"
        ),
        PoeticWordDefinition(
            wordLatin = "Junoon",
            wordUrdu = "جنون",
            wordHindi = "जुनून",
            wordOdia = "ଜୁନୁନ (ପାଗଳାମି)",
            originLanguage = "Arabic",
            literalMeaning = "Madness, intense frenzy, obsession",
            poeticNuance = "Holy poetic passion that transcends worldly logic; celebrated by Majnoon wandering the desolate desert for Laila.",
            rootDerivation = "Root J-N-N (جن) - hidden from view, veiled by supernatural ecstasy.",
            rhymingCompanions = listOf("Sukoon (सुकून)", "Afsoon (अफ़सून)", "Khoon (ख़ून)"),
            exampleCouplet = "इश्क़ में ग़ैरत-ए-जज़्बात ने रोने न दिया,\nवरना क्या बात थी किस बात ने रोने न दिया।",
            exampleAuthor = "Faiz Ahmad Faiz",
            categoryTag = "Mysticism"
        ),
        PoeticWordDefinition(
            wordLatin = "Bibhaswati",
            wordUrdu = "بیبهاسوتی",
            wordHindi = "विभास्वती",
            wordOdia = "ବିଭାସ୍ଵତୀ",
            originLanguage = "Sanskrit / Odia",
            literalMeaning = "Luminous woman, radiant glow, dispeller of darkness",
            poeticNuance = "Used in Odia classical poetry (*Chhanda*) to personify morning dawn or the beloved whose radiant face turns night into day.",
            rootDerivation = "Sanskrit: Vibhā (splendor/radiance) + suffix -vatī (possessing).",
            rhymingCompanions = listOf("Saraswati (ସରସ୍ୱତୀ)", "Prabhavati (ପ୍ରଭାବତୀ)", "Padmavati (ପଦ୍ମାବତୀ)"),
            exampleCouplet = "ବିଭାସ୍ଵତୀ ମୁଖ ଚାହିଁ ମନ ମୋର ଝୁରେ,\nକୋଟି ପ୍ରଦୀପ ସମ ଆଲୋକ ଢାଳଇ ନିଶୀଥରେ।",
            exampleAuthor = "Kabi Samrata Upendra Bhanja",
            categoryTag = "Aesthetics"
        ),
        PoeticWordDefinition(
            wordLatin = "Manamohana",
            wordUrdu = "من موهن",
            wordHindi = "मनमोहन",
            wordOdia = "ମନମୋହନ",
            originLanguage = "Sanskrit / Odia",
            literalMeaning = "Mind-captivator, enchanter of inner consciousness",
            poeticNuance = "Classical Odia Vaishnavite poetry epithet for the Divine Beloved whose flute melody steals away worldly ties.",
            rootDerivation = "Manas (mind/heart) + Mohana (enchanting/attracting).",
            rhymingCompanions = listOf("Pranadhana (ପ୍ରାଣଧନ)", "Jeevana (ଜୀବନ)", "Chandana (ଚନ୍ଦନ)"),
            exampleCouplet = "ମନମୋହନ ମୋର ପ୍ରାଣନାଥ,\nତୁମ ବିନା ଶୂନ୍ୟ ଏଇ ଜୀବନ ପଥ।",
            exampleAuthor = "Bhakta Kabi Banamali",
            categoryTag = "Mysticism"
        ),
        PoeticWordDefinition(
            wordLatin = "Abhisara",
            wordUrdu = "ابهیسارا",
            wordHindi = "अभिसार",
            wordOdia = "ଅଭିସାର",
            originLanguage = "Sanskrit / Odia",
            literalMeaning = "A lover's courageous secret journey in the dark",
            poeticNuance = "The classical heroine (*Abhisarika*) walking through thunder, lightning, and thorny forests fearless of peril for love.",
            rootDerivation = "Abhi (towards) + Sri (to advance/glide).",
            rhymingCompanions = listOf("Sansara (ସଂସାର)", "Bichara (ବିଚାର)", "Udhara (ଉଦ୍ଧାର)"),
            exampleCouplet = "ଅଭିସାର ରଜନୀରେ ଘୋର ଘନ ଘଟା,\nପ୍ରେମୀ ପାଦେ ବାଜେ ଯେବେ କଣ୍ଟକର ଛଟା।",
            exampleAuthor = "Kabisurjya Baladev Rath",
            categoryTag = "Love"
        ),
        PoeticWordDefinition(
            wordLatin = "Tashnagi",
            wordUrdu = "تشنگی",
            wordHindi = "तिश्नगी",
            wordOdia = "ତିସ୍ନଗୀ (ପିପାସା)",
            originLanguage = "Persian",
            literalMeaning = "Thirst, parched throat, dry desert longing",
            poeticNuance = "The infinite spiritual or romantic thirst that deepens the more the lover drinks from the cup of the beloved's beauty.",
            rootDerivation = "Persian: Tishneh (thirsty) + -gi (state/quality).",
            rhymingCompanions = listOf("Bandagi (बंदगी)", "Zindagi (ज़िंदगी)", "Tazgi (ताज़गी)"),
            exampleCouplet = "तिश्नगी ख़त्म नहीं होती समुंदर पीकर भी,\nइश्क़ वो प्यास है जो बूंद में बह जाती है।",
            exampleAuthor = "Meer Taqi Meer",
            categoryTag = "Mysticism"
        ),
        PoeticWordDefinition(
            wordLatin = "Kaifiyat",
            wordUrdu = "کیفیت",
            wordHindi = "कैफ़ियत",
            wordOdia = "କୈଫିୟତ (ଅବସ୍ଥା)",
            originLanguage = "Arabic",
            literalMeaning = "State of affairs, mood, inner psychic quality",
            poeticNuance = "The delicate, fragile atmosphere of feeling that cannot be explained in prose, only felt through verse.",
            rootDerivation = "From Arabic 'Kayf' (how / how does one fare?).",
            rhymingCompanions = listOf("Nafsiyat (नफ़्सियात)", "Shakhsiyat (शख़्सियत)", "Aafiyat (आफ़ियत)"),
            exampleCouplet = "अपनी कैफ़ियत का क्या बयान करूँ,\nदिल तो धड़कता है पर ज़िंदगी कहीं नहीं।",
            exampleAuthor = "Kaifi Azmi",
            categoryTag = "Aesthetics"
        ),
        PoeticWordDefinition(
            wordLatin = "Rind",
            wordUrdu = "رند",
            wordHindi = "रिंद",
            wordOdia = "ରିନ୍ଦ (ମୁକ୍ତଚେତା)",
            originLanguage = "Persian",
            literalMeaning = "Tavern-dweller, free-spirited nonconformist",
            poeticNuance = "In Sufi poetry, the sincere spiritual wanderer who rejects hypocritical piety in favor of truthful, unpretentious divine drunkenness.",
            rootDerivation = "Persian: Rind (candid, carefree lover).",
            rhymingCompanions = listOf("Chand (चाँद)", "Band (बंद)", "Kamand (कमंद)"),
            exampleCouplet = "ज़ाहिद शराब पीने दे मस्जिद में बैठ कर,\nया वो जगह बता जहाँ पर ख़ुदा न हो।",
            exampleAuthor = "Mirza Ghalib",
            categoryTag = "Mysticism"
        ),
        PoeticWordDefinition(
            wordLatin = "Takhallus",
            wordUrdu = "تخلص",
            wordHindi = "तख़ल्लुस",
            wordOdia = "ତଖଲ୍ଲୁସ (ଭଣିତା)",
            originLanguage = "Arabic",
            literalMeaning = "Liberation, conclusion, poet's adopted pen name",
            poeticNuance = "The sacred nom de plume woven into the concluding couplet (*Maqta*) where the poet steps back and addresses themselves directly.",
            rootDerivation = "Root KH-L-S (خلص) - to become pure, distinct, or emancipated.",
            rhymingCompanions = listOf("Khuloos (ख़ुलूस)", "Afsos (अफ़सोस)"),
            exampleCouplet = "हम को मा'लूम है जन्नत की हक़ीक़त लेकिन,\nदिल के ख़ुश रखने को 'ग़ालिब' ये ख़याल अच्छा है।",
            exampleAuthor = "Mirza Ghalib",
            categoryTag = "Aesthetics"
        ),
        PoeticWordDefinition(
            wordLatin = "Chhanda",
            wordUrdu = "چهندا",
            wordHindi = "छंद",
            wordOdia = "ଛାନ୍ଦ",
            originLanguage = "Sanskrit / Odia",
            literalMeaning = "Metrical rhythm, lyrical verse structure, poetic cadence",
            poeticNuance = "The foundation of classical Odia poetry where every line adheres to fixed syllable and matra counts tailored to classical Ragas.",
            rootDerivation = "Sanskrit root Chhad (to gladden, charm, or clothe in beauty).",
            rhymingCompanions = listOf("Ananda (ଆନନ୍ଦ)", "Mukunda (ମୁକୁନ୍ଦ)", "Gobinda (ଗୋବିନ୍ଦ)"),
            exampleCouplet = "ଛାନ୍ଦ ଚଉପଦୀ ଗୀତେ ମୋହିଲା ଉତ୍କଳ,\nଶୁଣିଲେ ତୋଷ ହୁଅଇ ଜନମାନସ ସକଳ।",
            exampleAuthor = "Kabi Samrata Upendra Bhanja",
            categoryTag = "Aesthetics"
        ),
        PoeticWordDefinition(
            wordLatin = "Khumaar",
            wordUrdu = "خمار",
            wordHindi = "ख़ुमार",
            wordOdia = "ଖୁମାର (ମାଦକତା)",
            originLanguage = "Arabic",
            literalMeaning = "Intoxication, lingering afterglow of wine or beauty",
            poeticNuance = "The sweet lethargy that follows profound emotional contact; the enduring scent of the beloved lingering in solitude.",
            rootDerivation = "Root KH-M-R (خمر) - to ferment, veil, or intoxicate.",
            rhymingCompanions = listOf("Diyar (दयार)", "Qarar (क़रार)", "Bahaar (बहार)", "Pyaar (प्यार)"),
            exampleCouplet = "ये जो हल्का-हल्का सुरूर है,\nये तेरी नज़र का क़ुसूर है!",
            exampleAuthor = "Nusrat Fateh Ali Khan / Anwar Farrukhabadi",
            categoryTag = "Love"
        )
    )

    fun search(query: String): List<PoeticWordDefinition> {
        val trimmed = query.trim().lowercase()
        if (trimmed.isEmpty()) return vocabulary
        return vocabulary.filter {
            it.wordLatin.lowercase().contains(trimmed) ||
            it.wordHindi.lowercase().contains(trimmed) ||
            it.wordUrdu.lowercase().contains(trimmed) ||
            it.wordOdia.lowercase().contains(trimmed) ||
            it.literalMeaning.lowercase().contains(trimmed) ||
            it.poeticNuance.lowercase().contains(trimmed) ||
            it.originLanguage.lowercase().contains(trimmed)
        }
    }

    fun findMatchingWordsInText(text: String): List<PoeticWordDefinition> {
        val normalized = text.lowercase()
        return vocabulary.filter { def ->
            normalized.contains(def.wordLatin.lowercase()) ||
            normalized.contains(def.wordHindi.lowercase()) ||
            normalized.contains(def.wordUrdu.lowercase()) ||
            (def.wordOdia.isNotBlank() && normalized.contains(def.wordOdia.lowercase()))
        }
    }
}
