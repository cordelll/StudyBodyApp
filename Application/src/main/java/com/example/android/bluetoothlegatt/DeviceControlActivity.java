/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.bluetoothlegatt;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;

import com.example.android.bluetoothlegatt.dialogs.MyDialog;
import com.example.android.bluetoothlegatt.dialogs.QuickAnalysisFirstDialog;
import com.example.android.bluetoothlegatt.dialogs.QuickAnalysisFirstDialog.EditDialogListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class DeviceControlActivity extends FragmentActivity implements EditDialogListener, QuickAnalysisFragment.QuickAnalysisFragmentListener {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();
    private static final String TAGS = "MyActivity";
    private static final String TAG2 = "clickCheck";

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mConnectionState, mDataField, percentage;
    private String mDeviceAddress, mDeviceName;
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    public Button startButton, stopButton;
    private int restingHR, maximalHR;
    private String genderPref, agePref;
    private int athleticismPref;

    DateFormat enddf = new SimpleDateFormat("HH:mm");

    public static String startedDate, stoppedDate, descriptionofAnalyse, typeofAnalyse;
    public static int avgofAnalyse, minofAnalyse, maxofAnalyse;
    public static String longhrBeatsFlow;
    public static List<Integer> hrBeatsFlow;

    // Pop-up dialogs
    Timer t;
    MyDialog myDialog = new MyDialog();
    QuickAnalysisFirstDialog quickAnalysisFirstDialog = new QuickAnalysisFirstDialog();

    private static int isaData;
    private static int minHB = 65;
    private static int maxHB = 0;
    private static float sumHB = 0;
    private static float countHB = 0;

    Analysis analyze;
    private AnalyzesDBadapter dbHelper;
    SharedPreferences sharedPref;
    BodyTypeCheckForQuickAnalysis bodyTypeCheckForQuickAnalysis;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_activity);

        initializeComponents();

        dbHelper = new AnalyzesDBadapter(this);
        // TODO: SHOULD I OPEN DB HERE???
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        stopButton.setEnabled(false);

        // Sets up UI references.
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        startButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        myDialog.show(getSupportFragmentManager(), "MyDialog");
                        Log.i(TAG2, "Start Button Clicked: DATE: " + startedDate);
                        startButton.setEnabled(false);
                        stopButton.setEnabled(true);
                        Log.i(TAG2, "New timer created");

                        t = new Timer();
                        t.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (isaData > 0){
                                hrBeatsFlow.add(isaData);
                                }
                                Log.i(TAG2, hrBeatsFlow.toString());
                            }
                        }, 0, 6000);
                    }
                }
        );

        stopButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Runnable rd = new Runnable() {
                            @Override
                            public void run() {
                                synchronized (this) {
                                    try {
                                        // stops the timer
                                        t.cancel();

                                        Log.i(TAG2, "stopButtonClicked");
                                        avgofAnalyse = Math.round(sumHB / countHB);
                                        minofAnalyse = minHB;
                                        maxofAnalyse = maxHB;
                                        stoppedDate = enddf.format(Calendar.getInstance().getTime());

                                        // save list of HR to array
                                        int[] hrBeatArray = new int[hrBeatsFlow.size()];
                                        for(int i = 0; i < hrBeatsFlow.size(); i++) hrBeatArray[i] = hrBeatsFlow.get(i);
                                        longhrBeatsFlow = Arrays.toString(hrBeatArray);

                                        analyze = new Analysis(typeofAnalyse,
                                                descriptionofAnalyse, startedDate,
                                                stoppedDate, avgofAnalyse, minofAnalyse, maxofAnalyse, longhrBeatsFlow, maximalHR, restingHR, genderPref);

                                        dbHelper.open();
                                        dbHelper.createAnalysis(analyze);
                                        dbHelper.close();


                                    } catch (Exception e) {
                                        Log.i(TAG2, "EXCEPTION IN STOP BUTTON !!!!");
                                    }
                                }
                            }
                        };
                        Thread stopButtonThread = new Thread(rd);
                        stopButtonThread.start();
                        startButton.setEnabled(true);
                        stopButton.setEnabled(false);


                        Toast.makeText(DeviceControlActivity.this, " Analysis successfully saved into the Database ", Toast.LENGTH_SHORT).show();
                    }

                }
        );

        // TODO: Check what is this
        // getActionBar().setTitle("Analyze");
        // getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // TODO: Do I need this at all?
        // Stops retrieving data from sensor !!! Use when the time of analyse is over.
        //  unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // useful change???
        unregisterReceiver(mGattUpdateReceiver);
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    public void initializeComponents(){

        sharedPref= getSharedPreferences("mypref", 0);
        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField = (TextView) findViewById(R.id.data_value);
        percentage = (TextView) findViewById(R.id.percentOfMaxHR);
        restingHR = sharedPref.getInt("restHR", 0);
        maximalHR = sharedPref.getInt("maximalHR", 0);
        genderPref = sharedPref.getString("gender", "");
        agePref = sharedPref.getString("ageGroup", "");
        athleticismPref = sharedPref.getInt("athletecism", 0);
        hrBeatsFlow = new ArrayList<Integer>();

    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                bringHRCharacteristics(mBluetoothLeService.getSupportedGattServices());

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));

                //taking a heart rate value and assigning it to my isaData variable
                isaData = Integer.parseInt(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                saveHBeats(isaData);
                displayPercentage(isaData);
            }
        }
    };

    // Display percentage
    int percentageValue;
    private void displayPercentage(int _isaData) {
        percentageValue = _isaData * 100 / maximalHR;
        percentage.setText(percentageValue +"%");

    }

    // takes HR and finds then stores the max, min and avg HR

    private void saveHBeats(final int hrData) {

        Runnable r = new Runnable() {
            @Override
            public void run() {
                synchronized (this){
                    try{
                        // If received number is not zero
                        if(hrData > 0) {

                            sumHB = sumHB + hrData;
                            countHB++;

                            if (hrData > maxHB) {
                                maxHB = hrData;
                                Log.i(TAGS, "New maxHB: " + maxHB);
                            } else if (hrData < minHB) {
                                minHB = hrData;
                                Log.i(TAGS, "New minHB: " + minHB);
                            }
                            Log.i(TAGS, "this is hrData: " + hrData + "\n");
                            Log.i(TAGS, "Sum: " + sumHB + "\n");
                            Log.i(TAGS, "Average HeartBeat: " + sumHB / countHB);
                        } else { Log.i(TAGS, "0 hrData came: " + hrData); }
                    } catch (Exception e){ Log.i(TAGS, "Exception in 'saveHbeats' runnable");}
                }
            }
        };

        Thread isaThread = new Thread(r);
        isaThread.start();
    }

    private void clearUI() {
        mDataField.setText(R.string.no_data);
    }

    // clear all data after analyse is finished
    public static void clearData(){
        hrBeatsFlow.clear();
        typeofAnalyse = "";
        descriptionofAnalyse = "";
        startedDate = "";
        stoppedDate = "";
        avgofAnalyse = 0;
        maxofAnalyse = 0;
        minofAnalyse = 0;
        minHB = 65;
        sumHB = 0;
        countHB = 0;
        maxHB = 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    // Displays HR
    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);

        }
    }

    // Filters through all characteristics and brings me back the only needed one
    private void bringHRCharacteristics(List<BluetoothGattService> gattServices){

        if(gattServices == null) Log.i(TAGS, " No Gatt Services visible ");
        String HRServiceUUID = "0000180d-0000-1000-8000-00805f9b34fb";
        String HRMeasurementUUID = "00002a37-0000-1000-8000-00805f9b34fb";

        for (BluetoothGattService gattService : gattServices) {
            if(gattService.getUuid().toString().equals(HRServiceUUID)){
                Log.i(TAGS, " Came into gatt Service: " + gattService.getUuid().toString());

                final List<BluetoothGattCharacteristic> gattSeveralCharacteristic = gattService.getCharacteristics();

                for(BluetoothGattCharacteristic bleGatt : gattSeveralCharacteristic){
                    if (bleGatt.getUuid().toString().equals(HRMeasurementUUID)){

                        Log.i(TAGS, " BLeGatt: " + bleGatt.getUuid().toString());


                        final int charaProp = bleGatt.getProperties();

                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLeService.readCharacteristic(bleGatt);
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mNotifyCharacteristic = bleGatt;
                            mBluetoothLeService.setCharacteristicNotification(
                                    bleGatt, true);
                        }
                    }
                }
            }
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    public void startQuickAnalysis() {
        quickAnalysisFirstDialog.show(getSupportFragmentManager(), "QuickAnalysisFirstDialog");
    }

    int testOrRest;
    @Override
    public void runQuickAnalysis(int _testORrest) {
        Toast.makeText(DeviceControlActivity.this, "Started", Toast.LENGTH_SHORT).show();
        testOrRest = _testORrest;
        startButton.setEnabled(false); //disable other buttons
        clearData(); // clear all data
        quickAnalysis(); //start 60 sec analysis
        QuickAnalysisFragment.fillValues(1,
                Integer.toString(0) + " %",
                "",
                "Please wait",
                "Analyzing...");

    }

        // 1 minute quick analysis
        public void quickAnalysis(){

            Runnable quicktestRun = new Runnable() {
                @Override
                public void run() {
                    long futureTime = System.currentTimeMillis() + 60000;
                    while(System.currentTimeMillis() < futureTime){
                        synchronized (this){
                            try{
                                wait(futureTime - System.currentTimeMillis());
                            }catch (Exception e){ Log.i(TAGS, "Exception in quickAnalysis Runnable");}
                        }
                    }
                    quickAnalysisHandler.sendEmptyMessage(0);
                }

            };

            Thread waitThread = new Thread(quicktestRun);
            waitThread.start();

        }

    Handler quickAnalysisHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //TODO: Finish

            // TODO: Create a method for all this

             if (testOrRest == 1){
                updateRestingHR(avgofAnalyse);
                 Toast.makeText(DeviceControlActivity.this, "Updating, please wait", Toast.LENGTH_SHORT).show();
            }else {
                 //code to run quickAnalysis
                 quickRecoveryAnalysis();
                 Toast.makeText(DeviceControlActivity.this, "Analyzing, please wait", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void updateRestingHR(int avg){
        avgofAnalyse = Math.round(sumHB / countHB);
        sharedPref= getSharedPreferences("mypref", 0);
        SharedPreferences.Editor editor;
        // Average!!! pass ot
         editor = sharedPref.edit();
         editor.remove("restHR");
         editor.putInt("restHR", avgofAnalyse);
         editor.commit();
        QuickAnalysisFragment.fillValues(100, "100", "AVG: " + avgofAnalyse, "Resting HR updated", "Finished");
        Toast.makeText(DeviceControlActivity.this, "Resting HR updated", Toast.LENGTH_SHORT).show();
        startButton.setEnabled(true);

    }

    private void quickRecoveryAnalysis() {
        int avgHR = avgofAnalyse = Math.round(sumHB / countHB);
        bodyTypeCheckForQuickAnalysis = new BodyTypeCheckForQuickAnalysis(genderPref, agePref, athleticismPref, avgHR, restingHR);
        startButton.setEnabled(true);

    }


}