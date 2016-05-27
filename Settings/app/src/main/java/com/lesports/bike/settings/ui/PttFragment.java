package com.lesports.bike.settings.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
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
import com.lesports.bike.settings.widget.SwitchButton;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;

import bike.os.media.PTTManager;

/**
 * Created by gwball on 2016/5/25.
 */
public class PttFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private PTTManager mPttManager;

    private SwitchButton mSwitchButton;
    private TextView mStatusView;
    private ArrayList<String> mChannelsList = new ArrayList<>();

    @Override
    protected void initViewAndData() {
        mStatusView = (TextView) getActivity().findViewById(R.id.ptt_status);
        mSwitchButton = (SwitchButton) getActivity().findViewById(R.id.ptt_switch);



        mPttManager = (PTTManager) getActivity().getSystemService("ptt_service");
        double[] allChannels = mPttManager.getAllChannels();

        DecimalFormat df = new DecimalFormat("#.0000");
        for (int i = 0; i < allChannels.length; i++) {
            mChannelsList.add(df.format(allChannels[i]));
        }

        ListView listView = (ListView) getActivity().findViewById(R.id.ptt_listview);
        ListAdapter adapter = new ListAdapter(getActivity(), 0, mChannelsList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);



        int pttStatus = mPttManager.getCurChannelId();
        Log.d("wei.gao", "ptt: "+pttStatus);

        getActivity().registerReceiver(pttReceiver, new IntentFilter(PTTManager.ACTION_TUNE));
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ptt, container, false);
    }

    @Override
    protected String getTitleName() {
        return getResources().getString(R.string.ptt);
    }

    private BroadcastReceiver pttReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int index = intent.getIntExtra(PTTManager.ACTION_TUNE_CHANNEL_ID, 0);

        }

    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    private class ListAdapter extends ArrayAdapter<String> {
        public ListAdapter(Context context, int resource, ArrayList<String> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.ptt_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.ptt_item_channel);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.textView.setText(mChannelsList.get(position));
            return convertView;
        }
    }

    private static class ViewHolder {
        public TextView textView;
    }
}
