package com.example.android.bluetoothlegatt.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.bluetoothlegatt.DeviceControlActivity;
import com.example.android.bluetoothlegatt.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MyDialog extends DialogFragment {

    LayoutInflater inflater;
    View v;
    Spinner spinnerType;
    EditText description;
    DateFormat startdf = new SimpleDateFormat("EEE MMM d HH:mm");

    //TODO: Get rid of Static members and implement an Interface!

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.my_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v).setPositiveButton("Start", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                DeviceControlActivity.clearData();

                DeviceControlActivity.typeofAnalyse = spinnerType.getSelectedItem().toString();
                DeviceControlActivity.descriptionofAnalyse = description.getText().toString();
                DeviceControlActivity.startedDate = startdf.format(Calendar.getInstance().getTime());

                Toast.makeText(getActivity(), spinnerType.getSelectedItem().toString() + " type analysis started", Toast.LENGTH_SHORT).show();

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setTitle("New Analyse");

        // Creating Analyze Type Spinner
        spinnerType = (Spinner) v.findViewById(R.id.spinnerType);
        List<String> types = new ArrayList<String>();
        types.add("Endurance");
        types.add("Strength Training");
        types.add("Sports specific");
        types.add("Other");
        ArrayAdapter<String> spinnerTypeAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, types);
        spinnerTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(spinnerTypeAdapter);

        description = (EditText) v.findViewById(R.id.txt_description);

        AlertDialog dialog = builder.create();
        return dialog;
    }
}
