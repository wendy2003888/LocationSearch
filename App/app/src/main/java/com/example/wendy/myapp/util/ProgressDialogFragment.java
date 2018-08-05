package com.example.wendy.myapp.util;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.wendy.myapp.R;

public class ProgressDialogFragment extends DialogFragment {

    private static final String ARG_ACTION = "action";

    private int action;
    private TextView actionText;
    private ProgressBar progressBar;

    public ProgressDialogFragment() {
    }

    public static ProgressDialogFragment newInstance(int action) {
        ProgressDialogFragment fragment = new ProgressDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ACTION, action);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            action = getArguments().getInt(ARG_ACTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        View rootView =  inflater.inflate(R.layout.pop_progress, container, false);
        actionText = (TextView)rootView.findViewById(R.id.action);
        actionText.setText(action);
        progressBar = (ProgressBar)rootView.findViewById(R.id.progress_bar);
        return rootView;
    }

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


    public ProgressBar getProgressBar() {
        return progressBar;
    }
}
