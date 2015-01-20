package com.example.propertylist.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;

import com.example.propertylist.R;

public class ImageAlertDialog extends DialogFragment {

    public static ImageAlertDialog newInstance(String title, String Message) {
        ImageAlertDialog frag = new ImageAlertDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", Message);
        frag.setArguments(args);
        return frag;
    }


    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");


        ContextThemeWrapper ctwrapper = new ContextThemeWrapper( getActivity(), R.style.AppTheme );
        AlertDialog.Builder custom = new AlertDialog.Builder(ctwrapper);
        View imageView = getActivity().getLayoutInflater().inflate(R.layout.no_image_dialog, null, false);
        TextView titleView = (TextView) imageView.findViewById(R.id.no_image_textview);
        titleView.setText(title);
        TextView messageView = (TextView) imageView.findViewById(R.id.no_image_text);
        messageView.setText(message);

        custom.setView(imageView)
                .setCancelable(false)
                .setNegativeButton(getString(R.string.alert_dialog_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                );
        return custom.create();
    }
}// end ImageAlertDialog class