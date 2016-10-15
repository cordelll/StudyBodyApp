package com.example.android.bluetoothlegatt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends FragmentActivity{

    private SeekBar seekBar;
    private TextView descriptionText;
    private Spinner ageSpinner;
    private RadioGroup radioGroup;
    private Button saveBtn;

    String poorAthletecism = "Poor: You cannot do sports, you barely walk and  mostly stay in one position";
    String belowAverageAthletecism = "Below average: Usually you do not do any sports, you find exhausting to climb up the stairs";
    String averageAthletecism = "Average: You have average physical abilities, you do sports only in school when it is mandatory";
    String aboveAverageAthletecism = "Above average: You do some physical activities at your spare times to stay fit";
    String goodAthletecism = "Good: You do jogging several times a week, your life is full of activities like hiking, team game playing etc.";
    String excellentAthletecism = "Excellent: You take trainings serious, you do sports 4 - 5 times a week";
    String athleteAthletecism = "Athlete: You are a professional athlete and you keep training almost every day, sometimes twice a day";

    String genderCheck;
    String ageGroupCheck;
    int athletecismCheck;
    int maximalHR;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public static int progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initializeComponents();

        descriptionText.setText("Covered: " + seekBar.getProgress() + "/" + seekBar.getMax());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                athletecismLevel(progress);
                athletecismCheck = progresValue;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        //radioGroup.clearCheck();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    Toast.makeText(UserProfileActivity.this, rb.getText(), Toast.LENGTH_SHORT).show();
                    genderCheck = rb.getText().toString();
                }

            }
        });

        // Saves selected data and transferes it to the StartingPage
        saveBtn.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        ageGroupCheck = ageSpinner.getSelectedItem().toString();

                        calculateMaximalHR(genderCheck, ageGroupCheck);

                        sharedPref = getSharedPreferences("mypref", 0);
                        editor = sharedPref.edit();
                        editor.remove("gender");
                        editor.remove("ageGroup");
                        editor.remove("athletecism");
                        editor.remove("maximalHR");
                        editor.putString("gender", genderCheck);
                        editor.putString("ageGroup", ageGroupCheck);
                        editor.putInt("athletecism", athletecismCheck);
                        editor.putInt("maximalHR", maximalHR);
                        editor.commit();

                        final Intent intent = new Intent(UserProfileActivity.this, StartingPage.class);
                        startActivity(intent);
                    }
                });

    }

    private void calculateMaximalHR(String gender, String age) {
        /***************************
         MAX HR ==
         Male: mhr = 209.6-0.72*age
         Female: mhr = 207.2-0.65*age
         ***************************/
        int ageInt = Integer.parseInt(age);

        if(gender.equals("Female")){
            maximalHR = Math.round(207.2f - 0.65f*ageInt);
        }else{
            maximalHR = Math.round(209.6f-0.72f*ageInt);
        }
    }


    private void athletecismLevel(int value){

        switch (value){
            case 1:
                descriptionText.setText(value + " " + poorAthletecism);
                break;
            case 2:
                descriptionText.setText(value + " " + belowAverageAthletecism);
                break;
            case 3:
                descriptionText.setText(value + " " + averageAthletecism);
                break;
            case 4:
                descriptionText.setText(value + " " + aboveAverageAthletecism);
                break;
            case 5:
                descriptionText.setText(value + " " + goodAthletecism);
                break;
            case 6:
                descriptionText.setText(value + " " + excellentAthletecism);
                break;
            case 7:
                descriptionText.setText(value + " " + athleteAthletecism);
                break;
            case 0:
                descriptionText.setText(value + " Move the seek bar");
                break;
        }

    }


    // Initializing components
    private void initializeComponents(){

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        seekBar = (SeekBar) findViewById(R.id.seekBar1);
        descriptionText = (TextView) findViewById(R.id.descriptionText);
        saveBtn = (Button) findViewById(R.id.saveButton);
        ageSpinner = (Spinner) findViewById(R.id.ageSpinner);

        sharedPref = getSharedPreferences("mypref", 0);
        genderCheck = sharedPref.getString("gender", "");
        ageGroupCheck = sharedPref.getString("ageGroup", "");;
        athletecismCheck = sharedPref.getInt("athletecism", 0);;

        seekBar.setProgress(athletecismCheck);
        if(genderCheck.equals("Male")){
            ((RadioButton)radioGroup.findViewById(R.id.radio_male)).setChecked(true);
        }else if(genderCheck.equals("Female")){
            ((RadioButton)radioGroup.findViewById(R.id.radio_female)).setChecked(true);
        } else{ radioGroup.clearCheck(); }


        List<String> types = new ArrayList<String>();
        String intToString;
        for(int i = 18; i<75; i++){
            intToString = Integer.toString(i);
            types.add(intToString);
        }
        ArrayAdapter<String> spinnerTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, types);
        spinnerTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageSpinner.setAdapter(spinnerTypeAdapter);
        if(!ageGroupCheck.equals(null)) {
            int spinnerPosition = spinnerTypeAdapter.getPosition(ageGroupCheck);
            ageSpinner.setSelection(spinnerPosition);
        }
    }

}
