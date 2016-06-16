package com.lesports.bike.settings.ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.lesports.bike.settings.R;
import com.lesports.bike.settings.utils.ActivityUtils;
import com.lesports.bike.settings.utils.DateTimeUtils;
import com.lesports.bike.settings.utils.PickerDividerColorUtils;
import com.lesports.bike.settings.widget.SwitchButton;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by gwball on 2016/5/25.
 */
public class DateFragment extends BaseFragment implements View.OnClickListener {
    private View dateAndTimeView;
    private LinearLayout dateView;
    private LinearLayout timeView;
    private LinearLayout timezoneView;
    private SwitchButton autoSetDateButton;
    private SwitchButton autoSetTimezoneButton;
    private TextView setDateText;
    private TextView setTimeText;;
    private TextView setTimezoneText;
    private TextView dateText;
    private TextView timeText;
    private TextView timezoneText;

    private View dateDialogView;
    private TextView dateDisplay;
    private Button dateCancelButton;
    private Button dateFinishButton;

    private View timeDialogView;
    private Button timeCancelButton;
    private Button timeFinishButton;

    private static final int AUTO_DATE_TIME_ON = 1;
    private static final int AUTO_TIMEZONE_ON = 1;
    private static final Class timezoneClass = TimeZoneFragment.class;

    private int mYear;
    private int mMonth;
    private int mDay;
    private String mDayInWeek;
    private int mHour;
    private int mMinute;
    private String TAG = "DateFragment";

    @Override
    protected void initViewAndData() {
        DateTimeUtils.set24HourFormat(getActivity());
        setAutoDateTimeEnable(isAutoDateTimeEnable(getActivity()));
        setAutoTimezoneEnable(isAutoTimezoneEnable(getActivity()));
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        dateAndTimeView = inflater.inflate(R.layout.fragment_date_time, container, false);
        autoSetDateButton = (SwitchButton) dateAndTimeView
                .findViewById(R.id.auto_set_date_time_switch);
        autoSetTimezoneButton = (SwitchButton) dateAndTimeView
                .findViewById(R.id.auto_set_timezone_switch);
        dateView = (LinearLayout) dateAndTimeView.findViewById(R.id.manual_set_date_section);
        setDateText = (TextView) dateAndTimeView.findViewById(R.id.setDateText);
        dateText = (TextView) dateAndTimeView.findViewById(R.id.dateText);

        timeView = (LinearLayout) dateAndTimeView.findViewById(R.id.manual_set_time_section);
        setTimeText = (TextView) dateAndTimeView.findViewById(R.id.setTimeText);
        timeText = (TextView) dateAndTimeView.findViewById(R.id.timeText);

        timezoneView = (LinearLayout) dateAndTimeView
                .findViewById(R.id.manual_set_timezone_section);
        setTimezoneText = (TextView) dateAndTimeView.findViewById(R.id.setTimezoneText);
        timezoneText = (TextView) dateAndTimeView.findViewById(R.id.timezoneText);

        autoSetDateButton.setOnClickListener(this);
        autoSetTimezoneButton.setOnClickListener(this);
        dateView.setOnClickListener(this);
        timeView.setOnClickListener(this);
        timezoneView.setOnClickListener(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        getActivity().registerReceiver(dateTimeAndTimezoneReceiver, filter);
        return dateAndTimeView;
    }

    @Override
    protected String getTitleName() {
        return getResources().getString(R.string.date_time_title);
    }

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        switch (view.getId()) {
            case R.id.auto_set_date_time_switch:
                setAutoDateTimeEnable(!isAutoDateTimeEnable(getActivity()));
                break;
            case R.id.auto_set_timezone_switch:
                setAutoTimezoneEnable(!isAutoTimezoneEnable(getActivity()));
                break;
            case R.id.manual_set_date_section:
                if (!isAutoDateTimeEnable(getActivity())) {
                    setDate();
                }
                break;
            case R.id.manual_set_time_section:
                if (!isAutoDateTimeEnable(getActivity())) {
                    setTime();
                }
                break;
            case R.id.manual_set_timezone_section:
                if (!isAutoTimezoneEnable(getActivity())) {
                    ActivityUtils.startFragmentActivity(getActivity(), timezoneClass);
                }
                break;
            default:
                break;
        }
    }

    private BroadcastReceiver dateTimeAndTimezoneReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            Log.v(TAG, action);
            if (action.equals(Intent.ACTION_TIME_CHANGED) ||
                    action.equals(Intent.ACTION_TIME_TICK)) {
                dateText.setText(DateTimeUtils.getCorrentDate(getActivity()));
                timeText.setText(DateTimeUtils.getCorrentTime());
            }
            if (action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                dateText.setText(DateTimeUtils.getCorrentDate(getActivity()));
                timeText.setText(DateTimeUtils.getCorrentTime());
                timezoneText.setText(DateTimeUtils.getCorrentTimezone());
            }
        }

    };

    public void setDate() {
        dateDialogView = View.inflate(getActivity(), R.layout.date_dialog, null);
        final DatePicker datePicker = (DatePicker) dateDialogView
                .findViewById(R.id.new_set_date_picker);
        dateDisplay = (TextView) dateDialogView.findViewById(R.id.choose_date);
        dateCancelButton = (Button) dateDialogView.findViewById(R.id.choose_date_cancel_button);
        dateFinishButton = (Button) dateDialogView.findViewById(R.id.choose_date_finish_button);

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        Date date = new Date(mYear - 1900, mMonth, mDay);
        mDayInWeek = DateTimeUtils.dateToWeek(date);
        String dateDisplayString = DateTimeUtils.getCorrentDate(getActivity()) + mDayInWeek;
        dateDisplay.setText(dateDisplayString);
        datePicker.init(mYear, mMonth, mDay, new OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker arg0, int year, int month, int day) {
                // TODO Auto-generated method stub
                DateFragment.this.mYear = year;
                DateFragment.this.mMonth = month;
                DateFragment.this.mDay = day;
                Date date = new Date(year - 1900, month, day);
                mDayInWeek = DateTimeUtils.dateToWeek(date);
                String displayString = DateTimeUtils.getDate(getActivity(), year, month + 1, day) + mDayInWeek;
                dateDisplay.setText(displayString);
            }
        });
        final AlertDialog dateDialog;
        dateDialog = new AlertDialog.Builder(DateFragment.this.getActivity()).create();
        dateDialog.show();
        PickerDividerColorUtils.setDatePickerDividerColor(getActivity(), datePicker);
        android.view.WindowManager.LayoutParams lp =
                dateDialog.getWindow().getAttributes();
        lp.y = 176;
        lp.x = 0;
        dateDialog.getWindow().setAttributes(lp);
        dateDialog.getWindow().setLayout(android.view.WindowManager.LayoutParams.FILL_PARENT,
                android.view.WindowManager.LayoutParams.WRAP_CONTENT);
        dateDialog.getWindow().setContentView(dateDialogView);

        dateCancelButton.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dateDialog.dismiss();
                    }
                });

        dateFinishButton.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DateTimeUtils.setSystemDate(mYear, mMonth, mDay);
                        dateDialog.dismiss();
                    }
                });

    }

    public void setTime() {
        timeDialogView = View.inflate(getActivity(), R.layout.time_dialog, null);
        timeCancelButton = (Button) timeDialogView.findViewById(R.id.choose_time_cancel_button);
        timeFinishButton = (Button) timeDialogView.findViewById(R.id.choose_time_finish_button);
        final TimePicker timePicker = (TimePicker) timeDialogView
                .findViewById(R.id.new_set_time_picker);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {

            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
                // TODO Auto-generated method stub
                DateFragment.this.mHour = hour;
                DateFragment.this.mMinute = minute;
            }
        });
        final AlertDialog timeDialog;
        timeDialog = new AlertDialog.Builder(DateFragment.this.getActivity()).create();
        timeDialog.show();
        PickerDividerColorUtils.setTImePickerDividerColor(getActivity(), timePicker);
        android.view.WindowManager.LayoutParams lp =
                timeDialog.getWindow().getAttributes();
        lp.y = 176;
        lp.x = 0;
        timeDialog.getWindow().setAttributes(lp);
        timeDialog.getWindow().setLayout(android.view.WindowManager.LayoutParams.FILL_PARENT,
                android.view.WindowManager.LayoutParams.WRAP_CONTENT);
        timeDialog.getWindow().setContentView(timeDialogView);

        timeCancelButton.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timeDialog.dismiss();
                    }
                });

        timeFinishButton.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DateTimeUtils.setSystemTime(mHour, mMinute);
                        timeDialog.dismiss();
                    }
                });

    }

    private Boolean isAutoDateTimeEnable(Context context) {
        Boolean nAutoTimeStatus = true;
        try {
            nAutoTimeStatus = (Settings.Global.getInt(
                    getActivity().getContentResolver(), Settings.Global.AUTO_TIME)) ==
                    AUTO_DATE_TIME_ON;
        } catch (SettingNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return nAutoTimeStatus;
    }

    private Boolean isAutoTimezoneEnable(Context context) {
        Boolean nAutoTimezoneStatus = true;
        try {
            nAutoTimezoneStatus = (Settings.Global.getInt(
                    getActivity().getContentResolver(), Settings.Global.AUTO_TIME_ZONE)) ==
                    AUTO_TIMEZONE_ON;
        } catch (SettingNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return nAutoTimezoneStatus;
    }

    private void setAutoDateTimeEnable(Boolean state) {
        if (state) {
            Settings.Global.putInt(
                    getActivity().getContentResolver(), Settings.Global.AUTO_TIME, 1);
            autoSetDateButton.setSwitchStatus(true);
            setDateTimeSecitonEnable(false);
        } else {
            Settings.Global.putInt(
                    getActivity().getContentResolver(), Settings.Global.AUTO_TIME, 0);
            autoSetDateButton.setSwitchStatus(false);
            setDateTimeSecitonEnable(true);
        }
    }

    private void setAutoTimezoneEnable(Boolean state) {
        if (state) {
            Settings.Global.putInt(
                    getActivity().getContentResolver(), Settings.Global.AUTO_TIME_ZONE, 1);
            autoSetTimezoneButton.setSwitchStatus(true);
            setTimezoneSecitonEnable(false);
        } else {
            Settings.Global.putInt(
                    getActivity().getContentResolver(), Settings.Global.AUTO_TIME_ZONE, 0);
            autoSetTimezoneButton.setSwitchStatus(false);
            setTimezoneSecitonEnable(true);
        }
    }

    private void setDateTimeSecitonEnable(Boolean state) {
        if (state) {
            setDateText.setTextColor(getResources().getColor(R.color.colorWhite));
            dateText.setTextColor(getResources().getColor(R.color.colorWhite));
            setTimeText.setTextColor(getResources().getColor(R.color.colorWhite));
            timeText.setTextColor(getResources().getColor(R.color.colorWhite));

        } else {
            dateText.setText(DateTimeUtils.getCorrentDate(getActivity()));
            timeText.setText(DateTimeUtils.getCorrentTime());
            setDateText.setTextColor(getResources().getColor(R.color.colorGray6));
            dateText.setTextColor(getResources().getColor(R.color.colorGray6));
            setTimeText.setTextColor(getResources().getColor(R.color.colorGray6));
            timeText.setTextColor(getResources().getColor(R.color.colorGray6));
        }
    }

    private void setTimezoneSecitonEnable(Boolean state) {
        if (state) {
            setTimezoneText.setTextColor(getResources().getColor(R.color.colorWhite));
            timezoneText.setTextColor(getResources().getColor(R.color.colorWhite));
        } else {
            timezoneText.setText(DateTimeUtils.getCorrentTimezone());
            setTimezoneText.setTextColor(getResources().getColor(R.color.colorGray6));
            timezoneText.setTextColor(getResources().getColor(R.color.colorGray6));
        }
    }

}
