package com.ben.dronecontroller.data_classes;

import java.io.Serializable;

public class DropdownModel implements Serializable {
    public int dropdownImage;
    public String dropdownText;

    public DropdownModel(int dropdownImage, String dropdownText){
        this.dropdownImage = dropdownImage;
        this.dropdownText = dropdownText;
    }

    public int getDropdownImage() {
        return dropdownImage;
    }

    public String getDropdownText() {
        return dropdownText;
    }
}
