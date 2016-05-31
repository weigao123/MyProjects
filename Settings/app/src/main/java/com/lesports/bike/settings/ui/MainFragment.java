package com.lesports.bike.settings.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lesports.bike.settings.R;
import com.lesports.bike.settings.utils.ActivityUtils;

/**
 * Created by gaowei3 on 2016/5/16.
 */
public class MainFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    public static final Integer[] mItemsText = new Integer[] {
        R.string.audio_profiles,
        R.string.wifi,
        R.string.data_usage,
        R.string.bluetooth,
        R.string.sensor,
        R.string.ptt,
        R.string.display,
        R.string.light,
        R.string.security_and_password,
        R.string.date_time,
        R.string.language,
        R.string.about_bike
    };
    private static final Integer[] mItemsImage = new Integer[] {
        R.drawable.sound,
        R.drawable.wifi_1,
        R.drawable.data,
        R.drawable.bluetooth,
        R.drawable.sensor,
        R.drawable.ptt,
        R.drawable.display,
        R.drawable.light,
        R.drawable.fingerprint,
        R.drawable.date,
        R.drawable.language,
        R.drawable.bike
    };
    private static final Class[] classes = {
        AudioFragment.class,
        WifiFragment.class,
        DataUsageFragment.class,
        BluetoothFragment.class,
        SensorFragment.class,
        PttFragment.class,
        DisplayFragment.class,
        LightFragment.class,
        SecurityFragment.class,
        DateFragment.class,
        LanguageFragment.class,
        AboutFragment.class
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    protected String getTitleName() {
        return getResources().getString(R.string.setting);
    }

    @Override
    public void initViewAndData() {
        ListView listView = (ListView) getActivity().findViewById(
                R.id.main_list);
        ListAdapter adapter = new ListAdapter(getActivity(), 0, mItemsText);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        Log.d("wei.gao", "position: "+position);
        ActivityUtils.startFragmentActivity(getActivity(), classes[position]);
    }

    private class ListAdapter extends ArrayAdapter<Integer> {
        public ListAdapter(Context context, int resource, Integer[] objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.home_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) convertView
                        .findViewById(R.id.setting_list_image_view);
                viewHolder.textView = (TextView) convertView
                        .findViewById(R.id.setting_list_text_view);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.textView.setText(mItemsText[position]);
            viewHolder.imageView.setImageResource(mItemsImage[position]);
            return convertView;
        }
    }

    private static class ViewHolder {
        public ImageView imageView;
        public TextView textView;
    }
}
