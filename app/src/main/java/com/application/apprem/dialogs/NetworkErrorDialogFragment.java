package com.application.apprem.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.application.apprem.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;


public class NetworkErrorDialogFragment extends DialogFragment
        implements View.OnClickListener {

    //Constant used as a Fragment Tag identifier
    public static final String DIALOG_FRAGMENT_TAG = NetworkErrorDialogFragment.class.getSimpleName();
    //Constant used for Logs
    private static final String LOG_TAG = NetworkErrorDialogFragment.class.getSimpleName();

    public static NetworkErrorDialogFragment newInstance() {
        //Returning the DialogFragment Instance
        return new NetworkErrorDialogFragment();
    }

    /**
     * Method invoked by the system to create the Dialog to be shown.
     * The layout 'R.layout.network_error_dialog_original' for Network Error
     * will be inflated and returned as Dialog.
     *
     * @param savedInstanceState The last saved instance state of the Fragment,
     *                           or null if this is a freshly created Fragment.
     * @return Return a new Dialog instance to be displayed by the Fragment.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Building the Dialog using the AlertDialog.Builder
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        //Inflating the Network Error Dialog Layout 'R.layout.network_error_dialog_original'
        //(Passing null as we are attaching the layout ourselves to a Dialog)
        View networkErrorLayoutView = LayoutInflater.from(getActivity()).inflate(R.layout.network_error_dialog, null);

        //Retrieving the action buttons
        Button positiveButton = networkErrorLayoutView.findViewById(R.id.network_error_settings_btn_id);
        Button negativeButton = networkErrorLayoutView.findViewById(R.id.network_error_cancel_btn_id);

        //Setting the click listener on the Buttons
        positiveButton.setOnClickListener(this);
        negativeButton.setOnClickListener(this);

        //Setting this prepared layout onto the dialog's builder
        dialogBuilder.setView(networkErrorLayoutView);

        //Returning the Dialog instance built
        return dialogBuilder.create();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {
        //Evaluating based on View's id
        switch (view.getId()) {
            case R.id.network_error_settings_btn_id:
                //When the Positive Button is clicked

                //Creating an Intent to launch the Network Settings
                Intent networkIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                //Verifying that the Intent will resolve to an Activity
                if (networkIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    //Launching the Activity if resolved
                    startActivity(networkIntent);
                }

                dismiss(); //Dismissing the dialog in the end

                break;
            case R.id.network_error_cancel_btn_id:
                //When the Negative Button is clicked

                //Dismissing the dialog without doing any other operation
                dismiss();
                break;
        }
    }

}
