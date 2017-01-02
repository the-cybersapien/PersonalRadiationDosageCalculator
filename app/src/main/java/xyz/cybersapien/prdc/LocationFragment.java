package xyz.cybersapien.prdc;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.RadioGroup;
import android.widget.TextView;

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

    private Location nearestLocationOntable;

    private double altitude;
    private double cosmicRadiation;
    private double terrestialRadiation;
    private String address;
    private MainActivity containerActivity;

    private double residenceRadiation;

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

        RadioGroup materialGroup = (RadioGroup) fragmentView.findViewById(R.id.residenceBuildingGroup);
        materialGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioYes:
                        if (residenceRadiation == 0){
                            residenceRadiation = 7;
                            containerActivity.addRads(residenceRadiation);
                            fragmentView.findViewById(R.id.residence_layout).setVisibility(View.VISIBLE);
                            ((TextView)fragmentView.findViewById(R.id.residence_radiation_value)).setText(String.valueOf(residenceRadiation));
                        }
                        break;
                    case R.id.radioNo:
                        if (residenceRadiation == 7){
                            residenceRadiation = 0;
                            containerActivity.addRads(-7d);
                            fragmentView.findViewById(R.id.residence_layout).setVisibility(View.GONE);
                            ((TextView)fragmentView.findViewById(R.id.residence_radiation_value)).setText(String.valueOf(0));
                        }
                        break;
                }
            }
        });
        return fragmentView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ifLocationDialogShown = false;
        altitude = 0;
        cosmicRadiation = 0;
        terrestialRadiation = 0;
        residenceRadiation = 0;
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
        getRads();
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
                }
        }

    }

    private void getRads(){
        GetRadsTask getRadsTask = new GetRadsTask();
        getRadsTask.execute(location);
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

    private class GetRadsTask extends AsyncTask<Location, Void, Double> {

        @Override
        protected Double doInBackground(Location... params) {
            if (!HelperUtils.isInternetConnected(getContext())){
                ((MainActivity)getActivity()).showInternetErrorDialog();
            } else {
                try {
                    Double alt =  HelperUtils.getAltitude(params[0]);
                    return alt;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return 0d;
        }

        @Override
        protected void onPostExecute(Double result) {
            if (result != 0){
                altitude = result;

                Double rads = HelperUtils.getCosmicRaysRads(altitude);
                containerActivity.addRads(rads - cosmicRadiation);
                cosmicRadiation = rads;

                TextView cosmicTextView = (TextView) fragmentView.findViewById(R.id.cosmic_radiation_value);
                cosmicTextView.setText(String.valueOf(cosmicRadiation));
                fragmentView.findViewById(R.id.cosmic_layout).setVisibility(View.VISIBLE);

                nearestLocationOntable = HelperUtils.getNearestTabLocation(location);
                Bundle extraBundles = nearestLocationOntable.getExtras();

                Double terrestialRads = extraBundles.getDouble(HelperUtils.TERRESTRIAL, 0);
                Double cosmogenicRads = extraBundles.getDouble(HelperUtils.COSMOGENIC, 0);
                Double inhalationRads = extraBundles.getDouble(HelperUtils.INHALATION, 0);
                Double ingestionRads = extraBundles.getDouble(HelperUtils.INGESTION, 0);

                TextView terrestialRadsTextView = (TextView) fragmentView.findViewById(R.id.terrestial_radiation_value);
                terrestialRadsTextView.setText(String.valueOf(terrestialRads));
                fragmentView.findViewById(R.id.terrestial_layout).setVisibility(View.VISIBLE);

                TextView cosmogenicRadsTextView = (TextView) fragmentView.findViewById(R.id.cosmogenic_radiation_value);
                cosmogenicRadsTextView.setText(String.valueOf(cosmogenicRads));
                fragmentView.findViewById(R.id.cosmogenic_layout).setVisibility(View.VISIBLE);

                TextView inhalationRadsTextView = (TextView) fragmentView.findViewById(R.id.inhalation_radiation_value);
                inhalationRadsTextView.setText(String.valueOf(inhalationRads));
                fragmentView.findViewById(R.id.inhalation_layout).setVisibility(View.VISIBLE);

                TextView ingestionRadsTextView = (TextView) fragmentView.findViewById(R.id.ingestion_radiation_value);
                ingestionRadsTextView.setText(String.valueOf(ingestionRads));
                fragmentView.findViewById(R.id.ingestion_layout).setVisibility(View.VISIBLE);

                containerActivity.addRads(terrestialRads - terrestialRadiation);
                terrestialRadiation = terrestialRads;
            }
        }
    }

    private class AddressToLocationAsyncTask extends AsyncTask<String, Void, Location> {

        @Override
        protected Location doInBackground(String... params) {
            if (!HelperUtils.isInternetConnected(getContext())){
                ((MainActivity)getActivity()).showInternetErrorDialog();
                return null;
            }
            return HelperUtils.getLocationFromAddress(params[0]);
        }

        @Override
        protected void onPostExecute(Location loc) {
            if (loc != null){
                location = loc;
                getRads();
            }
        }
    }

    private class LocationToAddressASyncTask extends AsyncTask<Location, Void, String> {

        @Override
        protected String doInBackground(Location... params) {
            if (!HelperUtils.isInternetConnected(getContext())){
                ((MainActivity)getActivity()).showInternetErrorDialog();
                return null;
            }
            return HelperUtils.getAddressFromLocation(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            if (!s.isEmpty()){
                address = s;
                EditText searchText = (EditText) fragmentView.findViewById(R.id.locationEditText);
                searchText.setText(address);
            }
        }
    }
}
