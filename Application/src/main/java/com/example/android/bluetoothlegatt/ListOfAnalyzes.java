package com.example.android.bluetoothlegatt;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ListOfAnalyzes extends Activity {

    private AnalyzesDBadapter dbHelper;
    private SimpleCursorAdapter dataAdapter;
    private ListView mylistView;
    public int[] arrayOfHR;
    public double finalTRIMP;
    public List<Integer> minuteAvgsList;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dbHelper.close();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_analyzes);

        dbHelper = new AnalyzesDBadapter(this);
        dbHelper.open();

        displayListView();

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

        dataAdapter = new SimpleCursorAdapter(this, R.layout.custom_listitem_analyzes, cursor, columns, to, 0);
        mylistView = (ListView) findViewById(R.id.SecondChanceList);
        mylistView.setAdapter(dataAdapter);

        mylistView.setClickable(true);

        mylistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                // Get the state's capital from this row in the database.
                String typeOfTraining = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                String averageHR = cursor.getString(cursor.getColumnIndexOrThrow("avg"));
                String maximumHR = cursor.getString(cursor.getColumnIndexOrThrow("max"));
                String minimumHR = cursor.getString(cursor.getColumnIndexOrThrow("min"));
                String startTime = cursor.getString(cursor.getColumnIndexOrThrow("starttime"));
                String endTime = cursor.getString(cursor.getColumnIndexOrThrow("endtime"));
                String longHRflow = cursor.getString(cursor.getColumnIndexOrThrow("hrflow"));
                String maximalHR = cursor.getString(cursor.getColumnIndexOrThrow("maximal_hr"));
                String restingHR = cursor.getString(cursor.getColumnIndexOrThrow("resting_hr"));
                String gender = cursor.getString(cursor.getColumnIndexOrThrow("gndr"));

                stringToArrayConverter(longHRflow);

                if (arrayOfHR.length <= 25) {
                    // Say that not enough data here
                    finalTRIMP = 0;
                } else {
                    createMinuteAvgsArray(arrayOfHR);

                    calculateTRIMP(minuteAvgsList, maximalHR, restingHR, gender);
                }



              /*  Toast.makeText(getApplicationContext(), "Here: " + longHRflow
                        + " " + averageHR
                        + " " + maximumHR
                        + " " + minimumHR
                        , Toast.LENGTH_SHORT).show();
                        */
                final Intent intent = new Intent(ListOfAnalyzes.this, ListItemContentandGraph.class);
                intent.putExtra(ListItemContentandGraph.EXTRAS_TYPE_OF_TRAINING, typeOfTraining);
                intent.putExtra(ListItemContentandGraph.EXTRAS_AVERAGE_HR, averageHR);
                intent.putExtra(ListItemContentandGraph.EXTRAS_MAXIMUM_HR, maximumHR);
                intent.putExtra(ListItemContentandGraph.EXTRAS_MINIMUM_HR, minimumHR);
                intent.putExtra(ListItemContentandGraph.EXTRAS_START_TIME_OF_TRAINING, startTime);
                intent.putExtra(ListItemContentandGraph.EXTRAS_END_TIME_OF_TRAINING, endTime);
                intent.putExtra(ListItemContentandGraph.EXTRAS_HR_FLOW, longHRflow);
                intent.putExtra(ListItemContentandGraph.PROFILE_MAXIMAL_HR, maximalHR);
                intent.putExtra(ListItemContentandGraph.PROFILE_RESTING_HR, restingHR);
                intent.putExtra(ListItemContentandGraph.PROFILE_GENDER, gender);
                intent.putExtra(ListItemContentandGraph.FINAL_TRIMP, finalTRIMP);

                startActivity(intent);

            }
        });

       /* mylistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                Object o = mylistView.getItemAtPosition(position);
    /* write you handling code like...
    String st = "sdcard/";
    File f = new File(st+o.toString());
    // do whatever u want to do with 'f' File object

        //    }
        }); */
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

        finalTRIMP = 0;
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
            finalTRIMP = finalTRIMP + TRIMP;
        }

        //DecimalFormat df = new DecimalFormat("#0.0");
        //trimpResultA.setText(df.format(finalTRIMP));
    }

}
