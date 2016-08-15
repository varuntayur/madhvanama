package com.vtayur.madhvanama.data;

/**
 * Created by vtayur on 8/22/2014.
 */
public enum YesNo {

    yes("Yes"), no("No");

    private final String value;

    YesNo(String value) {
        this.value = value;
    }

    public static YesNo getYesNoEnum(String strValue) {
        for (YesNo lang : YesNo.values()) {
            if (lang.toString().equals(strValue))
                return lang;
        }
        return YesNo.no;
    }

    @Override
    public String toString() {
        return value;
    }

}
