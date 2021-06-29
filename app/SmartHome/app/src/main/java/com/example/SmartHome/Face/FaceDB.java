package com.example.SmartHome.Face;

public class FaceDB {
    /**
     * [
     *   {
     *      "Name": "JunHyoung",
     *      "Image_Name": "jun2.png",
     *      "Image_url": "https://junfirstbucket.~~~~"
     *   },
     *   ...
     * ]
     */

    private String Image_Name;
    private String Image_url;
    private String Name;

    // https://cishome.tistory.com/137
    // https://nobase-dev.tistory.com/6
    // https://relz.tistory.com/15

    public FaceDB(String Name, String Image_url) {
        this.Name = Name;
        this.Image_url = Image_url;
    }

    public String getImage_url() {
        return Image_url;
    }

    public void setImage_url(String image_url) {
        Image_url = image_url;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}

