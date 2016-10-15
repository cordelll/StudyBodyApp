package com.example.android.bluetoothlegatt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.android.bluetoothlegatt.dialogs.CompareToAnotherDialog;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ListItemContentandGraph extends FragmentActivity implements CompareToAnotherDialog.CompareDialogListener {

    TextView typeA, timeA, avgA, maxA, minA, trimpA, trimpResultA;
    TextView avgB, maxB, minB, trimpResultB;
    TextView appearAvg, appearMin, appearMax, appearTrimp;

    public static final String TAG3 = "trimpCheck";

    public static final String EXTRAS_TYPE_OF_TRAINING = "TYPE_OF_TRAINING";
    public static final String EXTRAS_START_TIME_OF_TRAINING = "START_TIME_OF_TRAINING";
    public static final String EXTRAS_END_TIME_OF_TRAINING = "END_TIME_OF_TRAINING";
    public static final String EXTRAS_AVERAGE_HR = "AVERAGE_HR";
    public static final String EXTRAS_MAXIMUM_HR = "MAXIMUM_HR";
    public static final String EXTRAS_MINIMUM_HR = "MINIMUM_HR";
    public static final String EXTRAS_HR_FLOW = "HR_FLOW";
    public static final String PROFILE_MAXIMAL_HR = "PROFILE_MAXIMAL_HR";
    public static final String PROFILE_RESTING_HR = "PROFILE_RESTING_HR";
    public static final String PROFILE_GENDER = "PROFILE_GENDER";
    public static final String FINAL_TRIMP = "FINAL_TRIMP";

    private Button compareBtn;
    private View lineCompare1, lineCompare2;

    public String t_type, t_start_time, t_end_time, t_avg, t_max, t_min, t_hr_flow, t_profile_maximal, t_profile_resting, t_profile_gender;
    public double t_trimp;
    public int[] arrayOfHR;
    public double finalTRIMP;
    public List<Integer> minuteAvgsList;
    CompareToAnotherDialog compareToAnotherDialog = new CompareToAnotherDialog();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Take the data
        getIntents();
        stringToArrayConverter(t_hr_flow);
        setContentView(R.layout.activity_list_item_contentand_graph);

        // Init
        initializeComponents();

        compareBtn.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        compareToAnotherDialog.show(getFragmentManager(), "CompareToAnotherDialog");

                    }
                }
        );


    }



    private void calculateTRIMP(List<Integer> avgInMinute, String maximalHB, String restingHB, String gender) {

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
            Log.i(TAG3, "TRIMP(" + imi + ") :" + TRIMP);
            finalTRIMP = finalTRIMP + TRIMP;
        }
        DecimalFormat df = new DecimalFormat("#0.0");
        trimpResultA.setText(df.format(finalTRIMP));
    }

    private void getIntents(){
        final Intent intent = getIntent();
        t_type = intent.getStringExtra(EXTRAS_TYPE_OF_TRAINING);
        t_start_time = intent.getStringExtra(EXTRAS_START_TIME_OF_TRAINING);
        t_end_time = intent.getStringExtra(EXTRAS_END_TIME_OF_TRAINING);
        t_avg = intent.getStringExtra(EXTRAS_AVERAGE_HR);
        t_max = intent.getStringExtra(EXTRAS_MAXIMUM_HR);
        t_min = intent.getStringExtra(EXTRAS_MINIMUM_HR);
        t_hr_flow = intent.getStringExtra(EXTRAS_HR_FLOW);
        t_profile_maximal = intent.getStringExtra(PROFILE_MAXIMAL_HR);
        t_profile_resting = intent.getStringExtra(PROFILE_RESTING_HR);
        t_profile_gender = intent.getStringExtra(PROFILE_GENDER);
        t_trimp = intent.getDoubleExtra(FINAL_TRIMP, 0);

    }

    private void initializeComponents() {
        typeA = (TextView) findViewById(R.id.type_analysis_graph);
        timeA = (TextView) findViewById(R.id.time_analysis_graph);
        avgA = (TextView) findViewById(R.id.avg_analysis_graph);
        maxA = (TextView) findViewById(R.id.max_analysis_graph);
        minA = (TextView) findViewById(R.id.min_analysis_graph);
        trimpA = (TextView) findViewById(R.id.trimp);
        minuteAvgsList = new ArrayList<Integer>();
        trimpResultA = (TextView) findViewById(R.id.trimpResult);

        ///
        compareBtn = (Button) findViewById(R.id.compareBtn);
        ///
        appearAvg = (TextView) findViewById(R.id.AVGHR_lblB);
        avgB = (TextView) findViewById(R.id.avg_analysis_graphB);
        appearMax = (TextView) findViewById(R.id.MAXHR_lblB);
        maxB = (TextView) findViewById(R.id.max_analysis_graphB);
        appearMin = (TextView) findViewById(R.id.MINHB_lblB);
        minB = (TextView) findViewById(R.id.min_analysis_graphB);
        appearTrimp = (TextView) findViewById(R.id.trimpB);
        trimpResultB = (TextView) findViewById(R.id.trimpResultB);
        //
        lineCompare1 = findViewById(R.id.lineCompare1);
        lineCompare2 = findViewById(R.id.lineCompare2);



        typeA.setText(t_type);
        timeA.setText(t_start_time);
        avgA.setText(t_avg);
        maxA.setText(t_max);
        minA.setText(t_min);

        if (t_trimp != 0){
        DecimalFormat df = new DecimalFormat("#0.0");
        trimpResultA.setText(df.format(t_trimp));
        } else trimpResultA.setText("-");
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

   // GraphFragment graphFragment;

    @Override
    public void compareToNew(String _averageHR, String _maximumHR, String _minimumHR, String _longHRflow, String _maximalHR, String _restingHR, String _gender, double trimp) {
       // graphFragment = new GraphFragment();
        avgB.setText(_averageHR);
        maxB.setText(_maximumHR);
        minB.setText(_minimumHR);
        appearAvg.setText("AVG HR ");
        appearTrimp.setText("TRIMP ");
        appearMin.setText("MIN HR ");
        appearMax.setText("MAX HR ");
        if (trimp != 0){
            DecimalFormat dff = new DecimalFormat("#0.0");
            trimpResultB.setText(dff.format(trimp));
        } else trimpResultB.setText("-");

        lineCompare1.setBackgroundColor(0xffffff00);
        lineCompare2.setBackgroundColor(0xffffff00);

       // graphFragment.addAnotherLine();
        //trimpResultB.setText(_longHRflow);
    }
}
