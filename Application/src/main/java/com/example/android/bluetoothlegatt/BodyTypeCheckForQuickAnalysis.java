package com.example.android.bluetoothlegatt;

import android.content.SharedPreferences;

public class BodyTypeCheckForQuickAnalysis {

    private String feed;
    private float stepNum = 5.88f;
    private int percentage;



    BodyTypeCheckForQuickAnalysis(String _gender, String _age, int _athletecism, int _avgHR, int _restHR){
        if (_gender.equals("Male")){
            maleperson(_age, _athletecism, _avgHR, _restHR);
        } else if(_gender.equals("Female")){
            maleperson(_age, _athletecism, _avgHR, _restHR);
        }
    }


    public void maleperson(String m_ageGroup, int m_athletecismLevel, int m_avgHR, int m_restHR){

        int m_difference = m_avgHR - m_restHR;
       // int age = Integer.parseInt(m_ageGroup);
        percentage = Math.round(100 - (m_difference * stepNum));
        if(m_difference <= 1){

            QuickAnalysisFragment.fillValues(100,
                    Integer.toString(100) + " %",
                    "AVG HR: " + m_avgHR,
                    "Your body is fully recovered, you are ready to conquer new peaks",
                    "Finished");

        }else if (m_difference >= 2 && m_difference <= 6){

            QuickAnalysisFragment.fillValues(percentage,
                    Integer.toString(percentage) + " %",
                    "AVG HR: " + m_avgHR,
                    "Good recovery, you may start next training session",
                    "Finished");

        }else if (m_difference <= 10 && m_difference > 6){

            QuickAnalysisFragment.fillValues(percentage,
                    Integer.toString(percentage) + " %",
                    "AVG HR: " + m_avgHR,
                    "You are adapting to the load, but you have not overcame a fatigue",
                    "Finished");

        }else if (m_difference > 10 && m_difference <= 16 ){

            QuickAnalysisFragment.fillValues(percentage,
                    Integer.toString(percentage) + " %",
                    "AVG HR: " + m_avgHR,
                    "High level of fatigue, your body is vulnerable it is better to have a rest, ",
                    "Finished");

        }else if(m_difference > 16){
            QuickAnalysisFragment.fillValues(5,
                    "<" + Integer.toString(5) + " %",
                    "AVG HR: " + m_avgHR,
                    "High level of fatigue, very likely to be injured. You must have a rest",
                    "Finished");
        }





     /*   if(m_difference <= 0){
            // 100% recovered
        }else if (m_difference < 6 && m_difference > 0){
            // Good recovery
        }else if(m_difference > 6 && m_difference <= 10){
            // Adapting, but hasn't fully overcame a fatigue
        }else if(m_difference > 10 && m_difference <=16){
            // High level
        }else if (m_difference > 16){
            // Overtrained
        }*/

      /*  if (age >= 18 && age < 26){
           switch (m_athletecismLevel){
               case 1:

                   break;
               case 2:

                   break;
               case 3:

                   break;
               case 4:

                   break;
               case 5:

                   break;
               case 6:

                   break;
               case 7:

                   break;
               case 0:

                   break;
           }
        }
        else if (age >= 26 && age < 36){

        }
        else if (age >= 36 && age < 46){}
        else if (age >= 46 && age < 56){}
        else if (age >= 56 && age < 66){}
        else if (age > 65){}*/

    }

   // public void femaleperson(String ageGroup, int athletecismLevel, int avgHR){


////    }

}
