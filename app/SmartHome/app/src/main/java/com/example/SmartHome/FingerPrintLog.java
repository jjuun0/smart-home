package com.example.SmartHome;

public class FingerPrintLog {
    /**
     * [
     *   {
     *      "Correct": "True",
     *      "Date": "2021-06-21 14:40:41",
     *      "Confidence" : "135",
     *      "ID": "3",
     *      "Message": "Found a print match!"
     *   },
     *   ...
     * ]
     */

    private String Correct;
    private String Date;
    private String Confidence;
    private String ID;
    private String Message;

    public FingerPrintLog(String Correct, String Date, String Confidence, String ID, String Message) {
        this.Correct = Correct;
        this.Date = Date;
        this.Confidence = Confidence;
        this.ID = ID;
        this.Message = Message;
    }

    public String getCorrect() {
        return Correct;
    }

    public void setCorrect(String correct) {
        Correct = correct;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getConfidence() {
        return Confidence;
    }

    public void setConfidence(String confidence) {
        Confidence = confidence;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
