package com.vtayur.madhvanama.data.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by varuntayur on 4/5/2014.
 */
@Root
public class Section implements Serializable {


    @Attribute(name = "name")
    private String name;

    @ElementList(inline = true, name = "shloka")
    private List<Shloka> shlokaList;

    @Attribute
    private String num;

    private Map<String, List<Shloka>> mapLang2ListShloka = new HashMap<String, List<Shloka>>();

    private Map<String, Shloka> mapShlokaNum2Shloka = new HashMap<String, Shloka>();

    public Section() {
    }

    @Override
    public String toString() {
        return "Section{" +
                "name='" + name + '\'' +
                ", shlokaList=" + shlokaList +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Shloka> getShlokaList() {
        return shlokaList;
    }

    public List<Shloka> getShlokaList(int startRange, int endRange) {
        return shlokaList.subList(startRange, endRange);
    }

    public void setShlokaList(List<Shloka> shlokaList) {
        this.shlokaList = shlokaList;
    }

    public Shloka getShloka(String shlokaNum) {

        if (mapShlokaNum2Shloka.keySet().isEmpty())
            for (Shloka shloka : this.shlokaList) {
                mapShlokaNum2Shloka.put(shloka.getNum(), shloka);
            }

        return mapShlokaNum2Shloka.get(shlokaNum);
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

}
