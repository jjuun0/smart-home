package com.example.SmartHome;

public class LogTable {

    /**
     * [
     *   { 모두 string 으로 반환해준다.
     *     "Date": "2021-04-29 21:04:13",
     *     "Correct": "true",
     *     "Similarity": "99",
     *     "Image_Name": "jun2.jpg"
     *   },
     *   ...
     * ]
     */

    private String Date;
    private String Correct;
    private String Similarity;
    private String Name;

    public LogTable(String Date, String Correct, String Similarity, String Name) {
        this.Date = Date;
        this.Correct = Correct;
        this.Similarity = Similarity;
        this.Name = Name;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getCorrect() {
        return Correct;
    }

    public void setCorrect(String correct) {
        Correct = correct;
    }

    public String getSimilarity() {
        return Similarity;
    }

    public void setSimilarity(String similarity) {
        Similarity = similarity;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
