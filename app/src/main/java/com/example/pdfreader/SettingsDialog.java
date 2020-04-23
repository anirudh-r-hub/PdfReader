package com.example.pdfreader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class SettingsDialog extends AppCompatDialogFragment {

    private SeekBar seekbarPitch;
    private SeekBar seekbarSpeed;
    int pitch, speed, pitch_prev, speed_prev;
    private SettingsDialogListener listener;


    SettingsDialog(int pitch, int speed)
    {
        pitch_prev = pitch;
        speed_prev = speed;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.voicesettings_dialog, null);

        seekbarPitch = view.findViewById(R.id.seek_bar_pitch);
        seekbarSpeed = view.findViewById(R.id.seek_bar_speed);

        seekbarPitch.setProgress(pitch_prev);
        seekbarSpeed.setProgress(speed_prev);

        builder.setView(view)
                .setTitle("Voice Settings")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        pitch = seekbarPitch.getProgress();
                        speed = seekbarSpeed.getProgress();



                        listener.apply(pitch, speed);
                    }
                });



        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            listener = (SettingsDialogListener) context;

        }catch(ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement SettingsDialogListener");
        }

    }

    public interface SettingsDialogListener {
        void apply(int pitch, int speed);

    }



}


