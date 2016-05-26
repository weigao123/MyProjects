package com.lesports.bike.settings.ui;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lesports.bike.settings.R;

import java.util.List;

/**
 * Created by gwball on 2016/5/25.
 */
public class WifiFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private WifiManager mWifiManager;
    private List<ScanResult> mdata;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wifi, container, false);
    }

    @Override
    protected String getTitleName() {
        return getResources().getString(R.string.wifi);
    }


    @Override
    public void initViewAndData() {
        mWifiManager = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);

        ListView listView = (ListView) getActivity().findViewById(R.id.main_list);
        ListAdapter adapter = new ListAdapter(getActivity(), 0, mdata);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    private class ListAdapter extends ArrayAdapter<ScanResult> {
        public ListAdapter(Context context, int resource, List<ScanResult> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.home_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.wifiLockView = (ImageView) convertView.findViewById(R.id.setting_list_image_view);
                viewHolder.wifiNameView = (TextView) convertView.findViewById(R.id.setting_list_text_view);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.wifiNameView.setText(mdata.get(position).SSID);
            viewHolder.wifiLockView.setVisibility(true?View.VISIBLE:View.INVISIBLE);
            return convertView;
        }
    }

    private static class ViewHolder {
        public TextView wifiNameView;
        public ImageView wifiLockView;
    }
}