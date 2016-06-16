package com.lesports.bike.settings.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.lesports.bike.settings.R;

import java.lang.reflect.Field;

public class PickerDividerColorUtils {

    public static void setDatePickerDividerColor(Context context, DatePicker datePicker) {
        // 获取 mSpinners
        LinearLayout llFirst = (LinearLayout) datePicker.getChildAt(0);

        // 获取 NumberPicker
        LinearLayout mSpinners = (LinearLayout) llFirst.getChildAt(0);

        for (int i = 0; i < mSpinners.getChildCount(); i++) {
            NumberPicker picker = (NumberPicker) mSpinners.getChildAt(i);
            setNumberPickerDividerColor(context, picker);
        }
    }

    public static void setTImePickerDividerColor(Context context, TimePicker datePicker)
    {
        try
        {
            Class<?> clazz = Class.forName("com.android.internal.R$id");
            Field fieldHour = clazz.getField("hour");
            fieldHour.setAccessible(true);
            int hourId = fieldHour.getInt(null);
            NumberPicker hourNumberPicker = (NumberPicker) datePicker.findViewById(hourId);
            setNumberPickerDividerColor(context, hourNumberPicker);

            Field fieldminute = clazz.getField("minute");
            fieldminute.setAccessible(true);
            int minuteId = fieldminute.getInt(null);
            NumberPicker minuteNumberPicker = (NumberPicker) datePicker.findViewById(minuteId);
            setNumberPickerDividerColor(context, minuteNumberPicker);

            // 更改冒号颜色
            Field fieldDivider = clazz.getField("divider");
            fieldDivider.setAccessible(true);
            int dividerId = fieldDivider.getInt(null);
            TextView textView = (TextView) datePicker.findViewById(dividerId);
            textView.setTextColor(context.getResources().getColor(R.color.colorGreen));

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void setNumberPickerDividerColor(Context context, NumberPicker numberPicker) {
        NumberPicker picker = numberPicker;
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    // 设置分割线的颜色值
                    pf.set(picker,
                            new ColorDrawable(context.getResources().getColor(R.color.colorGreen)));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

}
