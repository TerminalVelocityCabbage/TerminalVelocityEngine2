package com.terminalvelocitycabbage.engine.translation;

/**
 * A list of typically recognized ISO 639-1 language codes
 */
public enum Language {

    AFRIKAANS("afrikaans","af"),
    ALBANIAN("albanian","sq"),
    ARABIC_ALGERIA("arabic (algeria)","ar-dz"),
    ARABIC_EGYPT("arabic (egypt)","ar-eg"),
    ARABIC_BAHRAIN("arabic (bahrain)","ar-bh"),
    ARABIC_IRAQ("arabic (iraq)","ar-iq"),
    ARABIC_JORDAN("arabic (jordan)","ar-jo"),
    ARABIC_KUWAIT("arabic (kuwait)","ar-kw"),
    ARABIC_LEBANON("arabic (lebanon)","ar-lb"),
    ARABIC_LIBYA("arabic (libya)","ar-ly"),
    ARABIC_MOROCCO("arabic (morocco)","ar-ma"),
    ARABIC_OMAN("arabic (oman)","ar-om"),
    ARABIC_QATAR("arabic (qatar)","ar-qa"),
    ARABIC_SAUDI_ARABIA("arabic (saudi arabia)","ar-sa"),
    ARABIC_SYRIA("arabic (syria)","ar-sy"),
    ARABIC_TUNISIA("arabic (tunisia)","ar-tn"),
    ARABIC_UAE("arabic (u.a.e.)","ar-ae"),
    ARABIC_YEMEN("arabic (yemen)","ar-ye"),
    BASQUE("basque","eu"),
    BELARUSIAN("belarusian","be"),
    BULGARIAN("bulgarian","bg"),
    CATALAN("catalan","ca"),
    CHINESE_HONG_KONG("chinese (hong kong)","zh-hk"),
    CHINESE_PRC("chinese (prc)","zh-cn"),
    CHINESE_SINGAPORE("chinese (singapore)","zh-sg"),
    CHINESE_TAIWAN("chinese (taiwan)","zh-tw"),
    CROATIAN("croatian","hr"),
    CZECH("czech","cs"),
    DANISH("danish","da"),
    DUTCH_BELGIUM("dutch (belgium)","nl-be"),
    DUTCH_STANDARD("dutch (standard)","nl"),
    ENGLISH("english","en"),
    ENGLISH_AUSTRALIA("english (australia)","en-au"),
    ENGLISH_BELIZE("english (belize)","en-bz"),
    ENGLISH_CANADA("english (canada)","en-ca"),
    ENGLISH_IRELAND("english (ireland)","en-ie"),
    ENGLISH_JAMAICA("english (jamaica)","en-jm"),
    ENGLISH_NEW_ZEALAND("english (new zealand)","en-nz"),
    ENGLISH_SOUTH_AFRICA("english (south africa)","en-za"),
    ENGLISH_TRINIDAD("english (trinidad)","en-tt"),
    ENGLISH_UNITED_KINGDOM("english (united kingdom)","en-gb"),
    ENGLISH_UNITED_STATES("english (united states)","en-us"),
    ESTONIAN("estonian","et"),
    FAEROESE("faeroese","fo"),
    FARSI("farsi","fa"),
    FINNISH("finnish","fi"),
    FRENCH_BELGIUM("french (belgium)","fr-be"),
    FRENCH_CANADA("french (canada)","fr-ca"),
    FRENCH_LUXEMBOURG("french (luxembourg)","fr-lu"),
    FRENCH_STANDARD("french (standard)","fr"),
    FRENCH_SWITZERLAND("french (switzerland)","fr-ch"),
    GAELIC_SCOTLAND("gaelic (scotland)","gd"),
    GERMAN_AUSTRIA("german (austria)","de-at"),
    GERMAN_LIECHTENSTEIN("german (liechtenstein)","de-li"),
    GERMAN_LUXEMBOURG("german (luxembourg)","de-lu"),
    GERMAN_STANDARD("german (standard)","de"),
    GERMAN_SWITZERLAND("german (switzerland)","de-ch"),
    GREEK("greek","el"),
    HEBREW("hebrew","he"),
    HINDI("hindi","hi"),
    HUNGARIAN("hungarian","hu"),
    ICELANDIC("icelandic","is"),
    INDONESIAN("indonesian","id"),
    IRISH("irish","ga"),
    ITALIAN_STANDARD("italian (standard)","it"),
    ITALIAN_SWITZERLAND("italian (switzerland)","it-ch"),
    JAPANESE("japanese","ja"),
    KOREAN("korean","ko"),
    KOREAN_JOHAB("korean (johab)","ko"),
    KURDISH("kurdish","ku"),
    LATVIAN("latvian","lv"),
    LITHUANIAN("lithuanian","lt"),
    MACEDONIAN_FYROM("macedonian (fyrom)","mk"),
    MALAYALAM("malayalam","ml"),
    MALAYSIAN("malaysian","ms"),
    MALTESE("maltese","mt"),
    NORWEGIAN("norwegian","no"),
    NORWEGIAN_BOKMAL("norwegian (bokmål)","nb"),
    NORWEGIAN_NYNORSK("norwegian (nynorsk)","nn"),
    POLISH("polish","pl"),
    PORTUGUESE_BRAZIL("portuguese (brazil)","pt-br"),
    PORTUGUESE_PORTUGAL("portuguese (portugal)","pt"),
    PUNJABI("punjabi","pa"),
    RHAETO_ROMANIC("rhaeto-romanic","rm"),
    ROMANIAN("romanian","ro"),
    ROMANIAN_REPUBLIC_OF_MOLDOVA("romanian (republic of moldova)","ro-md"),
    RUSSIAN("russian","ru"),
    RUSSIAN_REPUBLIC_OF_MOLDOVA("russian (republic of moldova)","ru-md"),
    SERBIAN("serbian","sr"),
    SLOVAK("slovak","sk"),
    SLOVENIAN("slovenian","sl"),
    SORBIAN("sorbian","sb"),
    SPANISH_ARGENTINA("spanish (argentina)","es-ar"),
    SPANISH_BOLIVIA("spanish (bolivia)","es-bo"),
    SPANISH_CHILE("spanish (chile)","es-cl"),
    SPANISH_COLOMBIA("spanish (colombia)","es-co"),
    SPANISH_COSTA_RICA("spanish (costa rica)","es-cr"),
    SPANISH_DOMINICAN_REPUBLIC("spanish (dominican republic)","es-do"),
    SPANISH_ECUADOR("spanish (ecuador)","es-ec"),
    SPANISH_EL_SALVADOR("spanish (el salvador)","es-sv"),
    SPANISH_GUATEMALA("spanish (guatemala)","es-gt"),
    SPANISH_HONDURAS("spanish (honduras)","es-hn"),
    SPANISH_MEXICO("spanish (mexico)","es-mx"),
    SPANISH_NICARAGUA("spanish (nicaragua)","es-ni"),
    SPANISH_PANAMA("spanish (panama)","es-pa"),
    SPANISH_PARAGUAY("spanish (paraguay)","es-py"),
    SPANISH_PERU("spanish (peru)","es-pe"),
    SPANISH_PUERTO_RICO("spanish (puerto rico)","es-pr"),
    SPANISH_SPAIN("spanish (spain)","es"),
    SPANISH_URUGUAY("spanish (uruguay)","es-uy"),
    SPANISH_VENEZUELA("spanish (venezuela)","es-ve"),
    SWEDISH("swedish","sv"),
    SWEDISH_FINLAND("swedish (finland)","sv-fi"),
    THAI("thai","th"),
    TSONGA("tsonga","ts"),
    TSWANA("tswana","tn"),
    TURKISH("turkish","tr"),
    UKRAINIAN("ukrainian","uk"),
    URDU("urdu","ur"),
    VENDA("venda","ve"),
    VIETNAMESE("vietnamese","vi"),
    WELSH("welsh","cy"),
    XHOSA("xhosa","xh"),
    YIDDISH("yiddish","ji"),
    ZULU("zulu","zu");

    final String name;
    final String abbreviation;

    Language(String name, String abbreviation) {
        this.name = name;
        this.abbreviation = abbreviation;
    }

    public static Language fromName(String name) {
        for (Language language : Language.values()) {
            if (language.name().equals(name)) {
                return language;
            }
        }
        return null;
    }

    public static Language fromAbbreviation(String abbreviation) {
        for (Language value : values()) {
            if (value.abbreviation.equals(abbreviation)) {
                return value;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }
}
