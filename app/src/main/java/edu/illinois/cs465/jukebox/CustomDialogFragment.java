package edu.illinois.cs465.jukebox;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import edu.illinois.cs465.jukebox.model.PartyInfo;

public class CustomDialogFragment extends DialogFragment {
    Context context;
    String title;
    String message;
    String positiveButton;
    String negativeButton;
    Class goToClass;
    MusicService musicService;
    String partyCode;

    public CustomDialogFragment(Context _context, String _title, String _message, String _positiveButton, String _negativeButton, Class _goToClass, MusicService _musicService, String _partyCode) {
        context = _context;
        title = _title;
        message = _message;
        positiveButton = _positiveButton;
        negativeButton = _negativeButton;
        goToClass = _goToClass;
        musicService = _musicService;
        partyCode = _partyCode;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(positiveButton, (dialog, which) -> {
            Intent intent = new Intent(context.getApplicationContext(), goToClass);
            if (partyCode != null) intent.putExtra(PartyInfo.PARTY_CODE, partyCode);
            if (musicService != null) musicService.stopMusicService();
            startActivity(intent);
        });
        alertDialogBuilder.setNegativeButton(negativeButton, null);

        return alertDialogBuilder.create();
    }
}