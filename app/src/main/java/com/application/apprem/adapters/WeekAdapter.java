package com.application.apprem.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.application.apprem.R;
import com.application.apprem.activities.TeachersActivity;
import com.application.apprem.models.Week;
import com.application.apprem.receivers.DoNotDisturbReceiversKt;
import com.application.apprem.utils.AlertDialogsHelper;
import com.application.apprem.utils.ColorPalette;
import com.application.apprem.utils.DbHelper;
import com.application.apprem.utils.PreferenceUtil;
import com.application.apprem.utils.WeekUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;


import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;


public class WeekAdapter extends ArrayAdapter<Week> {

    @NonNull
    private final AppCompatActivity mActivity;
    @NonNull
    private final ArrayList<Week> weeklist;
    private Week week;
    private final ListView mListView;

    private static class ViewHolder {
        TextView subject;
        TextView teacher;
        TextView time;
        TextView room;
        ImageView popup;
        View cardView;
    }

    public WeekAdapter(@NonNull AppCompatActivity activity, ListView listView, int resource, @NonNull ArrayList<Week> objects) {
        super(activity, resource, objects);
        mActivity = activity;
        weeklist = objects;
        mListView = listView;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String subject = Objects.requireNonNull(getItem(position)).getSubject();
        String teacher = Objects.requireNonNull(getItem(position)).getTeacher();
        String time_from = Objects.requireNonNull(getItem(position)).getFromTime();
        String time_to = Objects.requireNonNull(getItem(position)).getToTime();
        String room = Objects.requireNonNull(getItem(position)).getRoom();
        int color = Objects.requireNonNull(getItem(position)).getColor();

        week = new Week(subject, teacher, room, time_from, time_to, color);
        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            convertView = inflater.inflate(R.layout.timetable_child, parent, false);
            holder = new ViewHolder();
            holder.subject = convertView.findViewById(R.id.subject);
            holder.teacher = convertView.findViewById(R.id.teacher);
            holder.time = convertView.findViewById(R.id.time);
            holder.room = convertView.findViewById(R.id.room);
            holder.popup = convertView.findViewById(R.id.popupbtn);
            holder.cardView = convertView.findViewById(R.id.week_cardview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //Setup colors based on Background
        int textColor = ColorPalette.pickTextColorBasedOnBgColorSimple(color, Color.WHITE, Color.BLACK);
      //  holder.subject.setTextColor(textColor);
      //  holder.teacher.setTextColor(textColor);
      //  holder.time.setTextColor(textColor);
      //  holder.room.setTextColor(textColor);
      //  ImageViewCompat.setImageTintList((ImageView) convertView.findViewById(R.id.roomimage), ColorStateList.valueOf(textColor));
      //  ImageViewCompat.setImageTintList((ImageView) convertView.findViewById(R.id.teacherimage), ColorStateList.valueOf(textColor));
      //  ImageViewCompat.setImageTintList((ImageView) convertView.findViewById(R.id.timeimage), ColorStateList.valueOf(textColor));
      //  ImageViewCompat.setImageTintList((ImageView) convertView.findViewById(R.id.popupbtn), ColorStateList.valueOf(textColor));
      //  convertView.findViewById(R.id.line).setBackgroundColor(textColor);


        holder.subject.setText(week.getSubject());
        holder.teacher.setText(week.getTeacher());
        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        holder.teacher.setBackgroundResource(outValue.resourceId);
        holder.teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.startActivity(new Intent(mActivity, TeachersActivity.class));
            }
        });

        holder.room.setText(week.getRoom());
        holder.room.setOnClickListener(null);

        if (PreferenceUtil.showTimes(getContext()))
            holder.time.setText(week.getFromTime() + " - " + week.getToTime());
        else {
            int start = WeekUtils.getMatchingScheduleBegin(week.getFromTime(), getContext());
            int end = WeekUtils.getMatchingScheduleEnd(week.getToTime(), getContext());
            if (start == end) {
                holder.time.setText(start + ". " + getContext().getString(R.string.lesson));
            } else {
                holder.time.setText(start + ".-" + end + ". " + getContext().getString(R.string.lesson));
            }
        }

        holder.cardView.setBackgroundColor(week.getColor());
        holder.popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mActivity);
                bottomSheetDialog.setContentView(R.layout.bottom_sheet);
                bottomSheetDialog.show();




                TextView unit_name = bottomSheetDialog.findViewById(R.id.subject);
                unit_name.setText(week.getSubject());
                TextView location = bottomSheetDialog.findViewById(R.id.room);
                location.setText(week.getRoom());
                TextView lecturer = bottomSheetDialog.findViewById(R.id.teacher);
                lecturer.setText(week.getTeacher());
                TextView from_time = bottomSheetDialog.findViewById(R.id.from_time);
                from_time.setText(week.getFromTime());
                TextView to_time = bottomSheetDialog.findViewById(R.id.to_time);
                to_time.setText(week.getToTime());


                lecturer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mActivity.startActivity(new Intent(mActivity, TeachersActivity.class));
                    }
                });

                final DbHelper db = new DbHelper(mActivity);

                Button edit = bottomSheetDialog.findViewById(R.id.button_edit);
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final View alertLayout = mActivity.getLayoutInflater().inflate(R.layout.dialog_add_subject, null);
                        AlertDialogsHelper.getEditSubjectDialog(mActivity, alertLayout, new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                            }
                        }, weeklist.get(position));
                        notifyDataSetChanged();

                        bottomSheetDialog.cancel();

                    }
                });

                Button delete = bottomSheetDialog.findViewById(R.id.button_delete);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialogsHelper.getDeleteDialog(getContext(), new Runnable() {
                            @Override
                            public void run() {
                                db.deleteWeekById(Objects.requireNonNull(getItem(position)));
                                db.updateWeek(Objects.requireNonNull(getItem(position)));
                                weeklist.remove(position);
                                notifyDataSetChanged();
                                DoNotDisturbReceiversKt.setDoNotDisturbReceivers(mActivity, false);


                            }
                        }, getContext().getString(R.string.delete_week, week.getSubject()));

                        bottomSheetDialog.cancel();
                    }
                });


                ContextThemeWrapper theme = new ContextThemeWrapper(mActivity, PreferenceUtil.isDark(WeekAdapter.this.getContext()) ? R.style.Widget_AppCompat_PopupMenu : R.style.Widget_AppCompat_Light_PopupMenu);
                final PopupMenu popup = new PopupMenu(theme, holder.popup);
            //    final DbHelper db = new DbHelper(mActivity);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(@NonNull MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.delete_popup) {
                            AlertDialogsHelper.getDeleteDialog(getContext(), new Runnable() {
                                @Override
                                public void run() {
                                    db.deleteWeekById(Objects.requireNonNull(getItem(position)));
                                    db.updateWeek(Objects.requireNonNull(getItem(position)));
                                    weeklist.remove(position);
                                    notifyDataSetChanged();
                                    DoNotDisturbReceiversKt.setDoNotDisturbReceivers(mActivity, false);
                                }
                            }, getContext().getString(R.string.delete_week, week.getSubject()));
                            return true;
                        } else if (itemId == R.id.edit_popup) {
                            final View alertLayout = mActivity.getLayoutInflater().inflate(R.layout.dialog_add_subject, null);
                            AlertDialogsHelper.getEditSubjectDialog(mActivity, alertLayout, new Runnable() {
                                @Override
                                public void run() {
                                    notifyDataSetChanged();
                                }
                            }, weeklist.get(position));
                            notifyDataSetChanged();
                            return true;
                        }
                        return onMenuItemClick(item);
                    }
                });
              //  popup.show();
            }
        });

        hidePopUpMenu(holder);


        return convertView;
    }

    @NonNull
    public ArrayList<Week> getWeekList() {
        return weeklist;
    }

    public Week getWeek() {
        return week;
    }

    private void hidePopUpMenu(@NonNull ViewHolder holder) {
        SparseBooleanArray checkedItems = mListView.getCheckedItemPositions();
        if (checkedItems.size() > 0) {
            for (int i = 0; i < checkedItems.size(); i++) {
                int key = checkedItems.keyAt(i);
                if (checkedItems.get(key)) {
                    holder.popup.setVisibility(View.INVISIBLE);
                }
            }
        } else {
            holder.popup.setVisibility(View.VISIBLE);
        }
    }

}
