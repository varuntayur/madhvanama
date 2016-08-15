package com.vtayur.madhvanama.data;

import android.content.res.AssetManager;
import android.util.Log;

import com.vtayur.madhvanama.R;
import com.vtayur.madhvanama.data.model.Madhvanama;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by vtayur on 8/19/2014.
 */
public class DataProvider {

    private static final String TAG = "DataProvider";

    public static final String PREFS_NAME = "MadhvaNama";
    public static final String SHLOKA_DISP_LANGUAGE = "localLanguage";
    public static final String LEARNING_MODE = "learningMode";
    public static final String REPEAT_SHLOKA = "repeatShlokaCount";
    public static final String REPEAT_SHLOKA_DEFAULT = "3";

    private static Map<String, Madhvanama> lang2Madhvanama = new ConcurrentHashMap<String, Madhvanama>();

    private static List<Integer> mBackgroundColors = new ArrayList<Integer>() {
        {
            add(R.color.orange);
            add(R.color.green);
            add(R.color.blue);
            add(R.color.yellow);
            add(R.color.grey);
            add(R.color.lblue);
            add(R.color.slateblue);
            add(R.color.cyan);
            add(R.color.silver);
        }
    };

    private final static CharSequence[] languages = {"Sanskrit", "Kannada"};

    public static CharSequence[] getLanguages() {
        return languages;
    }

    public static List<Integer> getBackgroundColorList() {
        return Collections.unmodifiableList(mBackgroundColors);
    }

    public static List<String> getMenuNames() {

        String anyResource = lang2Madhvanama.keySet().iterator().next();

        return new ArrayList<String>(DataProvider.getVayuSthuthi(Language.getLanguageEnum(anyResource)).getSectionNames());
    }

    public static int getBackgroundColor(int location) {
        return mBackgroundColors.get(location);
    }

    public static void init(AssetManager am) {
        Serializer serializer;
        InputStream inputStream;
        try {
            inputStream = am.open("db/sriharivayustuthi-eng.xml");
            serializer = new Persister();
            Madhvanama madhvanama = serializer.read(Madhvanama.class, inputStream);
            lang2Madhvanama.put(madhvanama.getLang(), madhvanama);
            Log.d(TAG, "* Finished de-serializing the file - sriharivayustuthi-eng.xml *");

            inputStream = am.open("db/sriharivayustuthi-kan.xml");
            serializer = new Persister();
            madhvanama = serializer.read(Madhvanama.class, inputStream);
            lang2Madhvanama.put(madhvanama.getLang(), madhvanama);
            Log.d(TAG, "* Finished de-serializing the file - sriharivayustuthi-kan.xml *");

            inputStream = am.open("db/sriharivayustuthi-san.xml");
            serializer = new Persister();
            madhvanama = serializer.read(Madhvanama.class, inputStream);
            lang2Madhvanama.put(madhvanama.getLang(), madhvanama);
            Log.d(TAG, "* Finished de-serializing the file - sriharivayustuthi-san.xml *");

            System.out.println(lang2Madhvanama.keySet());

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "* IOException de-serializing the file *" + e);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "* Exception de-serializing the file *" + e);
        }
    }

    public static Madhvanama getVayuSthuthi(Language lang) {
        return lang2Madhvanama.get(lang.toString());
    }


}
