package com.vtayur.madhvanama.home;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.vtayur.madhvanama.data.Language;
import com.vtayur.madhvanama.data.BundleArgs;
import com.vtayur.madhvanama.data.DataProvider;
import com.vtayur.madhvanama.data.YesNo;
import com.vtayur.madhvanama.data.model.Section;
import com.vtayur.madhvanama.detail.ShlokaSlideActivity;
import com.vtayur.madhvanama.detail.StotraInOnePageActivity;

import java.io.Serializable;

/**
 * Created by varuntayur on 6/21/2014.
 */
public enum MadhvanamaMenu {

    DEFAULT("Default") {
        @Override
        public void execute(Activity activity, String item, int position, Language language) {

            Intent intent = null;

            SharedPreferences settings = activity.getSharedPreferences(DataProvider.PREFS_NAME, 0);
            String learningMode = settings.getString(DataProvider.LEARNING_MODE, "");

            if (YesNo.yes.toString().equalsIgnoreCase(learningMode))
                intent = new Intent(activity, ShlokaSlideActivity.class);
            else
                intent = new Intent(activity, StotraInOnePageActivity.class);

            Section secEnglish = DataProvider.getMadhvanama(Language.eng).getSection(item);

            if (secEnglish == null) return;

            Section secMadhvanama = DataProvider.getMadhvanama(language).getSection(item);

            intent.putExtra(BundleArgs.SECTION_NAME, item);
            intent.putExtra(BundleArgs.PAGE_NUMBER, position);
            intent.putExtra(BundleArgs.ENG_SHLOKA_LIST, (Serializable) secEnglish.getShlokaList());
            intent.putExtra(BundleArgs.LOCAL_LANG_SHLOKA_LIST, (Serializable) secMadhvanama.getShlokaList());

            Log.d(TAG, "MadhvanamaMenu item secEnglish ->" + item + " " + secEnglish);

            activity.startActivity(intent);
        }
    };

    private static final String TAG = "MadhvanamaMenu";
    private String menuDisplayName;

    MadhvanamaMenu(String menu) {
        this.menuDisplayName = menu;
    }

    public static MadhvanamaMenu getEnum(String item) {
        for (MadhvanamaMenu v : values())
            if (v.toString().equalsIgnoreCase(item)) return v;
        return MadhvanamaMenu.DEFAULT;
    }

    @Override
    public String toString() {
        return menuDisplayName;
    }

    public abstract void execute(Activity activity, String item, int position, Language language);
}
