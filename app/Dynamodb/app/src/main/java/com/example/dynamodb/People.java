package com.example.dynamodb;

import com.google.gson.annotations.SerializedName;

public class People {
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
//    @SerializedName("Image_Name")  // json 객체 매칭 -> gson 사용
    private String Image_Name;
    private String Image_url;
    private String Name;
    // https://cishome.tistory.com/137
    // https://nobase-dev.tistory.com/6
    // https://relz.tistory.com/15

    public People(String Name, String Image_Name, String Image_url) {
        this.Name = Name;
        this.Image_Name = Image_Name;
        this.Image_url = Image_url;
    }




    public String getImage_Name() {
        return Image_Name;
    }

    public void setImage_Name(String image_Name) {
        Image_Name = image_Name;
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

