
package com.lesports.bike.settings.ui;

import android.app.AlarmManager;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.lesports.bike.settings.R;

import org.xmlpull.v1.XmlPullParserException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by gwball on 2016/5/25.
 */
public class TimeZoneFragment extends BaseFragment {

    private static final String TAG = "ZonePicker";

    private static final String KEY_DISPLAYNAME = "name"; // value: String
    private static final String KEY_GMT = "gmt"; // value: String
    private static final String XMLTAG_TIMEZONE = "timezone";

    private static final int HOURS_1 = 60 * 60000;

    private View timezoneView;
    private ListView timezoneListView;
    private List<HashMap<String, Object>> timezones;
    private List<String> placeIds = new ArrayList<String>();

    @Override
    protected void initViewAndData() {

    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        timezoneView = inflater.inflate(R.layout.fragment_timezones, container, false);
        timezoneListView = (ListView) timezoneView.findViewById(R.id.timezoneListView);
        timezones = getZones(getActivity());
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), timezones,
                R.layout.timezone_list_item, new String[] {
                        KEY_DISPLAYNAME, KEY_GMT
                },
                new int[] {
                        R.id.dispalyName, R.id.GMT
                });
        timezoneListView.setAdapter(adapter);
        timezoneListView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(final AdapterView<?> parent,
                    final View view, final int position, final long id)
            {
                String placeId = placeIds.get(position);
                setSystemTimezone(placeId);
                getActivity().finish();
            }
        });
        return timezoneView;
    }

    @Override
    protected String getTitleName() {
        return getResources().getString(R.string.timezone_set);
    }

    private List<HashMap<String, Object>> getZones(Context context) {
        List<HashMap<String, Object>> myData = new ArrayList<HashMap<String, Object>>();
        final long date = Calendar.getInstance().getTimeInMillis();
        try {
            XmlResourceParser xrp = context.getResources().getXml(R.xml.timezones);
            while (xrp.next() != XmlResourceParser.START_TAG)
                continue;
            xrp.next();
            while (xrp.getEventType() != XmlResourceParser.END_TAG) {
                while (xrp.getEventType() != XmlResourceParser.START_TAG) {
                    if (xrp.getEventType() == XmlResourceParser.END_DOCUMENT) {
                        return myData;
                    }
                    xrp.next();
                }
                if (xrp.getName().equals(XMLTAG_TIMEZONE)) {
                    String id = xrp.getAttributeValue(0);
                    String displayName = xrp.nextText();
                    addItem(myData, placeIds, id, displayName, date);
                }
                while (xrp.getEventType() != XmlResourceParser.END_TAG) {
                    xrp.next();
                }
                xrp.next();
            }
            xrp.close();
        } catch (XmlPullParserException xppe) {
            Log.e(TAG, "Ill-formatted timezones.xml file");
        } catch (java.io.IOException ioe) {
            Log.e(TAG, "Unable to read timezones.xml file");
        }

        return myData;
    }

    private void addItem(
            List<HashMap<String, Object>> myData, List<String> placeIds,
            String id, String displayName, long date) {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(KEY_DISPLAYNAME, displayName);

        final TimeZone tz = TimeZone.getTimeZone(id);
        final int offset = tz.getOffset(date);
        final int p = Math.abs(offset);
        final StringBuilder name = new StringBuilder();
        name.append("GMT");
        if (offset < 0) {
            name.append('-');
        } else {
            name.append('+');
        }
        name.append(p / (HOURS_1));
        name.append(':');
        int min = p / 60000;
        min %= 60;
        if (min < 10) {
            name.append('0');
        }
        name.append(min);
        map.put(KEY_GMT, name.toString());
        placeIds.add(id);
        myData.add(map);
    }

    private void setSystemTimezone(String placeId) {
        AlarmManager timeZone = (AlarmManager) getActivity().getSystemService(
                getActivity().ALARM_SERVICE);
        timeZone.setTimeZone(placeId);
    }
}
