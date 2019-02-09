package ua.tripguide.tripguideua.Models;

import android.support.annotation.NonNull;

public class SpinnerList {


    private String spinnerValue;
    private String spinnerName;
    private String spinnerDescription;
    private int spinnerLogo;


    public SpinnerList(String spinnerValue, String spinnerName, String spinnerDescription, int spinnerLogo) {
        this.spinnerValue = spinnerValue;
        this.spinnerName = spinnerName;
        this.spinnerDescription = spinnerDescription;
        this.spinnerLogo = spinnerLogo;
    }

    public String getSpinnerValue() {
        return spinnerValue;
    }

    public void setSpinnerValue(String spinnerValue) {
        this.spinnerValue = spinnerValue;
    }

    public String getSpinnerName() {
        return spinnerName;
    }

    public void setSpinnerName(String spinnerName) {
        this.spinnerName = spinnerName;
    }

    public String getSpinnerDescription() {
        return spinnerDescription;
    }

    public void setSpinnerDescription(String spinnerDescription) {
        this.spinnerDescription = spinnerDescription;
    }

    public int getSpinnerLogo() {
        return spinnerLogo;
    }

    public void setSpinnerLogo(int spinnerLogo) {
        this.spinnerLogo = spinnerLogo;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getSpinnerValue();
    }
}
