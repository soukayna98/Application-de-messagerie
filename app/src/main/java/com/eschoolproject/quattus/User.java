package com.eschoolproject.quattus;

public class User {


    public String getUsername() {
        return Username;
    }

    public void setUsername(String name) {
        Username = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    

    public User() {


    }

    private  String Username;
    private String Image;
    private String Status;
    private String Image_Thmb;

    public String getImage_Thmb() {
        return Image_Thmb;
    }

    public void setImage_Thmb(String image_Thmb) {
        Image_Thmb = image_Thmb;
    }

    public User(String username, String image, String status, String thumb_Image) {
        Username = username;
        Image = image;
        Status = status;
        Image_Thmb = thumb_Image;
    }


}
