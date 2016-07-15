
package com.lesports.bike.settings.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lesports.bike.settings.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liutingting5 on 2016/5/27.
 */
public class BluetoothAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<BluetoothDevice> mDataList = new ArrayList<BluetoothDevice>();
    ViewHolder mHolder = null;

    public BluetoothAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void refreshList(List<BluetoothDevice> mDataList) {
        this.mDataList = mDataList;
        this.notifyDataSetChanged();
    }

    public void addList(List<BluetoothDevice> mDataList) {
        this.mDataList.addAll(mDataList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.bluetooth_list_item, null);
            convertView.setTag(mHolder);
            mHolder.bluetooth_name = (TextView) convertView.findViewById(R.id.bluetooth_name);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        final BluetoothDevice bean = mDataList.get(position);

        if (null == bean.getName() || bean.getName().equals(""))
            mHolder.bluetooth_name.setText(bean.getAddress());
        else
            mHolder.bluetooth_name.setText(bean.getName());

        return convertView;
    }

    class ViewHolder {
        private TextView bluetooth_name;
    }
}
