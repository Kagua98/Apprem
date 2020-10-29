package com.application.apprem.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.application.apprem.R;
import com.application.apprem.adapters.ExamsAdapter;
import com.application.apprem.adapters.FragmentsTabAdapter;
import com.application.apprem.adapters.HomeworkAdapter;
import com.application.apprem.adapters.NotesAdapter;
import com.application.apprem.adapters.TeachersAdapter;
import com.application.apprem.fragments.WeekdayFragment;
import com.application.apprem.models.Exam;
import com.application.apprem.models.Homework;
import com.application.apprem.models.Note;
import com.application.apprem.models.Teacher;
import com.application.apprem.models.Week;
import com.application.apprem.receivers.DoNotDisturbReceiversKt;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.pd.chocobar.ChocoBar;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import me.jfenn.colorpickerdialog.dialogs.ColorPickerDialog;
import me.jfenn.colorpickerdialog.interfaces.OnColorPickedListener;
import me.jfenn.colorpickerdialog.views.picker.RGBPickerView;

public class AlertDialogsHelper {

    public static void getEditSubjectDialog(@NonNull final AppCompatActivity activity, @NonNull final View alertLayout, @NonNull final Runnable runOnSafe, @NonNull final Week week) {
        final HashMap<Integer, EditText> editTextHashs = new HashMap<>();
        final EditText subject = alertLayout.findViewById(R.id.subject_dialog);
        editTextHashs.put(R.string.subject, subject);
        final EditText teacher = alertLayout.findViewById(R.id.teacher_dialog);
//        editTextHashs.put(R.string.teacher, teacher);
        final EditText room = alertLayout.findViewById(R.id.room_dialog);
//        editTextHashs.put(R.string.room, room);
        final TextView from_time = alertLayout.findViewById(R.id.from_time);
        final TextView to_time = alertLayout.findViewById(R.id.to_time);
        final TextView from_hour = alertLayout.findViewById(R.id.from_hour);
        final TextView to_hour = alertLayout.findViewById(R.id.to_hour);
        final Button select_color = alertLayout.findViewById(R.id.select_color);
        select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(week.getColor(), Color.WHITE, Color.BLACK));

        subject.setText(week.getSubject());
        teacher.setText(week.getTeacher());
        room.setText(week.getRoom());
        from_time.setText(week.getFromTime());
        to_time.setText(week.getToTime());
        from_hour.setText("" + WeekUtils.getMatchingScheduleBegin(week.getFromTime(), activity));
        to_hour.setText("" + WeekUtils.getMatchingScheduleEnd(week.getToTime(), activity));
        select_color.setBackgroundColor(week.getColor() != 0 ? week.getColor() : Color.WHITE);

        from_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mHour = Integer.parseInt(week.getFromTime().substring(0, week.getFromTime().indexOf(":")));
                int mMinute = Integer.parseInt(week.getFromTime().substring(week.getFromTime().indexOf(":") + 1));
                TimePickerDialog timePickerDialog = new TimePickerDialog(activity,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                from_time.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
                                week.setFromTime(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
                                from_hour.setText("" + WeekUtils.getMatchingScheduleBegin(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute), activity));
                                try {
                                    int value = WeekUtils.getMatchingScheduleBegin(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute), activity);
                                    if (Integer.parseInt(to_hour.getText().toString()) < value && PreferenceUtil.isIntelligentAutoFill(activity)) {
                                        to_time.setText(WeekUtils.getMatchingTimeEnd(value, activity));
                                        week.setToTime(WeekUtils.getMatchingTimeEnd(value, activity));
                                        to_hour.setText("" + value);
                                    }
                                } catch (Exception ignore) {
                                }
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.setTitle(R.string.choose_time);
                timePickerDialog.show();
            }
        });

        to_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mHour = Integer.parseInt(week.getToTime().substring(0, week.getToTime().indexOf(":")));
                int mMinute = Integer.parseInt(week.getToTime().substring(week.getToTime().indexOf(":") + 1));
                TimePickerDialog timePickerDialog = new TimePickerDialog(activity,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute1) {
                                to_time.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1));
                                week.setToTime(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1));
                                to_hour.setText("" + WeekUtils.getMatchingScheduleEnd(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1), activity));
                                try {
                                    int value = WeekUtils.getMatchingScheduleEnd(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1), activity);
                                    if (Integer.parseInt(from_hour.getText().toString()) > value && PreferenceUtil.isIntelligentAutoFill(activity)) {
                                        from_time.setText(WeekUtils.getMatchingTimeBegin(value, activity));
                                        week.setFromTime(WeekUtils.getMatchingTimeBegin(value, activity));
                                        from_hour.setText("" + value);
                                    }
                                } catch (Exception ignore) {
                                }
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.setTitle(R.string.choose_time);
                timePickerDialog.show();
            }
        });

        from_hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NumberPicker numberPicker = new NumberPicker(activity);
                numberPicker.setMaxValue(15);
                numberPicker.setMinValue(1);
                numberPicker.setValue(Integer.parseInt(from_hour.getText().toString()));
                new MaterialDialog.Builder(activity)
                        .customView(numberPicker, false)
                        .positiveText(R.string.select)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog vi, @NonNull DialogAction w) {
                                int value = numberPicker.getValue();
                                from_time.setText(WeekUtils.getMatchingTimeBegin(value, activity));
                                week.setFromTime(WeekUtils.getMatchingTimeBegin(value, activity));
                                from_hour.setText("" + value);
                                try {
                                    if (Integer.parseInt(to_hour.getText().toString()) < value && PreferenceUtil.isIntelligentAutoFill(activity)) {
                                        to_time.setText(WeekUtils.getMatchingTimeEnd(value, activity));
                                        week.setToTime(WeekUtils.getMatchingTimeEnd(value, activity));
                                        to_hour.setText("" + value);
                                    }
                                } catch (Exception ignore) {
                                }
                            }
                        })
                        .show();
            }
        });

        to_hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NumberPicker numberPicker = new NumberPicker(activity);
                numberPicker.setMaxValue(15);
                numberPicker.setMinValue(1);
                numberPicker.setValue(Integer.parseInt(to_hour.getText().toString()));
                new MaterialDialog.Builder(activity)
                        .customView(numberPicker, false)
                        .positiveText(R.string.select)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog vi, @NonNull DialogAction w) {
                                int value = numberPicker.getValue();
                                to_time.setText(WeekUtils.getMatchingTimeEnd(value, activity));
                                week.setToTime(WeekUtils.getMatchingTimeEnd(value, activity));
                                to_hour.setText("" + value);
                                try {
                                    if (Integer.parseInt(from_hour.getText().toString()) > value && PreferenceUtil.isIntelligentAutoFill(activity)) {
                                        from_time.setText(WeekUtils.getMatchingTimeBegin(value, activity));
                                        week.setFromTime(WeekUtils.getMatchingTimeBegin(value, activity));
                                        from_hour.setText("" + value);
                                    }
                                } catch (Exception ignore) {
                                }
                            }
                        })
                        .show();
            }
        });

        select_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerDialog()
                        .withColor(((ColorDrawable) select_color.getBackground()).getColor()) // the default / initial color
                        .withPresets(ColorPalette.PRIMARY_COLORS)
                        .withTitle(activity.getString(R.string.choose_color))
                        .withTheme(PreferenceUtil.getGeneralTheme(activity))
                        .withCornerRadius(16)
                        .withAlphaEnabled(false)
                        .withListener(new OnColorPickedListener<ColorPickerDialog>() {
                            @Override
                            public void onColorPicked(@Nullable ColorPickerDialog dialog, int color) {
                                // a color has been picked; use it
                                select_color.setBackgroundColor(color);
                                select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(color, Color.WHITE, Color.BLACK));
                            }
                        })
                        .clearPickers()
                        .withPresets(ColorPalette.PRIMARY_COLORS)
                        .withPicker(RGBPickerView.class)
                        .show(activity.getSupportFragmentManager(), "colorPicker");
            }
        });


        subject.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE ||
                                event != null &&
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (event == null || !event.isShiftPressed()) {
                                // the user is done typing.
                                //AutoFill other fields
                                for (Week w : WeekUtils.getAllWeeks(new DbHelper(activity))) {
                                    if (w.getSubject().equalsIgnoreCase(v.getText().toString())) {
                                        if (teacher.getText().toString().trim().isEmpty())
                                            teacher.setText(w.getTeacher());
                                        if (room.getText().toString().trim().isEmpty())
                                            room.setText(w.getRoom());
                                        select_color.setBackgroundColor(w.getColor());
                                        select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(w.getColor(), Color.WHITE, Color.BLACK));
                                    }
                                }

                                return true;
                            }
                        }
                        return false;
                    }
                }
        );
        subject.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    for (Week w : WeekUtils.getAllWeeks(new DbHelper(activity))) {
                        if (w.getSubject().equalsIgnoreCase(((EditText) v).getText().toString())) {
                            if (teacher.getText().toString().trim().isEmpty())
                                teacher.setText(w.getTeacher());
                            if (room.getText().toString().trim().isEmpty())
                                room.setText(w.getRoom());
                            select_color.setBackgroundColor(w.getColor());
                            select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(w.getColor(), Color.WHITE, Color.BLACK));
                        }
                    }
                }
            }
        });

        final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle(R.string.edit_subject);
        alert.setCancelable(false);
        final Button cancel = alertLayout.findViewById(R.id.cancel);
        final Button save = alertLayout.findViewById(R.id.save);
        alert.setView(alertLayout);
        final AlertDialog dialog = alert.create();
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subject.getText().clear();
                teacher.getText().clear();
                room.getText().clear();
                from_time.setText(R.string.select_start_time);
                to_time.setText(R.string.select_end_time);
                from_hour.setText(R.string.lesson);
                to_hour.setText(R.string.lesson);
                select_color.setBackgroundColor(Color.WHITE);
                subject.requestFocus();
                from_hour.setText(R.string.lesson);
                to_hour.setText(R.string.lesson);
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(subject.getText()) /*|| TextUtils.isEmpty(teacher.getText()) || TextUtils.isEmpty(room.getText())*/) {
                    for (Map.Entry<Integer, EditText> entry : editTextHashs.entrySet()) {
                        if (TextUtils.isEmpty(entry.getValue().getText())) {
                            entry.getValue().setError(activity.getResources().getString(entry.getKey()) + " " + activity.getResources().getString(R.string.field_error));
                            entry.getValue().requestFocus();
                        }
                    }
                } else if (!from_time.getText().toString().matches(".*\\d+.*") || !to_time.getText().toString().matches(".*\\d+.*")) {
                    Snackbar.make(alertLayout, R.string.time_error, Snackbar.LENGTH_LONG).show();
                } else {
                    DbHelper db = new DbHelper(activity);
                    ColorDrawable buttonColor = (ColorDrawable) select_color.getBackground();
                    week.setSubject(subject.getText().toString());
                    week.setTeacher(teacher.getText().toString());
                    week.setRoom(room.getText().toString());
                    week.setColor(buttonColor.getColor());
                    db.updateWeek(week);
                    runOnSafe.run();
                    databaseChanged(activity);
                    dialog.dismiss();
                }
            }
        });
    }

    public static void getAddSubjectDialog(@NonNull final AppCompatActivity activity, @NonNull final View alertLayout, @NonNull final FragmentsTabAdapter adapter, @NonNull final ViewPager viewPager) {
        final HashMap<Integer, EditText> editTextHashs = new HashMap<>();
        final EditText subject = alertLayout.findViewById(R.id.subject_dialog);
        subject.requestFocus();
        editTextHashs.put(R.string.subject, subject);
        final EditText teacher = alertLayout.findViewById(R.id.teacher_dialog);
//        editTextHashs.put(R.string.teacher, teacher);
        final EditText room = alertLayout.findViewById(R.id.room_dialog);
//        editTextHashs.put(R.string.room, room);

        final TextView from_time = alertLayout.findViewById(R.id.from_time);
        final TextView to_time = alertLayout.findViewById(R.id.to_time);
        final TextView from_hour = alertLayout.findViewById(R.id.from_hour);
        final TextView to_hour = alertLayout.findViewById(R.id.to_hour);

        from_hour.setText(R.string.select_start_time);
        to_hour.setText(R.string.select_end_time);

        final Button select_color = alertLayout.findViewById(R.id.select_color);
//        select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(((ColorDrawable) select_color.getBackground()).getColor(), Color.WHITE, Color.BLACK));

        final Week week = new Week();

        from_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mHour, mMinute;
                try {
                    String time = from_time.getText().toString();
                    mHour = Integer.parseInt(time.substring(0, time.indexOf(":")));
                    mMinute = Integer.parseInt(time.substring(time.indexOf(":") + 1));
                } catch (Exception ignore) {
                    final Calendar c = Calendar.getInstance();
                    mHour = c.get(Calendar.HOUR_OF_DAY);
                    mMinute = c.get(Calendar.MINUTE);
                }
                TimePickerDialog timePickerDialog = new TimePickerDialog(activity,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                from_time.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
                                week.setFromTime(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
                                from_hour.setText("" + WeekUtils.getMatchingScheduleBegin(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute), activity));
                                try {
                                    int value = WeekUtils.getMatchingScheduleBegin(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute), activity);
                                    if (Integer.parseInt(to_hour.getText().toString()) < value && PreferenceUtil.isIntelligentAutoFill(activity)) {
                                        to_time.setText(WeekUtils.getMatchingTimeEnd(value, activity));
                                        week.setToTime(WeekUtils.getMatchingTimeEnd(value, activity));
                                        to_hour.setText("" + value);
                                    }
                                } catch (Exception ignore) {
                                }
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.setTitle(R.string.choose_time);
                timePickerDialog.show();
            }
        });

        to_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour, minute;
                try {
                    String time = WeekUtils.getMatchingTimeEnd(WeekUtils.getMatchingScheduleBegin(from_time.getText().toString(), activity), activity);
                    hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
                    minute = Integer.parseInt(time.substring(time.indexOf(":") + 1));
                } catch (Exception ignore) {
                    final Calendar c = Calendar.getInstance();
                    hour = c.get(Calendar.HOUR_OF_DAY);
                    minute = c.get(Calendar.MINUTE);
                }
                TimePickerDialog timePickerDialog = new TimePickerDialog(activity,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute1) {
                                to_time.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1));
                                week.setToTime(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1));
                                to_hour.setText("" + WeekUtils.getMatchingScheduleEnd(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1), activity));
                                try {
                                    int value = WeekUtils.getMatchingScheduleEnd(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1), activity);
                                    if (Integer.parseInt(from_hour.getText().toString()) > value && PreferenceUtil.isIntelligentAutoFill(activity)) {
                                        from_time.setText(WeekUtils.getMatchingTimeBegin(value, activity));
                                        week.setFromTime(WeekUtils.getMatchingTimeBegin(value, activity));
                                        from_hour.setText("" + value);
                                    }
                                } catch (Exception ignore) {
                                }
                            }
                        }, hour, minute, true);
                timePickerDialog.setTitle(R.string.choose_time);
                timePickerDialog.show();
            }
        });

        from_hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NumberPicker numberPicker = new NumberPicker(activity);
                numberPicker.setMaxValue(15);
                numberPicker.setMinValue(1);
                try {
                    numberPicker.setValue(Integer.parseInt(from_hour.getText().toString()));
                } catch (Exception ignore) {
                }
                new MaterialDialog.Builder(activity)
                        .customView(numberPicker, false)
                        .positiveText(R.string.select)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog vi, @NonNull DialogAction w) {
                                int value = numberPicker.getValue();
                                from_time.setText(WeekUtils.getMatchingTimeBegin(value, activity));
                                week.setFromTime(WeekUtils.getMatchingTimeBegin(value, activity));
                                from_hour.setText("" + value);
                                try {
                                    if (Integer.parseInt(to_hour.getText().toString()) < value && PreferenceUtil.isIntelligentAutoFill(activity)) {
                                        to_time.setText(WeekUtils.getMatchingTimeEnd(value, activity));
                                        week.setToTime(WeekUtils.getMatchingTimeEnd(value, activity));
                                        to_hour.setText("" + value);
                                    }
                                } catch (Exception ignore) {
                                }
                            }
                        })
                        .show();
            }
        });

        to_hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NumberPicker numberPicker = new NumberPicker(activity);
                numberPicker.setMaxValue(15);
                numberPicker.setMinValue(1);
                try {
                    numberPicker.setValue(Integer.parseInt(from_hour.getText().toString()) + 1);
                } catch (Exception ignore) {
                }
                new MaterialDialog.Builder(activity)
                        .customView(numberPicker, false)
                        .positiveText(R.string.select)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog vi, @NonNull DialogAction w) {
                                int value = numberPicker.getValue();
                                to_time.setText(WeekUtils.getMatchingTimeEnd(value, activity));
                                week.setToTime(WeekUtils.getMatchingTimeEnd(value, activity));
                                to_hour.setText("" + value);
                                try {
                                    if (Integer.parseInt(from_hour.getText().toString()) > value && PreferenceUtil.isIntelligentAutoFill(activity)) {
                                        from_time.setText(WeekUtils.getMatchingTimeBegin(value, activity));
                                        week.setFromTime(WeekUtils.getMatchingTimeBegin(value, activity));
                                        from_hour.setText("" + value);
                                    }
                                } catch (Exception ignore) {
                                }
                            }
                        })
                        .show();
            }
        });

        select_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerDialog()
                        .withColor(((ColorDrawable) select_color.getBackground()).getColor()) // the default / initial color
                        .withTitle(activity.getString(R.string.choose_color))
                        .withTheme(PreferenceUtil.getGeneralTheme(activity))
                        .withCornerRadius(16)
                        .withAlphaEnabled(false)
                        .withListener(new OnColorPickedListener<ColorPickerDialog>() {
                            @Override
                            public void onColorPicked(@Nullable ColorPickerDialog dialog, int color) {
                                // a color has been picked; use it
                                select_color.setBackgroundColor(color);
                                select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(color, Color.WHITE, Color.BLACK));
                            }
                        })
                        .clearPickers()
                        .withPresets(ColorPalette.PRIMARY_COLORS)
                        .withPicker(RGBPickerView.class)
                        .show(activity.getSupportFragmentManager(), "colorPicker");
            }
        });

        subject.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE ||
                                event != null &&
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (event == null || !event.isShiftPressed()) {
                                // the user is done typing.
                                //AutoFill other fields
                                for (Week w : WeekUtils.getAllWeeks(new DbHelper(activity))) {
                                    if (w.getSubject().equalsIgnoreCase(v.getText().toString())) {
                                        if (teacher.getText().toString().trim().isEmpty())
                                            teacher.setText(w.getTeacher());
                                        if (room.getText().toString().trim().isEmpty())
                                            room.setText(w.getRoom());
                                        select_color.setBackgroundColor(w.getColor());
                                        select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(w.getColor(), Color.WHITE, Color.BLACK));
                                    }
                                }

                                return true;
                            }
                        }
                        return false;
                    }
                }
        );
        subject.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    for (Week w : WeekUtils.getAllWeeks(new DbHelper(activity))) {
                        if (w.getSubject().equalsIgnoreCase(((EditText) v).getText().toString())) {
                            if (teacher.getText().toString().trim().isEmpty())
                                teacher.setText(w.getTeacher());
                            if (room.getText().toString().trim().isEmpty())
                                room.setText(w.getRoom());
                            select_color.setBackgroundColor(w.getColor());
                            select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(w.getColor(), Color.WHITE, Color.BLACK));
                        }
                    }
                }
            }
        });

        final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle(R.string.add_subject);
        alert.setCancelable(false);
        final Button cancel = alertLayout.findViewById(R.id.cancel);
        Button submit = alertLayout.findViewById(R.id.save);
        alert.setView(alertLayout);
        final AlertDialog dialog = alert.create();

        //Preselection
        FloatingActionButton fab = activity.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PreferenceUtil.showTimes(activity)) {
                    from_hour.setVisibility(View.GONE);
                    from_time.setVisibility(View.VISIBLE);
                    from_time.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, .7f));

                    to_hour.setVisibility(View.GONE);
                    to_time.setVisibility(View.VISIBLE);
                    to_time.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, .7f));
                } else {
                    from_hour.setVisibility(View.VISIBLE);
                    from_time.setVisibility(View.GONE);
                    from_hour.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, .7f));

                    to_hour.setVisibility(View.VISIBLE);
                    to_time.setVisibility(View.GONE);
                    to_hour.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, .7f));
                }

                if (PreferenceUtil.isPreselectionList(activity)) {
                    final ArrayList<Week> customWeeks = WeekUtils.getPreselection(activity);

                    ArrayList<String> subjects = new ArrayList<>();
                    for (Week w : customWeeks) {
                        subjects.add(w.getSubject());
                    }

                    new MaterialDialog.Builder(activity)
                            .title(R.string.pick_a_subject)
                            .items(subjects)
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog dialog1, View view1, int which, CharSequence text) {
                                    Week w = customWeeks.get(which);
                                    subject.setText(w.getSubject());
                                    teacher.setText(w.getTeacher());
                                    room.setText(w.getRoom());
                                    select_color.setBackgroundColor(w.getColor());
                                    select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(w.getColor(), Color.WHITE, Color.BLACK));

                                    String key = ((WeekdayFragment) adapter.getItem(viewPager.getCurrentItem())).getKey();
                                    ArrayList<Week> weeks = new DbHelper(activity).getWeek(key);
                                    int valueNew = 1;
                                    if (weeks.size() > 0) {
                                        valueNew = WeekUtils.getMatchingScheduleEnd(weeks.get(weeks.size() - 1).getToTime(), activity) + 1;
                                    }
                                    from_time.setText(WeekUtils.getMatchingTimeBegin(valueNew, activity));
                                    week.setFromTime(WeekUtils.getMatchingTimeBegin(valueNew, activity));
                                    from_hour.setText("" + valueNew);
                                    to_time.setText(WeekUtils.getMatchingTimeEnd(valueNew, activity));
                                    week.setToTime(WeekUtils.getMatchingTimeEnd(valueNew, activity));
                                    to_hour.setText("" + valueNew);

                                    dialog.show();
                                }
                            })
                            .positiveText(R.string.new_subject)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog1, @NonNull DialogAction which) {
                                    String key = ((WeekdayFragment) adapter.getItem(viewPager.getCurrentItem())).getKey();
                                    ArrayList<Week> weeks = new DbHelper(activity).getWeek(key);
                                    int valueNew = 1;
                                    if (weeks.size() > 0) {
                                        valueNew = WeekUtils.getMatchingScheduleEnd(weeks.get(weeks.size() - 1).getToTime(), activity) + 1;
                                    }
                                    from_time.setText(WeekUtils.getMatchingTimeBegin(valueNew, activity));
                                    week.setFromTime(WeekUtils.getMatchingTimeBegin(valueNew, activity));
                                    from_hour.setText("" + valueNew);
                                    to_time.setText(WeekUtils.getMatchingTimeEnd(valueNew, activity));
                                    week.setToTime(WeekUtils.getMatchingTimeEnd(valueNew, activity));
                                    to_hour.setText("" + valueNew);

                                    dialog.show();
                                }
                            })
                            .show();
                } else {
                    String key = ((WeekdayFragment) adapter.getItem(viewPager.getCurrentItem())).getKey();
                    ArrayList<Week> weeks = new DbHelper(activity).getWeek(key);
                    int valueNew = 1;
                    if (weeks.size() > 0) {
                        valueNew = WeekUtils.getMatchingScheduleEnd(weeks.get(weeks.size() - 1).getToTime(), activity) + 1;
                    }
                    from_time.setText(WeekUtils.getMatchingTimeBegin(valueNew, activity));
                    week.setFromTime(WeekUtils.getMatchingTimeBegin(valueNew, activity));
                    from_hour.setText("" + valueNew);
                    to_time.setText(WeekUtils.getMatchingTimeEnd(valueNew, activity));
                    week.setToTime(WeekUtils.getMatchingTimeEnd(valueNew, activity));
                    to_hour.setText("" + valueNew);

                    dialog.show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subject.getText().clear();
                teacher.getText().clear();
                room.getText().clear();
                from_time.setText(R.string.select_start_time);
                to_time.setText(R.string.select_end_time);
                from_hour.setText(R.string.select_start_time);
                to_hour.setText(R.string.select_end_time);
                select_color.setBackgroundColor(Color.WHITE);
                subject.requestFocus();
                dialog.dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(subject.getText()) /*|| TextUtils.isEmpty(teacher.getText()) || TextUtils.isEmpty(room.getText())*/) {
                    for (Map.Entry<Integer, EditText> entry : editTextHashs.entrySet()) {
                        if (TextUtils.isEmpty(entry.getValue().getText())) {
                            entry.getValue().setError(activity.getResources().getString(entry.getKey()) + " " + activity.getResources().getString(R.string.field_error));
                            entry.getValue().requestFocus();
                        }
                    }
                } else if (!from_time.getText().toString().matches(".*\\d+.*") || !to_time.getText().toString().matches(".*\\d+.*")) {
                    Snackbar.make(alertLayout, R.string.time_error, Snackbar.LENGTH_LONG).show();
                } else {
                    ColorDrawable buttonColor = (ColorDrawable) select_color.getBackground();
                    week.setSubject(subject.getText().toString());
                    week.setFragment(((WeekdayFragment) adapter.getItem(viewPager.getCurrentItem())).getKey());
                    week.setTeacher(teacher.getText().toString());
                    week.setRoom(room.getText().toString());
                    week.setColor(buttonColor.getColor());
                    new DbHelper(activity).insertWeek(week);
                    adapter.notifyDataSetChanged();
                    databaseChanged(activity);
                    cancel.performClick();
                }
            }
        });
    }

    public static void getEditHomeworkDialog(@NonNull final AppCompatActivity activity, @NonNull final View alertLayout, @NonNull final ArrayList<Homework> adapter, @NonNull final ListView listView, int listposition) {
        final HashMap<Integer, EditText> editTextHashs = new HashMap<>();
        final EditText subject = alertLayout.findViewById(R.id.subjecthomework);
        editTextHashs.put(R.string.subject, subject);
        final EditText description = alertLayout.findViewById(R.id.descriptionhomework);
        editTextHashs.put(R.string.description, description);
        final TextView date = alertLayout.findViewById(R.id.datehomework);
        final Button select_color = alertLayout.findViewById(R.id.select_color);
        final Homework homework = adapter.get(listposition);

        subject.setText(homework.getSubject());
        description.setText(homework.getDescription());
        date.setText(homework.getDate());
        select_color.setBackgroundColor(homework.getColor() != 0 ? homework.getColor() : Color.WHITE);
        select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(homework.getColor(), Color.WHITE, Color.BLACK));

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mdayofMonth = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date.setText(String.format(Locale.getDefault(), "%02d-%02d-%02d", year, month + 1, dayOfMonth));
                        homework.setDate(String.format(Locale.getDefault(), "%02d-%02d-%02d", year, month + 1, dayOfMonth));
                    }
                }, mYear, mMonth, mdayofMonth);
                datePickerDialog.setTitle(R.string.choose_date);
                datePickerDialog.show();
            }
        });

        select_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerDialog()
                        .withColor(((ColorDrawable) select_color.getBackground()).getColor()) // the default / initial color
                        .withPresets(ColorPalette.PRIMARY_COLORS)
                        .withTitle(activity.getString(R.string.choose_color))
                        .withTheme(PreferenceUtil.getGeneralTheme(activity))
                        .withCornerRadius(16)
                        .withAlphaEnabled(false)
                        .withListener(new OnColorPickedListener<ColorPickerDialog>() {
                            @Override
                            public void onColorPicked(@Nullable ColorPickerDialog dialog, int color) {
                                // a color has been picked; use it
                                select_color.setBackgroundColor(color);
                                select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(color, Color.WHITE, Color.BLACK));
                            }
                        })
                        .clearPickers()
                        .withPresets(ColorPalette.PRIMARY_COLORS)
                        .withPicker(RGBPickerView.class)
                        .show(activity.getSupportFragmentManager(), "colorPicker");
            }
        });


        subject.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE ||
                                event != null &&
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (event == null || !event.isShiftPressed()) {
                                // the user is done typing.
                                //AutoFill other fields
                                for (Week w : WeekUtils.getAllWeeks(new DbHelper(activity))) {
                                    if (w.getSubject().equalsIgnoreCase(v.getText().toString())) {
                                        select_color.setBackgroundColor(w.getColor());
                                        select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(w.getColor(), Color.WHITE, Color.BLACK));
//                                    date.setText(DBUtil.getNextOccurenceOfSubject(dbHelper, w.getSubject()));
//                                    homework.setDate(DBUtil.getNextOccurenceOfSubject(dbHelper, w.getSubject()));
                                    }
                                }

                                return true;
                            }
                        }
                        return false;
                    }
                }
        );
        subject.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    for (Week w : WeekUtils.getAllWeeks(new DbHelper(activity))) {
                        if (w.getSubject().equalsIgnoreCase(((EditText) v).getText().toString())) {
                            select_color.setBackgroundColor(w.getColor());
                            select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(w.getColor(), Color.WHITE, Color.BLACK));
//                                    date.setText(DBUtil.getNextOccurenceOfSubject(dbHelper, w.getSubject()));
//                                    homework.setDate(DBUtil.getNextOccurenceOfSubject(dbHelper, w.getSubject()));
                        }
                    }
                }
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle(R.string.edit_homework);
        alert.setCancelable(false);
        final Button cancel = alertLayout.findViewById(R.id.cancel);
        final Button save = alertLayout.findViewById(R.id.save);
        alert.setView(alertLayout);
        final AlertDialog dialog = alert.create();
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subject.getText().clear();
                description.getText().clear();
                select_color.setBackgroundColor(Color.WHITE);
                subject.requestFocus();
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(subject.getText()) || TextUtils.isEmpty(description.getText())) {
                    for (Map.Entry<Integer, EditText> editText : editTextHashs.entrySet()) {
                        if (TextUtils.isEmpty(editText.getValue().getText())) {
                            editText.getValue().setError(activity.getResources().getString(editText.getKey()) + " " + activity.getResources().getString(R.string.field_error));
                            editText.getValue().requestFocus();
                        }
                    }
                } /*else if (!date.getText().toString().matches(".*\\d+.*")) {
                Snackbar.make(alertLayout, R.string.deadline_snackbar, Snackbar.LENGTH_LONG).show();
            }*/ else {
                    HomeworkAdapter homeworkAdapter = (HomeworkAdapter) listView.getAdapter();
                    ColorDrawable buttonColor = (ColorDrawable) select_color.getBackground();
                    homework.setSubject(subject.getText().toString());
                    homework.setDescription(description.getText().toString());
                    homework.setColor(buttonColor.getColor());
                    new DbHelper(activity).updateHomework(homework);
                    homeworkAdapter.notifyDataSetChanged();
                    dialog.dismiss();

                    new MaterialDialog.Builder(activity)
                            .content(R.string.add_to_calendar)
                            .positiveText(R.string.yes)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog s, @NonNull DialogAction w) {
                                    String year = homework.getDate().substring(0, homework.getDate().indexOf("-"));
                                    String month = homework.getDate().substring(year.length() + 1, homework.getDate().indexOf("-") + year.length() - 1);
                                    String day = homework.getDate().substring(year.length() + month.length() + 2);

                                    Calendar timeCalendar = Calendar.getInstance();
                                    timeCalendar.set(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), 0, 0);

                                    Intent intent = new Intent(Intent.ACTION_INSERT)
                                            .setData(CalendarContract.Events.CONTENT_URI)
                                            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, timeCalendar.getTimeInMillis())
                                            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, timeCalendar.getTimeInMillis())
                                            .putExtra(CalendarContract.Events.TITLE, homework.getSubject())
                                            .putExtra(CalendarContract.Events.DESCRIPTION, homework.getDescription());
                                    try {
                                        activity.startActivity(intent);
                                    } catch (ActivityNotFoundException e2) {
                                        ChocoBar.builder().setActivity(activity).setText(activity.getString(R.string.no_calendar_app)).setDuration(ChocoBar.LENGTH_LONG).red().show();
                                    }

                                }
                            })
                            .negativeText(R.string.no)
                            .show();
                }
            }
        });
    }

    public static void getAddHomeworkDialog(@NonNull final AppCompatActivity activity, @NonNull final View alertLayout, @NonNull final HomeworkAdapter adapter) {
        final HashMap<Integer, EditText> editTextHashs = new HashMap<>();
        final EditText subject = alertLayout.findViewById(R.id.subjecthomework);
        editTextHashs.put(R.string.subject, subject);
        subject.requestFocus();
        final EditText description = alertLayout.findViewById(R.id.descriptionhomework);
        editTextHashs.put(R.string.description, description);
        final TextView date = alertLayout.findViewById(R.id.datehomework);
        final Button select_color = alertLayout.findViewById(R.id.select_color);
        select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(((ColorDrawable) select_color.getBackground()).getColor(), Color.WHITE, Color.BLACK));

        final Homework homework = new Homework();

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mdayofMonth = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date.setText(String.format(Locale.getDefault(), "%02d-%02d-%02d", year, month + 1, dayOfMonth));
                        homework.setDate(String.format(Locale.getDefault(), "%02d-%02d-%02d", year, month + 1, dayOfMonth));
                    }
                }, mYear, mMonth, mdayofMonth);
                datePickerDialog.setTitle(R.string.choose_date);
                datePickerDialog.show();
            }
        });

        select_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerDialog()
                        .withColor(((ColorDrawable) select_color.getBackground()).getColor()) // the default / initial color
                        .withPresets(ColorPalette.PRIMARY_COLORS)
                        .withTitle(activity.getString(R.string.choose_color))
                        .withTheme(PreferenceUtil.getGeneralTheme(activity))
                        .withCornerRadius(16)
                        .withAlphaEnabled(false)
                        .withListener(new OnColorPickedListener<ColorPickerDialog>() {
                            @Override
                            public void onColorPicked(@Nullable ColorPickerDialog dialog, int color) {
                                // a color has been picked; use it
                                select_color.setBackgroundColor(color);
                                select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(color, Color.WHITE, Color.BLACK));
                            }
                        })
                        .clearPickers()
                        .withPresets(ColorPalette.PRIMARY_COLORS)
                        .withPicker(RGBPickerView.class)
                        .show(activity.getSupportFragmentManager(), "colorPicker");
            }
        });

        subject.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE ||
                                event != null &&
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (event == null || !event.isShiftPressed()) {
                                // the user is done typing.
                                //AutoFill other fields
                                for (Week w : WeekUtils.getAllWeeks(new DbHelper(activity))) {
                                    if (w.getSubject().equalsIgnoreCase(v.getText().toString())) {
                                        select_color.setBackgroundColor(w.getColor());
                                        select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(w.getColor(), Color.WHITE, Color.BLACK));
//                                    date.setText(DBUtil.getNextOccurenceOfSubject(dbHelper, w.getSubject()));
//                                    homework.setDate(DBUtil.getNextOccurenceOfSubject(dbHelper, w.getSubject()));
                                    }
                                }

                                return true;
                            }
                        }
                        return false;
                    }
                }
        );
        subject.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    for (Week w : WeekUtils.getAllWeeks(new DbHelper(activity))) {
                        if (w.getSubject().equalsIgnoreCase(((EditText) v).getText().toString())) {
                            select_color.setBackgroundColor(w.getColor());
                            select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(w.getColor(), Color.WHITE, Color.BLACK));
//                                    date.setText(DBUtil.getNextOccurenceOfSubject(dbHelper, w.getSubject()));
//                                    homework.setDate(DBUtil.getNextOccurenceOfSubject(dbHelper, w.getSubject()));
                        }
                    }
                }
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle(R.string.add_homework);
        final Button cancel = alertLayout.findViewById(R.id.cancel);
        final Button save = alertLayout.findViewById(R.id.save);
        alert.setView(alertLayout);
        alert.setCancelable(false);
        final AlertDialog dialog = alert.create();
        FloatingActionButton fab = activity.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subject.getText().clear();
                description.getText().clear();
                select_color.setBackgroundColor(Color.WHITE);
                subject.requestFocus();
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(subject.getText()) || TextUtils.isEmpty(description.getText())) {
                    for (Map.Entry<Integer, EditText> editText : editTextHashs.entrySet()) {
                        if (TextUtils.isEmpty(editText.getValue().getText())) {
                            editText.getValue().setError(activity.getResources().getString(editText.getKey()) + " " + activity.getResources().getString(R.string.field_error));
                            editText.getValue().requestFocus();
                        }
                    }
                }/* else if (!date.getText().toString().matches(".*\\d+.*")) {
                Snackbar.make(alertLayout, R.string.deadline_snackbar, Snackbar.LENGTH_LONG).show();
            }*/ else {
                    ColorDrawable buttonColor = (ColorDrawable) select_color.getBackground();
                    homework.setSubject(subject.getText().toString());
                    homework.setDescription(description.getText().toString());
                    homework.setColor(buttonColor.getColor());

                    DbHelper dbHelper = new DbHelper(activity);
                    dbHelper.insertHomework(homework);

                    adapter.clear();
                    adapter.addAll(dbHelper.getHomework());
                    adapter.notifyDataSetChanged();

                    subject.getText().clear();
                    description.getText().clear();
                    date.setText(R.string.choose_date);
                    select_color.setBackgroundColor(Color.WHITE);
                    subject.requestFocus();
                    dialog.dismiss();

                    if (homework.getDate() != null && !homework.getDate().trim().isEmpty()) {
                        new MaterialDialog.Builder(activity)
                                .content(R.string.add_to_calendar)
                                .positiveText(R.string.yes)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog s, @NonNull DialogAction w) {
                                        String year = homework.getDate().substring(0, homework.getDate().indexOf("-"));
                                        String month = homework.getDate().substring(year.length() + 1, homework.getDate().indexOf("-") + year.length() - 1);
                                        String day = homework.getDate().substring(year.length() + month.length() + 2);

                                        Calendar timeCalendar = Calendar.getInstance();
                                        timeCalendar.set(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), 0, 0);

                                        Intent intent = new Intent(Intent.ACTION_INSERT)
                                                .setData(CalendarContract.Events.CONTENT_URI)
                                                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, timeCalendar.getTimeInMillis())
                                                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, timeCalendar.getTimeInMillis())
                                                .putExtra(CalendarContract.Events.TITLE, homework.getSubject())
                                                .putExtra(CalendarContract.Events.DESCRIPTION, homework.getDescription());
                                        try {
                                            activity.startActivity(intent);
                                        } catch (ActivityNotFoundException e2) {
                                            ChocoBar.builder().setActivity(activity).setText(activity.getString(R.string.no_calendar_app)).setDuration(ChocoBar.LENGTH_LONG).red().show();
                                        }

                                    }
                                })
                                .negativeText(R.string.no)
                                .show();
                    }
                }
            }
        });
    }

    public static void getEditTeacherDialog(@NonNull final AppCompatActivity activity, @NonNull final View alertLayout, @NonNull final ArrayList<Teacher> adapter, @NonNull final ListView listView, int listposition) {
        final HashMap<Integer, EditText> editTextHashs = new HashMap<>();
        final EditText name = alertLayout.findViewById(R.id.name_dialog);
        editTextHashs.put(R.string.name, name);
        final EditText post = alertLayout.findViewById(R.id.post_dialog);
//        editTextHashs.put(R.string.post, post);
        final EditText phone_number = alertLayout.findViewById(R.id.phonenumber_dialog);
//        editTextHashs.put(R.string.phone_number, phone_number);
        final EditText email = alertLayout.findViewById(R.id.email_dialog);
        editTextHashs.put(R.string.email, email);
        final Button select_color = alertLayout.findViewById(R.id.select_color);
        final Teacher teacher = adapter.get(listposition);

        name.setText(teacher.getName());
        post.setText(teacher.getPost());
        phone_number.setText(teacher.getPhonenumber());
        email.setText(teacher.getEmail());
        select_color.setBackgroundColor(teacher.getColor() != 0 ? teacher.getColor() : Color.WHITE);

        select_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerDialog()
                        .withColor(((ColorDrawable) select_color.getBackground()).getColor()) // the default / initial color
                        .withPresets(ColorPalette.PRIMARY_COLORS)
                        .withTitle(activity.getString(R.string.choose_color))
                        .withTheme(PreferenceUtil.getGeneralTheme(activity))
                        .withCornerRadius(16)
                        .withAlphaEnabled(false)
                        .withListener(new OnColorPickedListener<ColorPickerDialog>() {
                            @Override
                            public void onColorPicked(@Nullable ColorPickerDialog dialog, int color) {
                                // a color has been picked; use it
                                select_color.setBackgroundColor(color);
                                select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(color, Color.WHITE, Color.BLACK));
                            }
                        })
                        .clearPickers()
                        .withPresets(ColorPalette.PRIMARY_COLORS)
                        .withPicker(RGBPickerView.class)
                        .show(activity.getSupportFragmentManager(), "colorPicker");
            }
        });

        final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle(R.string.edit_teacher);
        alert.setCancelable(false);
        final Button cancel = alertLayout.findViewById(R.id.cancel);
        final Button save = alertLayout.findViewById(R.id.save);
        alert.setView(alertLayout);
        final AlertDialog dialog = alert.create();
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(name.getText()) /*|| TextUtils.isEmpty(post.getText()) || TextUtils.isEmpty(phone_number.getText())*/ || TextUtils.isEmpty(email.getText())) {
                    for (Map.Entry<Integer, EditText> entry : editTextHashs.entrySet()) {
                        if (TextUtils.isEmpty(entry.getValue().getText())) {
                            entry.getValue().setError(activity.getResources().getString(entry.getKey()) + " " + activity.getResources().getString(R.string.field_error));
                            entry.getValue().requestFocus();
                        }
                    }
                } else {
                    DbHelper dbHelper = new DbHelper(activity);
                    TeachersAdapter teachersAdapter = (TeachersAdapter) listView.getAdapter();
                    ColorDrawable buttonColor = (ColorDrawable) select_color.getBackground();
                    teacher.setName(name.getText().toString());
                    teacher.setPost(post.getText().toString());
                    teacher.setPhonenumber(phone_number.getText().toString());
                    teacher.setEmail(email.getText().toString());
                    teacher.setColor(buttonColor.getColor());
                    dbHelper.updateTeacher(teacher);
                    teachersAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });
    }

    public static void getAddTeacherDialog(@NonNull final AppCompatActivity activity, @NonNull final View alertLayout, @NonNull final TeachersAdapter adapter) {
        final HashMap<Integer, EditText> editTextHashs = new HashMap<>();
        final EditText name = alertLayout.findViewById(R.id.name_dialog);
        editTextHashs.put(R.string.name, name);
        final EditText post = alertLayout.findViewById(R.id.post_dialog);
//        editTextHashs.put(R.string.post, post);
        final EditText phone_number = alertLayout.findViewById(R.id.phonenumber_dialog);
//        editTextHashs.put(R.string.phone_number, phone_number);
        final EditText email = alertLayout.findViewById(R.id.email_dialog);
        editTextHashs.put(R.string.email, email);
        final Button select_color = alertLayout.findViewById(R.id.select_color);
        final Teacher teacher = new Teacher();

        select_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerDialog()
                        .withColor(((ColorDrawable) select_color.getBackground()).getColor()) // the default / initial color
                        .withPresets(ColorPalette.PRIMARY_COLORS)
                        .withTitle(activity.getString(R.string.choose_color))
                        .withTheme(PreferenceUtil.getGeneralTheme(activity))
                        .withCornerRadius(16)
                        .withAlphaEnabled(false)
                        .withListener(new OnColorPickedListener<ColorPickerDialog>() {
                            @Override
                            public void onColorPicked(@Nullable ColorPickerDialog dialog, int color) {
                                // a color has been picked; use it
                                select_color.setBackgroundColor(color);
                                select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(color, Color.WHITE, Color.BLACK));
                            }
                        })
                        .clearPickers()
                        .withPresets(ColorPalette.PRIMARY_COLORS)
                        .withPicker(RGBPickerView.class)
                        .show(activity.getSupportFragmentManager(), "colorPicker");
            }
        });

        final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle(activity.getResources().getString(R.string.add_teacher));
        alert.setCancelable(false);
        final Button cancel = alertLayout.findViewById(R.id.cancel);
        final Button save = alertLayout.findViewById(R.id.save);
        alert.setView(alertLayout);
        final AlertDialog dialog = alert.create();
        FloatingActionButton fab = activity.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(name.getText()) /*|| TextUtils.isEmpty(post.getText()) || TextUtils.isEmpty(phone_number.getText())*/ || TextUtils.isEmpty(email.getText())) {
                    for (Map.Entry<Integer, EditText> entry : editTextHashs.entrySet()) {
                        if (TextUtils.isEmpty(entry.getValue().getText())) {
                            entry.getValue().setError(activity.getResources().getString(entry.getKey()) + " " + activity.getResources().getString(R.string.field_error));
                            entry.getValue().requestFocus();
                        }
                    }
                } else {
                    DbHelper dbHelper = new DbHelper(activity);
                    ColorDrawable buttonColor = (ColorDrawable) select_color.getBackground();
                    teacher.setName(name.getText().toString());
                    teacher.setPost(post.getText().toString());
                    teacher.setPhonenumber(phone_number.getText().toString());
                    teacher.setEmail(email.getText().toString());
                    teacher.setColor(buttonColor.getColor());
                    dbHelper.insertTeacher(teacher);

                    adapter.clear();
                    adapter.addAll(dbHelper.getTeacher());
                    adapter.notifyDataSetChanged();

                    name.getText().clear();
                    post.getText().clear();
                    phone_number.getText().clear();
                    email.getText().clear();
                    select_color.setBackgroundColor(Color.WHITE);
                    name.requestFocus();
                    dialog.dismiss();
                }
            }
        });
    }

    public static void getEditNoteDialog(@NonNull final AppCompatActivity activity, @NonNull final View alertLayout, @NonNull final ArrayList<Note> adapter, @NonNull final ListView listView, int listposition) {
        final EditText title = alertLayout.findViewById(R.id.titlenote);
        final Button select_color = alertLayout.findViewById(R.id.select_color);
        final Note note = adapter.get(listposition);
        title.setText(note.getTitle());
        select_color.setBackgroundColor(note.getColor() != 0 ? note.getColor() : Color.WHITE);
        select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(note.getColor(), Color.WHITE, Color.BLACK));

        select_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerDialog()
                        .withColor(((ColorDrawable) select_color.getBackground()).getColor()) // the default / initial color
                        .withPresets(ColorPalette.PRIMARY_COLORS)
                        .withTitle(activity.getString(R.string.choose_color))
                        .withTheme(PreferenceUtil.getGeneralTheme(activity))
                        .withCornerRadius(16)
                        .withAlphaEnabled(false)
                        .withListener(new OnColorPickedListener<ColorPickerDialog>() {
                            @Override
                            public void onColorPicked(@Nullable ColorPickerDialog dialog, int color) {
                                // a color has been picked; use it
                                select_color.setBackgroundColor(color);
                                select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(color, Color.WHITE, Color.BLACK));
                            }
                        })
                        .clearPickers()
                        .withPresets(ColorPalette.PRIMARY_COLORS)
                        .withPicker(RGBPickerView.class)
                        .show(activity.getSupportFragmentManager(), "colorPicker");
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle(R.string.edit_note);
        final Button cancel = alertLayout.findViewById(R.id.cancel);
        final Button save = alertLayout.findViewById(R.id.save);
        alert.setView(alertLayout);
        alert.setCancelable(false);
        final AlertDialog dialog = alert.create();
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.getText().clear();
                select_color.setBackgroundColor(Color.WHITE);
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(title.getText())) {
                    title.setError(activity.getResources().getString(R.string.title_error));
                    title.requestFocus();
                } else {
                    DbHelper dbHelper = new DbHelper(activity);
                    ColorDrawable buttonColor = (ColorDrawable) select_color.getBackground();
                    note.setTitle(title.getText().toString());
                    note.setColor(buttonColor.getColor());
                    dbHelper.updateNote(note);
                    NotesAdapter notesAdapter = (NotesAdapter) listView.getAdapter();
                    notesAdapter.notifyDataSetChanged();

                    dialog.dismiss();
                }
            }
        });
    }

    public static void getAddNoteDialog(@NonNull final AppCompatActivity activity, @NonNull final View alertLayout, @NonNull final NotesAdapter adapter) {
        final EditText title = alertLayout.findViewById(R.id.titlenote);
        title.requestFocus();
        final Button select_color = alertLayout.findViewById(R.id.select_color);
        select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(((ColorDrawable) select_color.getBackground()).getColor(), Color.WHITE, Color.BLACK));
        final Note note = new Note();

        select_color.setOnClickListener(v -> new ColorPickerDialog()
                .withColor(((ColorDrawable) select_color.getBackground()).getColor()) // the default / initial color
                .withPresets(ColorPalette.PRIMARY_COLORS)
                .withTitle(activity.getString(R.string.choose_color))
                .withTheme(PreferenceUtil.getGeneralTheme(activity))
                .withCornerRadius(16)
                .withAlphaEnabled(false)
                .withListener((dialog, color) -> {
                    // a color has been picked; use it
                    select_color.setBackgroundColor(color);
                    select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(color, Color.WHITE, Color.BLACK));
                })
                .clearPickers()
                .withPresets(ColorPalette.PRIMARY_COLORS)
                .withPicker(RGBPickerView.class)
                .show(activity.getSupportFragmentManager(), "colorPicker"));

        final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle(R.string.add_note);
        final Button cancel = alertLayout.findViewById(R.id.cancel);
        final Button save = alertLayout.findViewById(R.id.save);
        alert.setView(alertLayout);
        alert.setCancelable(false);
        final AlertDialog dialog = alert.create();
        FloatingActionButton fab = activity.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        cancel.setOnClickListener(v -> {
            title.getText().clear();
            select_color.setBackgroundColor(Color.WHITE);
            dialog.dismiss();
        });

        save.setOnClickListener(v -> {
            if (TextUtils.isEmpty(title.getText())) {
                title.setError(activity.getResources().getString(R.string.title_error));
                title.requestFocus();
            } else {
                DbHelper dbHelper = new DbHelper(activity);
                ColorDrawable buttonColor = (ColorDrawable) select_color.getBackground();
                note.setTitle(title.getText().toString());
                note.setColor(buttonColor.getColor());
                dbHelper.insertNote(note);

                adapter.clear();
                adapter.addAll(dbHelper.getNote());
                adapter.notifyDataSetChanged();

                title.getText().clear();
                select_color.setBackgroundColor(Color.WHITE);
                dialog.dismiss();
            }
        });
    }

    public static void getEditExamDialog(@NonNull final AppCompatActivity activity, @NonNull final View alertLayout, @NonNull final ArrayList<Exam> adapter, @NonNull final ListView listView, int listposition) {
        final HashMap<Integer, EditText> editTextHashs = new HashMap<>();
        final EditText subject = alertLayout.findViewById(R.id.subjectexam_dialog);
        editTextHashs.put(R.string.subject, subject);
        final EditText teacher = alertLayout.findViewById(R.id.teacherexam_dialog);
//        editTextHashs.put(R.string.teacher, teacher);
        final EditText room = alertLayout.findViewById(R.id.roomexam_dialog);
//        editTextHashs.put(R.string.room, room);
        final TextView date = alertLayout.findViewById(R.id.dateexam_dialog);
        final TextView time = alertLayout.findViewById(R.id.timeexam_dialog);
        final TextView hour = alertLayout.findViewById(R.id.hourexam_dialog);
        final Button select_color = alertLayout.findViewById(R.id.select_color);

        final Exam exam = adapter.get(listposition);

        subject.setText(exam.getSubject());
        teacher.setText(exam.getTeacher());
        room.setText(exam.getRoom());
        date.setText(exam.getDate());
        time.setText(exam.getTime());
        if (exam.getTime() != null && !exam.getTime().trim().isEmpty()) {
            hour.setText("" + WeekUtils.getMatchingScheduleBegin(exam.getTime(), activity));
            time.setText(exam.getTime());
        } else {
            hour.setText("0");
            time.setText("0:0");
        }
        select_color.setBackgroundColor(exam.getColor());
        select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(exam.getColor(), Color.WHITE, Color.BLACK));

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mdayofMonth = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date.setText(String.format(Locale.getDefault(), "%02d-%02d-%02d", year, month + 1, dayOfMonth));
                        exam.setDate(String.format(Locale.getDefault(), "%02d-%02d-%02d", year, month + 1, dayOfMonth));
                    }
                }, mYear, mMonth, mdayofMonth);
                datePickerDialog.setTitle(R.string.choose_date);
                datePickerDialog.show();
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mHour, mMinute;
                try {
                    mHour = Integer.parseInt(exam.getTime().substring(0, exam.getTime().indexOf(":")));
                    mMinute = Integer.parseInt(exam.getTime().substring(exam.getTime().indexOf(":") + 1));
                } catch (Exception ignore) {
                    mHour = 0;
                    mMinute = 0;
                }
                TimePickerDialog timePickerDialog = new TimePickerDialog(activity,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                time.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
                                exam.setTime(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
                                hour.setText("" + WeekUtils.getMatchingScheduleBegin(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute), activity));
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.setTitle(R.string.choose_time);
                timePickerDialog.show();
            }
        });

        hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NumberPicker numberPicker = new NumberPicker(activity);
                numberPicker.setMaxValue(15);
                numberPicker.setMinValue(1);
                try {
                    numberPicker.setValue(Integer.parseInt(hour.getText().toString()));
                } catch (Exception e) {
                    numberPicker.setValue(1);
                }
                new MaterialDialog.Builder(activity)
                        .customView(numberPicker, false)
                        .positiveText(R.string.select)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog vi, @NonNull DialogAction w) {
                                int value = numberPicker.getValue();
                                time.setText(WeekUtils.getMatchingTimeBegin(value, activity));
                                exam.setTime(WeekUtils.getMatchingTimeBegin(value, activity));
                                hour.setText("" + value);
                            }
                        })
                        .show();
            }
        });

        select_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerDialog()
                        .withColor(((ColorDrawable) select_color.getBackground()).getColor()) // the default / initial color
                        .withPresets(ColorPalette.PRIMARY_COLORS)
                        .withTitle(activity.getString(R.string.choose_color))
                        .withTheme(PreferenceUtil.getGeneralTheme(activity))
                        .withCornerRadius(16)
                        .withAlphaEnabled(false)
                        .withListener(new OnColorPickedListener<ColorPickerDialog>() {
                            @Override
                            public void onColorPicked(@Nullable ColorPickerDialog dialog, int color) {
                                // a color has been picked; use it
                                select_color.setBackgroundColor(color);
                                select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(color, Color.WHITE, Color.BLACK));
                            }
                        })
                        .clearPickers()
                        .withPresets(ColorPalette.PRIMARY_COLORS)
                        .withPicker(RGBPickerView.class)
                        .show(activity.getSupportFragmentManager(), "colorPicker");
            }
        });


        subject.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE ||
                                event != null &&
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (event == null || !event.isShiftPressed()) {
                                // the user is done typing.
                                //AutoFill other fields
                                for (Week w : WeekUtils.getAllWeeks(new DbHelper(activity))) {
                                    if (w.getSubject().equalsIgnoreCase(v.getText().toString())) {
                                        if (teacher.getText().toString().trim().isEmpty())
                                            teacher.setText(w.getTeacher());
                                        if (room.getText().toString().trim().isEmpty())
                                            room.setText(w.getRoom());
                                        select_color.setBackgroundColor(w.getColor());
                                        select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(w.getColor(), Color.WHITE, Color.BLACK));
                                    }
                                }

                                return true;
                            }
                        }
                        return false;
                    }
                }
        );
        subject.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    for (Week w : WeekUtils.getAllWeeks(new DbHelper(activity))) {
                        if (w.getSubject().equalsIgnoreCase(((EditText) v).getText().toString())) {
                            if (teacher.getText().toString().trim().isEmpty())
                                teacher.setText(w.getTeacher());
                            if (room.getText().toString().trim().isEmpty())
                                room.setText(w.getRoom());
                            select_color.setBackgroundColor(w.getColor());
                            select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(w.getColor(), Color.WHITE, Color.BLACK));
                        }
                    }
                }
            }
        });

        final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle(activity.getResources().getString(R.string.add_exam));
        alert.setCancelable(false);
        final Button cancel = alertLayout.findViewById(R.id.cancel);
        final Button save = alertLayout.findViewById(R.id.save);
        alert.setView(alertLayout);
        final AlertDialog dialog = alert.create();
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subject.getText().clear();
                teacher.getText().clear();
                room.getText().clear();
                select_color.setBackgroundColor(Color.WHITE);
                subject.requestFocus();
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(subject.getText())/* || TextUtils.isEmpty(teacher.getText()) || TextUtils.isEmpty(room.getText())*/) {
                    for (Map.Entry<Integer, EditText> entry : editTextHashs.entrySet()) {
                        if (TextUtils.isEmpty(entry.getValue().getText())) {
                            entry.getValue().setError(activity.getResources().getString(entry.getKey()) + " " + activity.getResources().getString(R.string.field_error));
                            entry.getValue().requestFocus();
                        }
                    }
                } else if (!date.getText().toString().matches(".*\\d+.*")) {
                    Snackbar.make(alertLayout, R.string.date_error, Snackbar.LENGTH_LONG).show();
                } /*else if (!time.getText().toString().matches(".*\\d+.*")) {
                Snackbar.make(alertLayout, R.string.time_error, Snackbar.LENGTH_LONG).show();
            }*/ else {
                    ColorDrawable buttonColor = (ColorDrawable) select_color.getBackground();
                    exam.setSubject(subject.getText().toString());
                    exam.setTeacher(teacher.getText().toString());
                    exam.setRoom(room.getText().toString());
                    exam.setColor(buttonColor.getColor());

                    new DbHelper(activity).updateExam(exam);

                    ExamsAdapter examsAdapter = (ExamsAdapter) listView.getAdapter();
                    examsAdapter.notifyDataSetChanged();

                    dialog.dismiss();

                    new MaterialDialog.Builder(activity)
                            .content(R.string.add_to_calendar)
                            .positiveText(R.string.yes)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog s, @NonNull DialogAction w) {
                                    String year = exam.getDate().substring(0, exam.getDate().indexOf("-"));
                                    String month = exam.getDate().substring(year.length() + 1, exam.getDate().indexOf("-") + year.length() - 1);
                                    String day = exam.getDate().substring(year.length() + month.length() + 2);

                                    String hour2, minute;
                                    if (exam.getTime() != null && !exam.getTime().trim().isEmpty()) {
                                        hour2 = exam.getTime().substring(0, exam.getTime().indexOf(":"));
                                        minute = exam.getTime().substring(hour2.length() + 1);
                                    } else {
                                        hour2 = "0";
                                        minute = "0";
                                    }

                                    Calendar timeCalendar = Calendar.getInstance();
                                    timeCalendar.set(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), Integer.parseInt(hour2), Integer.parseInt(minute));

                                    Intent intent = new Intent(Intent.ACTION_INSERT)
                                            .setData(CalendarContract.Events.CONTENT_URI)
                                            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, timeCalendar.getTimeInMillis())
                                            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, timeCalendar.getTimeInMillis())
                                            .putExtra(CalendarContract.Events.TITLE, exam.getSubject())
                                            .putExtra(CalendarContract.Events.DESCRIPTION, exam.getTeacher())
                                            .putExtra(CalendarContract.Events.EVENT_LOCATION, exam.getRoom());
                                    try {
                                        activity.startActivity(intent);
                                    } catch (ActivityNotFoundException e2) {
                                        ChocoBar.builder().setActivity(activity).setText(activity.getString(R.string.no_calendar_app)).setDuration(ChocoBar.LENGTH_LONG).red().show();
                                    }

                                }
                            })
                            .negativeText(R.string.no)
                            .show();
                }
            }
        });
    }

    public static void getAddExamDialog(@NonNull final AppCompatActivity activity, @NonNull final View alertLayout, @NonNull final ExamsAdapter adapter) {
        final HashMap<Integer, EditText> editTextHashs = new HashMap<>();
        final EditText subject = alertLayout.findViewById(R.id.subjectexam_dialog);
        editTextHashs.put(R.string.subject, subject);
        subject.requestFocus();
        final EditText teacher = alertLayout.findViewById(R.id.teacherexam_dialog);
//        editTextHashs.put(R.string.teacher, teacher);
        final EditText room = alertLayout.findViewById(R.id.roomexam_dialog);
//        editTextHashs.put(R.string.room, room);
        final TextView date = alertLayout.findViewById(R.id.dateexam_dialog);
        final TextView time = alertLayout.findViewById(R.id.timeexam_dialog);
        final TextView hour = alertLayout.findViewById(R.id.hourexam_dialog);
        final Button select_color = alertLayout.findViewById(R.id.select_color);
        select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(((ColorDrawable) select_color.getBackground()).getColor(), Color.WHITE, Color.BLACK));

        hour.setText(R.string.select_time);
        if (PreferenceUtil.showTimes(activity)) {
            hour.setVisibility(View.GONE);
            time.setVisibility(View.VISIBLE);
            time.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, .7f));
        } else {
            hour.setVisibility(View.VISIBLE);
            time.setVisibility(View.GONE);
            hour.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, .7f));
        }

        final Exam exam = new Exam();

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mdayofMonth = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date.setText(String.format(Locale.getDefault(), "%02d-%02d-%02d", year, month + 1, dayOfMonth));
                        exam.setDate(String.format(Locale.getDefault(), "%02d-%02d-%02d", year, month + 1, dayOfMonth));
                    }
                }, mYear, mMonth, mdayofMonth);
                datePickerDialog.setTitle(R.string.choose_date);
                datePickerDialog.show();
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(activity,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                time.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
                                exam.setTime(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
                                hour.setText("" + WeekUtils.getMatchingScheduleBegin(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute), activity));
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.setTitle(R.string.choose_time);
                timePickerDialog.show();
            }
        });

        hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NumberPicker numberPicker = new NumberPicker(activity);
                numberPicker.setMaxValue(15);
                numberPicker.setMinValue(1);
                new MaterialDialog.Builder(activity)
                        .customView(numberPicker, false)
                        .positiveText(R.string.select)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog vi, @NonNull DialogAction w) {
                                int value = numberPicker.getValue();
                                time.setText(WeekUtils.getMatchingTimeBegin(value, activity));
                                exam.setTime(WeekUtils.getMatchingTimeBegin(value, activity));
                                hour.setText("" + value);
                            }
                        })
                        .show();
            }
        });

        select_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerDialog()
                        .withColor(((ColorDrawable) select_color.getBackground()).getColor()) // the default / initial color
                        .withPresets(ColorPalette.PRIMARY_COLORS)
                        .withTitle(activity.getString(R.string.choose_color))
                        .withTheme(PreferenceUtil.getGeneralTheme(activity))
                        .withCornerRadius(16)
                        .withAlphaEnabled(false)
                        .withListener(new OnColorPickedListener<ColorPickerDialog>() {
                            @Override
                            public void onColorPicked(@Nullable ColorPickerDialog dialog, int color) {
                                // a color has been picked; use it
                                select_color.setBackgroundColor(color);
                                select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(color, Color.WHITE, Color.BLACK));
                            }
                        })
                        .clearPickers()
                        .withPresets(ColorPalette.PRIMARY_COLORS)
                        .withPicker(RGBPickerView.class)
                        .show(activity.getSupportFragmentManager(), "colorPicker");
            }
        });


        subject.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE ||
                                event != null &&
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (event == null || !event.isShiftPressed()) {
                                // the user is done typing.
                                //AutoFill other fields
                                for (Week w : WeekUtils.getAllWeeks(new DbHelper(activity))) {
                                    if (w.getSubject().equalsIgnoreCase(v.getText().toString())) {
                                        if (teacher.getText().toString().trim().isEmpty())
                                            teacher.setText(w.getTeacher());
                                        if (room.getText().toString().trim().isEmpty())
                                            room.setText(w.getRoom());
                                        select_color.setBackgroundColor(w.getColor());
                                        select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(w.getColor(), Color.WHITE, Color.BLACK));
                                    }
                                }

                                return true;
                            }
                        }
                        return false;
                    }
                }
        );
        subject.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    for (Week w : WeekUtils.getAllWeeks(new DbHelper(activity))) {
                        if (w.getSubject().equalsIgnoreCase(((EditText) v).getText().toString())) {
                            if (teacher.getText().toString().trim().isEmpty())
                                teacher.setText(w.getTeacher());
                            if (room.getText().toString().trim().isEmpty())
                                room.setText(w.getRoom());
                            select_color.setBackgroundColor(w.getColor());
                            select_color.setTextColor(ColorPalette.pickTextColorBasedOnBgColorSimple(w.getColor(), Color.WHITE, Color.BLACK));
                        }
                    }
                }
            }
        });

        final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle(activity.getResources().getString(R.string.add_exam));
        alert.setCancelable(false);
        final Button cancel = alertLayout.findViewById(R.id.cancel);
        final Button save = alertLayout.findViewById(R.id.save);
        alert.setView(alertLayout);
        final AlertDialog dialog = alert.create();
        FloatingActionButton fab = activity.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subject.getText().clear();
                teacher.getText().clear();
                room.getText().clear();
                select_color.setBackgroundColor(Color.WHITE);
                subject.requestFocus();
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(subject.getText()) /*|| TextUtils.isEmpty(teacher.getText()) || TextUtils.isEmpty(room.getText())*/) {
                    for (Map.Entry<Integer, EditText> entry : editTextHashs.entrySet()) {
                        if (TextUtils.isEmpty(entry.getValue().getText())) {
                            entry.getValue().setError(activity.getResources().getString(entry.getKey()) + " " + activity.getResources().getString(R.string.field_error));
                            entry.getValue().requestFocus();
                        }
                    }
                } else if (!date.getText().toString().matches(".*\\d+.*")) {
                    Snackbar.make(alertLayout, R.string.date_error, Snackbar.LENGTH_LONG).show();
                } /*else if (!time.getText().toString().matches(".*\\d+.*")) {
                Snackbar.make(alertLayout, R.string.time_error, Snackbar.LENGTH_LONG).show();
            }*/ else {
                    ColorDrawable buttonColor = (ColorDrawable) select_color.getBackground();
                    exam.setSubject(subject.getText().toString());
                    exam.setTeacher(teacher.getText().toString());
                    exam.setRoom(room.getText().toString());
                    exam.setColor(buttonColor.getColor());

                    DbHelper dbHelper = new DbHelper(activity);
                    dbHelper.insertExam(exam);

                    adapter.clear();
                    adapter.addAll(dbHelper.getExam());
                    adapter.notifyDataSetChanged();

                    subject.getText().clear();
                    teacher.getText().clear();
                    room.getText().clear();
                    date.setText(R.string.choose_date);
                    time.setText(R.string.select_time);
                    hour.setText(R.string.select_time);
                    select_color.setBackgroundColor(Color.WHITE);
                    subject.requestFocus();
                    dialog.dismiss();

                    new MaterialDialog.Builder(activity)
                            .content(R.string.add_to_calendar)
                            .positiveText(R.string.yes)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog s, @NonNull DialogAction w) {
                                    String year = exam.getDate().substring(0, exam.getDate().indexOf("-"));
                                    String month = exam.getDate().substring(year.length() + 1, exam.getDate().indexOf("-") + year.length() - 1);
                                    String day = exam.getDate().substring(year.length() + month.length() + 2);

                                    String hour2, minute;
                                    if (exam.getTime() != null && !exam.getTime().trim().isEmpty()) {
                                        hour2 = exam.getTime().substring(0, exam.getTime().indexOf(":"));
                                        minute = exam.getTime().substring(hour2.length() + 1);
                                    } else {
                                        hour2 = "0";
                                        minute = "0";
                                    }

                                    Calendar timeCalendar = Calendar.getInstance();
                                    timeCalendar.set(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), Integer.parseInt(hour2), Integer.parseInt(minute));

                                    Intent intent = new Intent(Intent.ACTION_INSERT)
                                            .setData(CalendarContract.Events.CONTENT_URI)
                                            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, timeCalendar.getTimeInMillis())
                                            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, timeCalendar.getTimeInMillis())
                                            .putExtra(CalendarContract.Events.TITLE, exam.getSubject())
                                            .putExtra(CalendarContract.Events.DESCRIPTION, exam.getTeacher())
                                            .putExtra(CalendarContract.Events.EVENT_LOCATION, exam.getRoom());
                                    try {
                                        activity.startActivity(intent);
                                    } catch (ActivityNotFoundException e2) {
                                        ChocoBar.builder().setActivity(activity).setText(activity.getString(R.string.no_calendar_app)).setDuration(ChocoBar.LENGTH_LONG).red().show();
                                    }

                                }
                            })
                            .negativeText(R.string.no)
                            .show();
                }
            }
        });
    }

    public static void getDeleteDialog(@NonNull Context context, @NonNull final Runnable runnable, String deleteSubject) {
        new MaterialDialog.Builder(context)
                .title(context.getString(R.string.are_you_sure))
                .content(context.getString(R.string.delete_content, deleteSubject))
                .positiveText(context.getString(R.string.yes))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        runnable.run();
                        dialog.dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .negativeText(context.getString(R.string.no))
                .show();
    }

    private static void databaseChanged(Context context) {
        NotificationUtil.sendNotificationCurrentLesson(context, false);
        DoNotDisturbReceiversKt.setDoNotDisturbReceivers(context, false);
    }
}

