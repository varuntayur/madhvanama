package com.vtayur.madhvanama.data.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by varuntayur on 4/5/2014.
 */
@Root
public class Shloka implements Serializable {


    public static final String EMPTY_STRING = "";
    @Attribute
    private String num;

    @Element(required = false)
    private String text;

    @Element(required = false)
    private ShlokaDescription explanation;

    @Element(required = false)
    private PhalaShruthi phala;

    public Shloka() {
    }

    @Override
    public String toString() {
        return "Shloka{" +
                "num='" + num + '\'' +
                ", text='" + text + '\'' +
                ", explanation=" + explanation +
                ", phala=" + phala +
                '}';
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getText() {
        if (text != null) {

            StringBuilder sbuilder = new StringBuilder();
            String[] split = text.split("\n");
            for (String splitStr : split) {
                sbuilder.append(splitStr.trim());
                sbuilder.append("\n");
            }
            return sbuilder.toString().trim();
        }
        return EMPTY_STRING;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setExplanation(ShlokaDescription explanation) {
        this.explanation = explanation;
    }

    public ShlokaDescription getExplanation() {
        return this.explanation;
    }

    public String getFormattedExplanation() {

        StringBuilder stringBuilder = new StringBuilder();
        for (Note note : explanation.getNotesList()) {
            stringBuilder.append(note.getFormattedNote());
        }
        return stringBuilder.toString();
    }

    public PhalaShruthi getPhala() {
        return phala;
    }
}
