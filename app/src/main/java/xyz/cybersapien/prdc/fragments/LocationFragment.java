package xyz.cybersapien.prdc.fragments;

import butterknife.BindView;
import butterknife.OnClick;
import xyz.cybersapien.prdc.helpers.*;
import xyz.cybersapien.prdc.*;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import butterknife.ButterKnife;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * A placeholder fragment containing a simple view.
 */
public class LocationFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, LocationListener{

    private static final String LOG_TAG = LocationFragment.class.getName();

    @BindView(R.id.why_location_img) ImageView whyImageButton;
    @BindView(R.id.why_location_textView) TextView whyTextView;

    public static final int LOCATION_CHECK_REQUEST = 101;

    private View fragmentView;
    private GoogleApiClient mGoogleApiClient;
    private Location location;
    private boolean updatedLocation;
    private LocationRequest locationRequest;
    private boolean ifLocationDialogShown;

    private double altitude;
    private Double totalLocationRadiation;
    private String address;
    private UpdateUI updateUI;

    private double residenceRadiation;

    private Context context;

    // Check to see if the why cardView is open or closed
    private boolean isWhyExpanded;


    public LocationFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_location, container, false);

        ButterKnife.bind(this, fragmentView);

        ImageButton addressButton = (ImageButton) fragmentView.findViewById(R.id.search_location);
        addressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchEditTextView = (EditText) fragmentView.findViewById(R.id.locationEditText);
                String ad = searchEditTextView.getText().toString();
                if (ad.isEmpty()){
                    onConnected(null);
                }
                address = ad;
                AddressToLocationAsyncTask addressTask = new AddressToLocationAsyncTask();
                addressTask.execute(address);
            }
        });

        RadioGroup materialGroup = (RadioGroup) fragmentView.findViewById(R.id.residenceBuildingGroup);
        materialGroup.setOnCheckedChangeListener(houseCheckedListener);

        whyImageButton.setImageResource(R.drawable.ic_angle_down);
        isWhyExpanded = false;

        return fragmentView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        ifLocationDialogShown = false;
        altitude = 0;
        residenceRadiation = 0;
        totalLocationRadiation = 0d;

        setRetainInstance(true);
        context = getContext();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        updateUI = (UpdateUI) getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(2000);
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

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
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        location = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        Log.e(LOG_TAG, "onConnected: " + location );
        if (location == null){
            showEnableLocationDialog();
        } else {
            LocationToAddressASyncTask addTask = new LocationToAddressASyncTask();
            Log.d(LOG_TAG, "onConnected: createdAddressTask");
            addTask.execute(location);
            getRads();
        }
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
                break;
        }
    }

    @OnClick(R.id.why_fragment_location)
    public void expandCollapseWhy(){
        if (isWhyExpanded){
            whyTextView.setVisibility(View.GONE);
            whyImageButton.setImageResource(R.drawable.ic_angle_down);
            isWhyExpanded = false;
        } else {
            whyImageButton.setImageResource(R.drawable.ic_angle_up);
            isWhyExpanded = true;
            whyTextView.setVisibility(View.VISIBLE);
        }
    }

    private void getRads(){
        GetRadsTask getRadsTask = new GetRadsTask();
        getRadsTask.execute(location);
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

    public void showEnableLocationDialog(){
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder settingRequestBuilder =
                new LocationSettingsRequest.Builder().addLocationRequest(request);

        settingRequestBuilder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, settingRequestBuilder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                final LocationSettingsStates state = locationSettingsResult.getLocationSettingsStates();

                switch (status.getStatusCode()){
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.d(LOG_TAG, "onResult: RESOLUTION REQUIRED");
                        try{
                            status.startResolutionForResult(getActivity(), 1000);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        if (!updatedLocation){
            updatedLocation = true;
            onConnected(null);
        }
    }

    private RadioGroup.OnCheckedChangeListener houseCheckedListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            TextView radsTextView = (TextView) fragmentView.findViewById(R.id.residence_radiation_value);
            switch (checkedId){
                case R.id.radioYes:
                    if (residenceRadiation == 0){
                        residenceRadiation = HelperUtils.RESIDENT_MATERIAL_RADIATION;
                        updateUI.addRads(residenceRadiation);
                        radsTextView.setText(getString(R.string.additional_radiation_display, HelperUtils.getPreferredValue(residenceRadiation, context)));
                        radsTextView.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.radioNo:
                    if (residenceRadiation == HelperUtils.RESIDENT_MATERIAL_RADIATION){
                        updateUI.addRads(-residenceRadiation);
                        residenceRadiation = 0;
                        radsTextView.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    private class GetRadsTask extends AsyncTask<Location, Void, Double> {

        @Override
        protected Double doInBackground(Location... params) {

            // Check that the Location is not null, else return 0
            if (params.length < 1 || params[0] == null){
                return 0d;
            }

            // Check the Internet Connection
            if (!HelperUtils.isInternetConnected(getContext())){
                updateUI.showInternetErrorDialog();
                this.cancel(true);
            }

            try {
                return HelperUtils.getAltitude(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return 0d;
        }

        @Override
        protected void onPostExecute(Double result) {
            if (result != 0){
                altitude = result;
                double totalRads = 0;

                Double cosmicRadiation = HelperUtils.getCosmicRaysRads(altitude);
                totalRads += cosmicRadiation;

                TextView cosmicTextView = (TextView) fragmentView.findViewById(R.id.cosmic_radiation_value);
                cosmicTextView.setText(getString(R.string.additional_radiation_display, HelperUtils.getPreferredValue(cosmicRadiation, context)));
                fragmentView.findViewById(R.id.cosmic_layout).setVisibility(View.VISIBLE);

                Location nearestLocationOntable = HelperUtils.getNearestTabLocation(location);
                Bundle extraBundles = nearestLocationOntable.getExtras();

                // Get and Set up Terrestial Radiation
                Double terrestialRads = extraBundles.getDouble(HelperUtils.TERRESTRIAL, 0);
                totalRads += terrestialRads;

                TextView terrestialRadsTextView = (TextView) fragmentView.findViewById(R.id.terrestial_radiation_value);
                terrestialRadsTextView.setText(getString(R.string.additional_radiation_display, HelperUtils.getPreferredValue(terrestialRads, context)));
                fragmentView.findViewById(R.id.terrestial_layout).setVisibility(View.VISIBLE);

                // Get and set up Cosmogenic Atmospheric Radiation
                Double cosmogenicRads = extraBundles.getDouble(HelperUtils.COSMOGENIC, 0);
                totalRads += cosmogenicRads;

                TextView cosmogenicRadsTextView = (TextView) fragmentView.findViewById(R.id.cosmogenic_radiation_value);
                cosmogenicRadsTextView.setText(getString(R.string.additional_radiation_display, HelperUtils.getPreferredValue(cosmogenicRads, context)));
                fragmentView.findViewById(R.id.cosmogenic_layout).setVisibility(View.VISIBLE);

                // Get and set up Atmospheric Radiation through inhalation
                Double inhalationRads = extraBundles.getDouble(HelperUtils.INHALATION, 0);
                totalRads += inhalationRads;

                TextView inhalationRadsTextView = (TextView) fragmentView.findViewById(R.id.inhalation_radiation_value);
                inhalationRadsTextView.setText(getString(R.string.additional_radiation_display, HelperUtils.getPreferredValue(inhalationRads, context)));
                fragmentView.findViewById(R.id.inhalation_layout).setVisibility(View.VISIBLE);

                // Get and set up Radiation due to food
                Double ingestionRads = extraBundles.getDouble(HelperUtils.INGESTION, 0);
                totalRads += ingestionRads;

                TextView ingestionRadsTextView = (TextView) fragmentView.findViewById(R.id.ingestion_radiation_value);
                ingestionRadsTextView.setText(getString(R.string.additional_radiation_display, HelperUtils.getPreferredValue(ingestionRads, context)));
                fragmentView.findViewById(R.id.ingestion_layout).setVisibility(View.VISIBLE);

                if (totalLocationRadiation == 0d){
                    updateUI.addRads(totalRads);
                } else {
                    updateUI.addRads(totalRads - totalLocationRadiation);
                }
                totalLocationRadiation = totalRads;
            }
        }
    }

    private class AddressToLocationAsyncTask extends AsyncTask<String, Void, Location> {

        @Override
        protected Location doInBackground(String... params) {
            // Check to ensure that there is an address to get a location from
            if (params.length < 1 || params[0] == null){
                return null;
            }

            // Check the Internet connection
            if (!HelperUtils.isInternetConnected(getContext())){
                updateUI.showInternetErrorDialog();
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
            // Check to ensure that there is a location to get an address from
            if (params.length < 1 || params[0] == null){
                return null;
            }

            // Check the internet connection
            if (!HelperUtils.isInternetConnected(getContext())){
                updateUI.showInternetErrorDialog();
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
