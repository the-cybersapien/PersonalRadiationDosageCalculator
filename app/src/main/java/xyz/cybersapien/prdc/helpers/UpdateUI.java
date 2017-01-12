package xyz.cybersapien.prdc.helpers;

/**
 * Created by ogcybersapien on 1/12/2017.
 * Interface to help update the UI in main activity from the Fragments
 */

public interface UpdateUI {

    void addRads(Double rads);

    void showInternetErrorDialog();

    Double getTotalRads();
}
