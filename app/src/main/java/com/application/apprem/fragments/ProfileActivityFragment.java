package com.application.apprem.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.application.apprem.R;
import com.application.apprem.profiles.Profile;
import com.application.apprem.profiles.ProfileManagement;
import com.application.apprem.utils.DbHelper;


import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileActivityFragment extends Fragment {
    private ProfileListAdapter adapter;
    private int preferredProfilePos = ProfileManagement.getPreferredProfilePosition();

    public ProfileActivityFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        adapter = new ProfileListAdapter(requireContext(), 0);
        ((ListView) root.findViewById(R.id.profile_list)).setAdapter(adapter);
        ((ListView) root.findViewById(R.id.profile_list)).setDividerHeight(0);

        requireActivity().findViewById(R.id.fab).setOnClickListener((View v) -> openAddDialog());
        return root;
    }

    private class ProfileListAdapter extends ArrayAdapter<String[]> {

        ProfileListAdapter(@NonNull Context con, int resource) {
            super(con, resource);
        }

        @NotNull
        @Override
        public View getView(int position, @Nullable View convertView, @NotNull ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.profile_child, null);
            }

            return generateView(convertView, position);
        }

        @Override
        public int getCount() {
            return ProfileManagement.getSize();
        }

        @NonNull
        private View generateView(@NonNull View base, int position) {
            Profile p = ProfileManagement.getProfile(position);
            TextView name = base.findViewById(R.id.profilelist_name);
            name.setText(p.getName());

            ImageButton edit = base.findViewById(R.id.profilelist_edit);
            edit.setOnClickListener((View v) -> openEditDialog(position));

            ImageButton delete = base.findViewById(R.id.profilelist_delete);
            delete.setOnClickListener((View v) -> openDeleteDialog(position));

            ImageButton star = base.findViewById(R.id.profilelist_preferred);
            if (position == preferredProfilePos) {
                star.setImageResource(R.drawable.ic_star_filled);
            } else {
                star.setImageResource(R.drawable.ic_star_outlined);
            }
            star.setOnClickListener((View v) -> setPreferredProfile(position));

            return base;
        }
    }

    private void openAddDialog() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(requireActivity());
        builder.title(getString(R.string.profiles_add));

        // Set up the input
        final EditText input = new EditText(requireContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint(getString(R.string.name));
        builder.customView(input, true);

        // Set up the buttons
        builder.onPositive((dialog, which) -> {
            //Add Profile
            String inputText = input.getText().toString();
            String name;
            if (inputText.trim().isEmpty())
                name = requireContext().getString(R.string.profile_empty_name) + (ProfileManagement.getSize() + 1);
            else
                name = inputText;
            ProfileManagement.addProfile(new Profile(name));
        });

        builder.onNegative((dialog, which) -> dialog.dismiss());

        builder.positiveText(R.string.add);
        builder.negativeText(R.string.cancel);
        builder.build().show();
    }

    private void openEditDialog(int position) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(requireContext());
        builder.title(getString(R.string.profiles_edit));

        // Set up the input
        LinearLayout base = new LinearLayout(requireContext());
        base.setOrientation(LinearLayout.VERTICAL);

        EditText name = new EditText(requireContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        name.setInputType(InputType.TYPE_CLASS_TEXT);
        name.setText(ProfileManagement.getProfile(position).getName());
        name.setHint(R.string.name);
        base.addView(name);

        builder.customView(base, true);

        // Set up the buttons
        builder.positiveText(getString(R.string.ok));
        builder.negativeText(getString(R.string.cancel));
        builder.onPositive((dialog, which) -> {
            Profile profile = ProfileManagement.getProfile(position);
            String nameText = name.getText().toString();
            //Do not enter empty text
            ProfileManagement.editProfile(position, new Profile(nameText.trim().isEmpty() ? profile.getName() : nameText));
            adapter.notifyDataSetChanged();
            dialog.dismiss();
        });

        builder.onNegative((dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void openDeleteDialog(int position) {
        Profile p = ProfileManagement.getProfile(position);
        new MaterialDialog.Builder(requireContext())
                .title(getString(R.string.profiles_delete_submit_heading))
                .content(getString(R.string.profiles_delete_message, p.getName()))
                .positiveText(getString(R.string.yes))
                .onPositive((dialog, which) -> {
                    ProfileManagement.removeProfile(position);
                    DbHelper dbHelper = new DbHelper(getContext());
                    dbHelper.deleteAll();
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                })
                .onNegative((dialog, which) -> dialog.dismiss())
                .negativeText(getString(R.string.no))
                .show();
    }

    private void setPreferredProfile(int position) {
        ProfileManagement.setPreferredProfilePosition(position);
        preferredProfilePos = ProfileManagement.getPreferredProfilePosition();
        adapter.notifyDataSetChanged();
    }

}
