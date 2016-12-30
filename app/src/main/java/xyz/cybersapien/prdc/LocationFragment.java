package xyz.cybersapien.prdc;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import xyz.cybersapien.prdc.helpers.*;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * A placeholder fragment containing a simple view.
 */
public class LocationFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks {

    private static final String LOG_TAG = LocationFragment.class.getName();

    public static final int LOCATION_CHECK_REQUEST = 101;

    private View fragmentView;
    private GoogleApiClient mGoogleApiClient;
    private Location location;
    private boolean ifLocationDialogShown;

    private double altitude;
    private double altitudeCosmic;
    private String address;
    private MainActivity containerActivity;

    public LocationFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_location, container, false);
        ImageButton addressButton = (ImageButton) fragmentView.findViewById(R.id.search_location);
        addressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchEditTextView = (EditText) fragmentView.findViewById(R.id.locationEditText);
                String ad = searchEditTextView.getText().toString();
                if (ad.isEmpty())
                    return;
                address = ad;
                AddressToLocationAsyncTask addressTask = new AddressToLocationAsyncTask();
                addressTask.execute(address);
            }
        });
        return fragmentView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ifLocationDialogShown = false;
        altitude = 0;
        altitudeCosmic = 0;
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        containerActivity = (MainActivity) getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_CHECK_REQUEST);
            return;
        }
        location = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        LocationToAddressASyncTask addTask = new LocationToAddressASyncTask();
        addTask.execute(location);
        checkAltitude();
        Log.d(LOG_TAG, "onConnected: " + location);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_CHECK_REQUEST:
                if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    onConnected(null);
                } else if (!ifLocationDialogShown){
                    showLocationErrorDialog();
                    Log.d(LOG_TAG, "onRequestPermissionsResult: Error getting Permissions!");
                }
        }

    }

    private void checkAltitude(){
        AltitudeAsyncTask altitudeAsyncTask = new AltitudeAsyncTask();
        altitudeAsyncTask.execute(location);
    }

    protected void createLocationRequest(){
        ifLocationDialogShown = true;
        LocationRequest locationRequest = new LocationRequest();

        locationRequest.setInterval(1000000);
        locationRequest.setFastestInterval(100000);

        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    public void showLocationErrorDialog(){
        ifLocationDialogShown = true;
        AlertDialog.Builder errorDialog = new AlertDialog.Builder(getActivity());
        errorDialog.setMessage("Error! Unable to get Location, please Enter Location Manually")
                .setPositiveButton("Give Permissions", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_CHECK_REQUEST);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        errorDialog.create().show();
    }

    private class AltitudeAsyncTask extends AsyncTask<Location, Void, Double> {

        @Override
        protected Double doInBackground(Location... params) {
            try {
                Log.d(LOG_TAG, "doInBackground: " + params[0]);
                Double alt =  HelperUtils.getAltitude(params[0]);
                Log.d(LOG_TAG, "doInBackground: " + alt);
                return alt;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0d;
        }

        @Override
        protected void onPostExecute(Double result) {
            altitude = result;
            Log.d(LOG_TAG, "onPostExecute: " + result);
            Double rads = HelperUtils.getCosmicRaysRads(altitude);
            if (!rads.isNaN()){
                rads -= altitudeCosmic;
                containerActivity.addRads(rads);
                altitudeCosmic = rads;
            } else {
                Snackbar.make(fragmentView, "Error getting data", Snackbar.LENGTH_LONG);
            }
        }
    }

    private class AddressToLocationAsyncTask extends AsyncTask<String, Void, Location> {

        @Override
        protected Location doInBackground(String... params) {
            return HelperUtils.getLocationFromAddress(params[0]);
        }

        @Override
        protected void onPostExecute(Location loc) {
            location = loc;
            checkAltitude();
        }
    }

    private class LocationToAddressASyncTask extends AsyncTask<Location, Void, String> {

        @Override
        protected String doInBackground(Location... params) {
            return HelperUtils.getAddressFromLocation(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            address = s;
            EditText searchText = (EditText) fragmentView.findViewById(R.id.locationEditText);
            searchText.setText(address);
        }
    }
}
