package com.swan.timerangepicker;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.util.List;


/**
 * author swan
 * desc 自定义时间区间选择组件
 * email 2538096489@qq.com
 * time 2024/8/16 20:56
 */
public class TimeRangePickerDialog extends Dialog {
    private Context context;

    private String startTime;
    private String endTime;

    private int screenWidth;

    private TimePicker timePickerStart;
    private TimePicker timePickerEnd;

    private View cancelBtn, submitBtn;

    private ConfirmAction confirmAction;

    public TimeRangePickerDialog(Context context) {

        super(context);

        this.context = context;
    }

    public TimeRangePickerDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);

        this.context = context;
    }

    public TimeRangePickerDialog(Context context, int theme) {

        super(context, theme);

        this.context = context;
    }

    public TimeRangePickerDialog(Context context, String startAndEndTime, ConfirmAction confirmAction) {
        super(context, R.style.dialog);

        this.context = context;

        List<String> strings = CommonUtils.getRegEx(startAndEndTime, "\\d+:\\d+");
        if (!CommonUtils.isNull(strings) && strings.size() >= 2) {
            this.startTime = CommonUtils.getRegEx(startAndEndTime, "\\d+:\\d+").get(0);
            this.endTime = CommonUtils.getRegEx(startAndEndTime, "\\d+:\\d+").get(1);
        }

        this.confirmAction = confirmAction;

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels - getDensityValue(80, context);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_time_range_picker, null);
        setContentView(view);
        getWindow().setLayout(screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);

        initView();
        initData();
        setEvent();

    }

    private void initView() {

        timePickerStart = (TimePicker) findViewById(R.id.timePickerStart);
        timePickerEnd = (TimePicker) findViewById(R.id.timePickerEnd);
        cancelBtn = findViewById(R.id.cancelBtn);
        submitBtn = findViewById(R.id.submitBtn);

    }

    private void initData() {

        timePickerStart.setIs24HourView(true);
        timePickerEnd.setIs24HourView(true);
        timePickerStart.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        timePickerEnd.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);

        setTimePickerDividerColor(timePickerStart);
        setTimePickerDividerColor(timePickerEnd);

        if (!CommonUtils.isNull(startTime) && !CommonUtils.isNull(endTime)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePickerStart.setHour(Integer.parseInt(startTime.substring(0, startTime.indexOf(":"))));
                timePickerStart.setMinute(Integer.parseInt(startTime.substring(startTime.indexOf(":") + 1)));

                timePickerEnd.setHour(Integer.parseInt(endTime.substring(0, endTime.indexOf(":"))));
                timePickerEnd.setMinute(Integer.parseInt(endTime.substring(endTime.indexOf(":") + 1)));
            } else {
                timePickerStart.setCurrentHour(Integer.parseInt(startTime.substring(0, startTime.indexOf(":"))));
                timePickerStart.setCurrentMinute(Integer.parseInt(startTime.substring(startTime.indexOf(":") + 1)));

                timePickerEnd.setCurrentHour(Integer.parseInt(endTime.substring(0, endTime.indexOf(":"))));
                timePickerEnd.setCurrentMinute(Integer.parseInt(endTime.substring(endTime.indexOf(":") + 1)));
            }

        }

        timePickerStart.setOnTimeChangedListener((timePicker, hourOfDay, minute) -> {

            String h = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
            String m = minute < 10 ? "0" + minute : "" + minute;
            startTime = h + ":" + m;
        });
        timePickerEnd.setOnTimeChangedListener((timePicker, hourOfDay, minute) -> {
            String h = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
            String m = minute < 10 ? "0" + minute : "" + minute;
            endTime = h + ":" + m;
        });
    }

    private void setEvent() {

        cancelBtn.setOnClickListener(v -> {

            confirmAction.onLeftClick();
            dismiss();
        });

        submitBtn.setOnClickListener(v -> {

            confirmAction.onRightClick(startTime + " - " + endTime);
            dismiss();
        });

        this.setCanceledOnTouchOutside(true);

    }

    private void setTimePickerDividerColor(TimePicker timePicker) {
        LinearLayout llFirst = (LinearLayout) timePicker.getChildAt(0);
        LinearLayout mSpinners = (LinearLayout) llFirst.getChildAt(1);
        for (int i = 0; i < mSpinners.getChildCount(); i++) {
            if (mSpinners.getChildAt(i) instanceof NumberPicker) {
                Field[] pickerFields = NumberPicker.class.getDeclaredFields();
                setPickerMargin((NumberPicker) mSpinners.getChildAt(i));
                for (Field pf : pickerFields) {
                    if (pf.getName().equals("mSelectionDivider")) {
                        pf.setAccessible(true);
                        try {
                            pf.set(mSpinners.getChildAt(i), new ColorDrawable());
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
    }

    /**
     * 设置picker之间的间距
     */
    private void setPickerMargin(NumberPicker picker) {
        LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) picker.getLayoutParams();
        p.setMargins(-getDensityValue(16, context), 0, -getDensityValue(16, context), 0);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            p.setMarginStart(-getDensityValue(16, context));
            p.setMarginEnd(-getDensityValue(16, context));
        }
    }


    public interface ConfirmAction {

        void onLeftClick();

        void onRightClick(String startAndEndTime);
    }

    public static int getDensityValue(float value, Context activity) {

        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();

        return (int) Math.ceil(value * displayMetrics.density);
    }
}
