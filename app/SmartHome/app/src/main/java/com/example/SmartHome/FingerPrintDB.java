package com.example.SmartHome;

public class FingerPrintDB {
    /**
     * [
     *   {
     *      "ID": "3",
     *      "Name": "JunHyoung"
     *   },
     *   ...
     * ]
     */

    private String Name;
    private String ID;

    public FingerPrintDB(String ID, String Name) {
        this.ID = ID;
        this.Name = Name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

}