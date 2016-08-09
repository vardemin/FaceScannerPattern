package com.appelsin.olympicsportfacescanner;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

public class MyDialog extends DialogFragment {
    private CustomDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        listener = (CustomDialogListener)getActivity();
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog, null);
        ImageButton btnOk = (ImageButton) v.findViewById(R.id.dialog_yes);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                listener.onOk();
            }
        });
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        ImageButton btnNo = (ImageButton) v.findViewById(R.id.dialog_no);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        dialog.setView(v);

        return dialog.create();
    }
    public interface CustomDialogListener {
        public void onOk();
    }
}