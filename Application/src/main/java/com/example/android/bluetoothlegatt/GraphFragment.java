package com.example.android.bluetoothlegatt;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bluetoothlegatt.dialogs.CompareToAnotherDialog;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.Random;
import android.os.Handler;
import android.widget.Toast;

public class GraphFragment extends Fragment {

    //DataPoint dp = new DataPoint(2,10);
    public int[] arrayOfHR2;
    public DataPoint[] dataPoints;
    Random random = new Random();
    public GraphView line_graph;
    private int[] heart_rate_array;
    private String startDate, endDate;
    public LineGraphSeries<DataPoint> line_series;
    public LineGraphSeries<DataPoint> line_series2;


    private static final String TAG2 = "clickCheck";
     @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.graph_fragment, container, false);

         heart_rate_array = ((ListItemContentandGraph) getActivity()).arrayOfHR;
         startDate = ((ListItemContentandGraph) getActivity()).t_start_time;
         endDate = ((ListItemContentandGraph) getActivity()).t_end_time;

         Log.i(TAG2, "HRArray length: " + heart_rate_array.length + "   " + startDate + "       " + endDate);

         line_graph = (GraphView) view.findViewById(R.id.graph);
         configureGraph(heart_rate_array, startDate, endDate);





       /* LineGraphSeries<DataPoint> line_series =
                new LineGraphSeries<DataPoint>(new DataPoint[] {
                        new DataPoint(0, 66),
                        new DataPoint(1, 78),
                        new DataPoint(2, 90),
                        new DataPoint(3, 120),
                        new DataPoint(4, 88),
                        new DataPoint(5, 55)
                });*/




       /*  for(int i = 0; i<200; i++){;
       //      dataPoints[i] = new DataPoint(i, random.nextInt(220));
       //  }*/



         // set the bound

         // set manual X bounds



         // set the dynamically label

        /*line_graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    return super.formatLabel(value, isValueX);
                } else {
                    // show currency for y values
                    return super.formatLabel(value, isValueX) + " $";
                }
            }
        });*/

       /*  //set the static label
         StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(line_graph);
         staticLabelsFormatter.setHorizontalLabels(new String[] {"Jan", "Feb", "March"});
         //  staticLabelsFormatter.setVerticalLabels(new String[] {"Sun", "Mon", "Tue"});
         line_graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter); */


         // custom paint to make a dotted line
       /* Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setPathEffect(new DashPathEffect(new float[]{8, 5}, 0));
        line_series.setCustomPaint(paint);*/


         // set the radius of data point
       /* line_series.setDrawDataPoints(true);
        line_series.setDataPointsRadius(10);*/


         // set the background color below the line
        /*line_series.setDrawBackground(true);
        line_series.setBackgroundColor(Color.BLUE);*/


         // set the thickness of line
         // line_series.setThickness(20);
         return view;
    }

    private void configureGraph(int[] hr_array, String start_date, String end_date){

        dataPoints = new DataPoint[hr_array.length];
        for(int i = 0; i < hr_array.length; i++){
            dataPoints[i] = new DataPoint(i, hr_array[i]);
        }

        line_series = new LineGraphSeries<DataPoint>(dataPoints);


        //*************************************
        // For comparison

        //**************************************

        line_graph.addSeries(line_series);



        line_graph.getViewport().setXAxisBoundsManual(true);
        line_graph.getViewport().setMinX(0);
        //changable
        line_graph.getViewport().setMaxX(hr_array.length);
        //line_graph.getViewport().

        // set manual Y bounds
        line_graph.getViewport().setYAxisBoundsManual(true);
        line_graph.getViewport().setMinY(0);
        line_graph.getViewport().setMaxY(220);

        line_graph.getViewport().setScrollable(true);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(line_graph);
            staticLabelsFormatter.setHorizontalLabels(new String[]{start_date, end_date});
            staticLabelsFormatter.setVerticalLabels(new String[] {"0", "30", "60", "100", "120", "160", "190", "220" });
        line_graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

    }


    public void addAnotherLine(String hrArray2){
        line_graph.removeSeries(line_series2);
        String[] s = hrArray2.split(", ");
        arrayOfHR2 = new int[s.length]; // here was changed
        //check this out if works from very first HB
        int counter=0;
        for(int curr = 1; curr < s.length - 1; curr++) { // Hmm...
            arrayOfHR2[counter] = Integer.parseInt(s[curr]);
            counter++;
        }

        dataPoints = new DataPoint[arrayOfHR2.length];
        for(int i = 0; i < arrayOfHR2.length; i++){
            dataPoints[i] = new DataPoint(i, arrayOfHR2[i]);
        }

        line_series2 = new LineGraphSeries<DataPoint>(dataPoints);
        line_series2.setColor(0xffffff00);
        line_graph.addSeries(line_series2);

    }
}
