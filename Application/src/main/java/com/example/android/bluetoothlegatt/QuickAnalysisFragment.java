package com.example.android.bluetoothlegatt;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class QuickAnalysisFragment extends Fragment {

    private static TextView recoveryPercents, avgHRfeed, feedback, statuslbl, statustxt, lblrecoverylevel;
    private static ProgressBar progressBar;
    private ImageButton quickAnalysisButton;

    QuickAnalysisFragmentListener activityCommander;

    public interface QuickAnalysisFragmentListener{
        void startQuickAnalysis();
    }

    public static void fillValues(int _progressBar, String _recoveryPercent, String _avgHR, String _feedback, String _status){
        if (_progressBar >= 65){
            progressBar.getProgressDrawable().setColorFilter(
                    Color.rgb(51,204,0), android.graphics.PorterDuff.Mode.SRC_IN);
            recoveryPercents.setTextColor(Color.parseColor("#33CC00"));
        }else if (_progressBar >= 40  && _progressBar < 65){
            progressBar.getProgressDrawable().setColorFilter(
                    Color.rgb(255,204,0), android.graphics.PorterDuff.Mode.SRC_IN);
            recoveryPercents.setTextColor(Color.parseColor("#FFCC00"));
        }else if(_progressBar >= 8 && _progressBar < 40){
            progressBar.getProgressDrawable().setColorFilter(
                    Color.rgb(255,55,0), android.graphics.PorterDuff.Mode.SRC_IN);
            recoveryPercents.setTextColor(Color.parseColor("#ff3700"));
        }else if(_progressBar < 8){
            progressBar.getProgressDrawable().setColorFilter(
                    Color.rgb(191,0,0), android.graphics.PorterDuff.Mode.SRC_IN);
            recoveryPercents.setTextColor(Color.parseColor("#bf0000"));
        }

        avgHRfeed.setText(_avgHR);
        progressBar.setProgress(_progressBar);
        recoveryPercents.setText(_recoveryPercent);
        feedback.setText(_feedback);
        statustxt.setText(_status);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            activityCommander = (QuickAnalysisFragmentListener) context;
        }catch(ClassCastException e){
            throw new ClassCastException(context.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quick_analysis_fragment, container, false);
        initializeComponents(view);



        quickAnalysisButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        // TODO:
                        // STEP 1: Open the instructions dialog
                        quickAnalysisButtonClicked(v);

                    }

                }
        );


        return view;
    }

    //
    private void quickAnalysisButtonClicked (View view) {
        QuickAnalysisFragmentListener activity = (QuickAnalysisFragmentListener) getActivity();
        activity.startQuickAnalysis();
    }


    private void initializeComponents(View v){
        quickAnalysisButton =(ImageButton) v.findViewById(R.id.quickAnalyzebtn); //est
        recoveryPercents = (TextView) v.findViewById(R.id.recovery_level_percents); //est
        progressBar = (ProgressBar) v.findViewById(R.id.recovery_level_progressBar); //est
        avgHRfeed = (TextView) v.findViewById(R.id.avg_HR); // est
        statuslbl = (TextView) v.findViewById(R.id.lbl_status); //est
        feedback = (TextView) v.findViewById(R.id.recovery_feed); //est
        statustxt = (TextView) v.findViewById(R.id.txt_status); //est
        lblrecoverylevel = (TextView) v.findViewById(R.id.lbl_recovery_level); //est
    }


}
