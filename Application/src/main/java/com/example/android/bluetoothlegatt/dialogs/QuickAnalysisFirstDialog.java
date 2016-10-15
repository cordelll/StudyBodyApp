package com.example.android.bluetoothlegatt.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

//deleted two imports (unused)

import com.example.android.bluetoothlegatt.DeviceControlActivity;
import com.example.android.bluetoothlegatt.R;

import org.w3c.dom.Text;


public class QuickAnalysisFirstDialog extends DialogFragment{

    private CheckBox testOrRest;
    private TextView warnifchecked;
    private int analyzingOrsaving;

    LayoutInflater inflater;
    View v;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        inflater = LayoutInflater.from(getActivity());
        v = inflater.inflate(R.layout.dialog_quick_analysis_first, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v).setPositiveButton("Start", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                DeviceControlActivity.clearData();

                EditDialogListener activity = (EditDialogListener) getActivity();
                activity.runQuickAnalysis(analyzingOrsaving);

                //TODO:
                // Step 2: Run the waiting code and clear the data
              //  DCActivity.testingYo("Hello, I work");


                Toast.makeText(getActivity(), " Analyze started, please wait ", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        testOrRest = (CheckBox) v.findViewById(R.id.restORtest);
        warnifchecked = (TextView) v.findViewById(R.id.warnIfCheched);

        testOrRest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked == true) {
                     warnifchecked.setText("Override your resting heart rate only if you know that you are fully recovered");
                     analyzingOrsaving = 1;
                } else {
                     warnifchecked.setText("");
                     analyzingOrsaving = 0;
                    }
            }
        });


        builder.setTitle("Quick Recovery Analysis");

        AlertDialog dialog = builder.create();
        return dialog;
    }

    public interface EditDialogListener {
        void runQuickAnalysis(int _testORrest);
    }

}
