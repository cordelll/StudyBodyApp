package com.example.android.bluetoothlegatt;


/**
 * Created by cordell on 23.04.2016.
 */
public class Analysis {

    private int _id;
    private int _avgHR;
    private int _minHR;
    private int _maxHR;
    private int _maximalHR;
    private int _restingHR;
    private String _type;
    private String _description;
    private String _startTime;
    private String _endTime;
    private String _longHRflow;
    private String _gender;


    public Analysis(){

    }

    @Override
    public String toString() {
        return "_avgHR: " + _avgHR + "   " +
                "_minHR: " + _minHR + "   " +
                "_maxHR: " + _maxHR + "   " +
                "_type: " + _type + "   " +
                "_description: " + _description + "   " +
                "_startTime: " + _startTime + "   " +
                "_endTime: " + _endTime + "   ";
    }

    public Analysis(String _type, String _description, String _startTime,
                    String _endTime, int _avgHR, int _minHR, int _maxHR,
                    String _longHRflow, int _maximalHR, int _restingHR, String _gender) {
        this._type = _type;
        this._description = _description;
        this._startTime = _startTime;
        this._endTime = _endTime;
        this._avgHR = _avgHR;
        this._minHR = _minHR;
        this._maxHR = _maxHR;
        this._longHRflow = _longHRflow;
        this._maximalHR = _maximalHR;
        this._restingHR = _restingHR;
        this._gender = _gender;
    }

    // Setters
    public void set_id(int _id) {
        this._id = _id;
    }

    public void set_type(String _type) {
        this._type = _type;
    }

    public void set_description(String _description) {
        this._description = _description;
    }

    public void set_startTime(String _startTime) {
        this._startTime = _startTime;
    }

    public void set_endTime(String _endTime) {
        this._endTime = _endTime;
    }

    public void set_gender(String _gender) {
        this._gender = _gender;
    }

    public void set_avgHR(int _avgHR) { this._avgHR = _avgHR; }

    public void set_minHR(int _minHR) { this._minHR = _minHR; }

    public void set_maxHR(int _maxHR) { this._maxHR = _maxHR; }

    public void set_maximalHR(int _maximalHR){ this._maximalHR = _maximalHR; }

    public void set_restingHR(int _restingHR){ this._restingHR = _restingHR; }

    public void set_longHRflow(String _longHRflow) {this._longHRflow = _longHRflow;}


    //Getters
    public int get_id() {
        return _id;
    }

    public String get_type() {
        return _type;
    }

    public String get_description() {
        return _description;
    }

    public String get_startTime() {
        return _startTime;
    }

    public String get_endTime() {
        return _endTime;
    }

    public String get_gender() {
        return _gender;
    }

    public int get_avgHR() {
        return _avgHR;
    }

    public int get_minHR() {
        return _minHR;
    }

    public int get_maxHR() {
        return _maxHR;
    }

    public int get_maximalHR() { return _maximalHR; }

    public int get_restingHR() { return _restingHR; }

    public String get_longHRflow() { return _longHRflow; }
}
