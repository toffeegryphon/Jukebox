package edu.illinois.cs465.jukebox;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class CustomDialogFragment extends DialogFragment {
    Context context;
    String title;
    String message;
    String positiveButton;
    String negativeButton;
    Class goToClass;

    public CustomDialogFragment(Context _context, String _title, String _message, String _positiveButton, String _negativeButton, Class _goToClass) {
        context = _context;
        title = _title;
        message = _message;
        positiveButton = _positiveButton;
        negativeButton = _negativeButton;
        goToClass = _goToClass;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(positiveButton, (dialog, which) -> startActivity(new Intent(context.getApplicationContext(), goToClass)));
        alertDialogBuilder.setNegativeButton(negativeButton, null);

        return alertDialogBuilder.create();
    }
}