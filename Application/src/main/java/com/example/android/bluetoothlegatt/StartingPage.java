package com.example.android.bluetoothlegatt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class StartingPage extends FragmentActivity {

    // Bringing the device's name and address back to Starting page.
    // Then to transfer them to the DeviceControlActivity
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    public String mDeviceName;
    public String mDeviceAddress;

    public String profileGender;
    public String profileAgeGroup;
    public int profileAthletecism;
    public int restingHR;
    public int maximalHR;

    private static final String TAGOS = "TAGGG";

    private ImageButton searchButton;
    private ImageButton alertDialog;
    private ImageButton startAnalyseButton;
    private ImageButton goToDBButton;
    public TextView lblGender, lblAgegroup, lblAthletecism, lblRestingHR, lblMaximalHR;
    public Intent i;

    SharedPreferences sharedPref;


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAGOS, "onResume!");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_page);
        Log.i(TAGOS, "onCreate!");
        initializingComponents();



        sharedPref= getSharedPreferences("mypref", 0);
        profileGender = sharedPref.getString("gender", "");
        profileAgeGroup = sharedPref.getString("ageGroup", "");
        profileAthletecism = sharedPref.getInt("athletecism", 0);
        restingHR = sharedPref.getInt("restHR", 0);
        maximalHR = sharedPref.getInt("maximalHR", 0);

        lblGender.setText(profileGender);
        lblAgegroup.setText(profileAgeGroup);
        lblAthletecism.setText(Integer.toString(profileAthletecism));
        lblRestingHR.setText(Integer.toString(restingHR));
        lblMaximalHR.setText(Integer.toString(maximalHR));


        final Intent intent = getIntent();
        // TODO: check usability of this:
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);


        Log.i(TAGOS, "Device Name: " + mDeviceName + "   Device Address: " + mDeviceAddress);

        searchButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        i = new Intent(StartingPage.this, DeviceScanActivity.class);
                        startActivity(i);
                    }
                }
        );

        startAnalyseButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        final Intent intent = new Intent(StartingPage.this, DeviceControlActivity.class);
                        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, mDeviceName);
                        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, mDeviceAddress);
                        startActivity(intent);
                    }
                }
        );

        goToDBButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        Intent goDBintent = new Intent(StartingPage.this, ListOfAnalyzes.class);
                        startActivity(goDBintent);
            }
        });

        alertDialog = (ImageButton) findViewById(R.id.alertDialog);
        alertDialog.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        Intent gotoProfileIntent = new Intent(StartingPage.this, UserProfileActivity.class);
                        startActivity(gotoProfileIntent);

                    }
                });

    }

    public void initializingComponents(){

        lblGender = (TextView) findViewById(R.id.lbl_genderTest);
        lblAgegroup = (TextView) findViewById(R.id.lbl_ageTest);
        lblAthletecism = (TextView) findViewById(R.id.lbl_athletecismTest);
        lblRestingHR = (TextView) findViewById(R.id.lbl_restingHR);
        lblMaximalHR = (TextView) findViewById(R.id.lbl_maximalHR);
        searchButton = (ImageButton) findViewById(R.id.searchImageButton);
        startAnalyseButton = (ImageButton) findViewById(R.id.startAnalyseButton);
        goToDBButton = (ImageButton) findViewById(R.id.databaseButton);
        alertDialog = (ImageButton) findViewById(R.id.alertDialog);

    }

}