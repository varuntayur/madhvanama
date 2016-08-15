package com.vtayur.madhvanama.data;

import android.content.res.AssetManager;
import android.graphics.Typeface;

/**
 * Created by vtayur on 8/22/2014.
 */
public enum Language {

    san("Sanskrit", "fonts/droidsansdevanagari.ttf"), kan("Kannada", "fonts/notosanskannadaregular.ttf"), eng("English", "");

    private final String language;
    private final String font;

    Language(String language, String font) {
        this.language = language;
        this.font = font;
    }

    public static Language getLanguageEnum(int selected) {

        switch (selected) {
            case 0:
                return Language.san;
            case 1:
                return Language.kan;
        }
        return Language.eng;
    }

    public static Language getLanguageEnum(String language) {
        for (Language lang : Language.values()) {
            if (lang.toString().equals(language))
                return lang;
        }
        return Language.eng;
    }

    @Override
    public String toString() {
        return language;
    }

    public Typeface getTypeface(AssetManager assets) {
        return Typeface.createFromAsset(assets, this.font);
    }
}
