
package com.lesports.bike.settings.ui;

import java.util.Locale;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.lesports.bike.settings.R;
import com.lesports.bike.settings.utils.LanguageUtil;

/**
 * Created by gwball on 2016/5/25.
 */
public class LanguageFragment extends BaseFragment implements OnClickListener {
    private View languageView;
    private CheckBox chineseCB;
    private CheckBox englishCB;

    @Override
    protected void initViewAndData() {
        languageView.findViewById(R.id.language_chinese).setOnClickListener(
                this);
        languageView.findViewById(R.id.language_english).setOnClickListener(
                this);
        chineseCB = (CheckBox) languageView.findViewById(R.id.chinese_cb);
        englishCB = (CheckBox) languageView.findViewById(R.id.english_cb);
        chineseCB.setClickable(false);
        englishCB.setClickable(false);
        initLanguage();
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        languageView = inflater.inflate(R.layout.fragment_language, container,
                false);
        return languageView;
    }

    @Override
    protected String getTitleName() {
        return getResources().getString(R.string.language);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.language_chinese:
                resetSelect();
                chineseCB.setChecked(true);
                LanguageUtil.updateLocale(getActivity().getApplicationContext(),
                        Locale.SIMPLIFIED_CHINESE);
                break;
            case R.id.language_english:
                resetSelect();
                englishCB.setChecked(true);
                LanguageUtil.updateLocale(getActivity().getApplicationContext(), Locale.US);
                break;
            default:
                break;
        }

    }

    private void resetSelect() {
        chineseCB.setChecked(false);
        englishCB.setChecked(false);
    }

    private void initLanguage() {
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh")) {
            chineseCB.setChecked(true);
        }
        else if (language.endsWith("en")) {
            englishCB.setChecked(true);
        }
    }
}
