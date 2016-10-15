package com.example.android.bluetoothlegatt.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bluetoothlegatt.AnalyzesDBadapter;
import com.example.android.bluetoothlegatt.DeviceControlActivity;
import com.example.android.bluetoothlegatt.GraphFragment;
import com.example.android.bluetoothlegatt.ListItemContentandGraph;
import com.example.android.bluetoothlegatt.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cordell on 04.07.2016.
 */
public class CompareToAnotherDialog extends DialogFragment {

    private AnalyzesDBadapter dbHelper;
    private SimpleCursorAdapter dataAdapter;
    private ListView mylistView;
    public AlertDialog dialog;

    public int[] arrayOfHR;
    public double finalTRIMP_B;
    public List<Integer> minuteAvgsList;
    public GraphFragment gfragment;

    LayoutInflater inflater;
    View v;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        inflater = LayoutInflater.from(getActivity());
        v = inflater.inflate(R.layout.activity_list_of_analyzes, null);

        // neponyatki
        dbHelper = new AnalyzesDBadapter(v.getContext());
        dbHelper.open();

        displayListView();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHelper.close();
                dialog.cancel();
            }
        });

        builder.setTitle("Compare with");

        dialog = builder.create();
        return dialog;
    }

    private void displayListView() {

        Cursor cursor = dbHelper.fetchAllAnalyzes();

        String[] columns = new String[]{
                AnalyzesDBadapter.KEY_TYPE,
                AnalyzesDBadapter.KEY_DESCRIPTION,
                AnalyzesDBadapter.KEY_START_TIME,
        };

        // Next thing to change - layout
        int[] to = new int[]{
                R.id.typeofAnalys,
                R.id.descriptionofAnalyse,
                R.id.startTimeofAnalyse,
        };

        dataAdapter = new SimpleCursorAdapter(v.getContext(), R.layout.custom_listitem_analyzes, cursor, columns, to, 0);
        mylistView = (ListView) v.findViewById(R.id.SecondChanceList);
        mylistView.setAdapter(dataAdapter);

        mylistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                // Get the state's capital from this row in the database.

                String averageHR = cursor.getString(cursor.getColumnIndexOrThrow("avg"));
                String maximumHR = cursor.getString(cursor.getColumnIndexOrThrow("max"));
                String minimumHR = cursor.getString(cursor.getColumnIndexOrThrow("min"));
                String longHRflow =  cursor.getString(cursor.getColumnIndexOrThrow("hrflow"));
                String maximalHR = cursor.getString(cursor.getColumnIndexOrThrow("maximal_hr"));
                String restingHR = cursor.getString(cursor.getColumnIndexOrThrow("resting_hr"));
                String gender = cursor.getString(cursor.getColumnIndexOrThrow("gndr"));

                stringToArrayConverter(longHRflow);

                if (arrayOfHR.length <= 25) {
                    // Say that not enough data here
                    finalTRIMP_B = 0;
                } else {
                    createMinuteAvgsArray(arrayOfHR);

                    calculateTRIMP(minuteAvgsList, maximalHR, restingHR, gender);
                }

                CompareDialogListener activity = (CompareDialogListener) getActivity();
                activity.compareToNew(averageHR, maximumHR, minimumHR, longHRflow, maximalHR, restingHR, gender, finalTRIMP_B);

                gfragment = (GraphFragment) getFragmentManager()
                        .findFragmentById(R.id.gfragment);
                if (gfragment != null && gfragment.isInLayout()) {
                    gfragment.addAnotherLine(longHRflow);
                }

                // close dialog
                dialog.dismiss();
            }
        });
    }

    public interface CompareDialogListener {
        void compareToNew(String _averageHR, String _maximumHR, String _minimumHR, String _longHRflow, String _maximalHR, String _restingHR, String _gender, double trimp);
    }


    public void stringToArrayConverter(String frFlowString) {

        String[] s = frFlowString.split(", ");
        arrayOfHR = new int[s.length]; // here was changed
        //check this out if works from very first HB
        int counter=0;
        for(int curr = 1; curr < s.length - 1; curr++){ // Hmm...
            arrayOfHR[counter] = Integer.parseInt(s[curr]);
            counter++;
        }
    }

    private void createMinuteAvgsArray(int[] arrayHB){
        minuteAvgsList = new ArrayList<Integer>();
        for(int curr = 0; curr < arrayHB.length; curr++){
        }

        int sumOfFirstTenHB = 0;
        int untilX = 10;
        for(int loope = 0; loope < untilX; loope++) {

            sumOfFirstTenHB = sumOfFirstTenHB + arrayHB[loope];
            if (loope == untilX-1) {
                minuteAvgsList.add(sumOfFirstTenHB / 10);
                untilX = untilX + 10;
                sumOfFirstTenHB = 0;
            }
            if (loope == arrayHB.length-1) break;
        }
    }

    private void calculateTRIMP(List<Integer> avgInMinute, String maximalHB, String restingHB, String gender) {

        finalTRIMP_B = 0;
        float genderValue;
        if (gender.equals("Female")){
            genderValue = 1.67f;
        }else genderValue = 1.92f;

        float HRmaximal = Float.parseFloat(maximalHB);
        float HRrest = Float.parseFloat(restingHB);


        for(int imi = 0; imi <= avgInMinute.size()-1; imi++){

            float hrr1 = avgInMinute.get(imi) - HRrest;
            float hrr2 = HRmaximal - HRrest;
            float HRr = hrr1/hrr2;

            double TRIMP = (1 * HRr * 0.64) * (Math.pow(2.718, (genderValue*HRr)));
            finalTRIMP_B = finalTRIMP_B + TRIMP;
        }

        //DecimalFormat df = new DecimalFormat("#0.0");
        //trimpResultA.setText(df.format(finalTRIMP));
    }



}
